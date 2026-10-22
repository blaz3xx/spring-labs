package com.example.lab6.services;

import com.example.lab6.dto.CategoryStats;
import com.example.lab6.dto.TopicPostCount;
import com.example.lab6.models.Post;
import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import com.example.lab6.repositories.PostRepository;
import com.example.lab6.repositories.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ForumServiceImpl implements ForumService {

    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int MAX_AUTHOR_LENGTH = 50;

    private final TopicRepository topicRepository;
    private final PostRepository postRepository;
    private final ActionLogger actionLogger;

    public ForumServiceImpl(TopicRepository topicRepository, PostRepository postRepository, ActionLogger actionLogger) {
        this.topicRepository = topicRepository;
        this.postRepository = postRepository;
        this.actionLogger = actionLogger;
    }

    public List<Topic> getTopics(String keyword, TopicCategory category) {
        return topicRepository.search(keyword, category, null).reversed();
    }

    public Map<TopicCategory, Long> countTopicsByCategory() {
        Map<TopicCategory, Long> counts = topicRepository.countByCategoryGrouped().stream()
                .collect(Collectors.toMap(CategoryStats::category, CategoryStats::topics));
        return Arrays.stream(TopicCategory.values())
                .collect(Collectors.toMap(Function.identity(), c -> counts.getOrDefault(c, 0L),
                        (a, b) -> a, LinkedHashMap::new));
    }

    public Map<Long, Long> countPostsByTopic() {
        return postRepository.countPerTopic().stream()
                .collect(Collectors.toMap(TopicPostCount::topicId, TopicPostCount::count));
    }

    public Topic getTopicById(Long id) {
        return topicRepository.findById(id).orElse(null);
    }

    public List<Post> getPostsByTopic(Long topicId) {
        actionLogger.log("Viewing posts of topic " + topicId);
        return postRepository.findByTopic_IdOrderByIdAsc(topicId);
    }

    @Transactional
    public void createPost(Long topicId, String content, String author) {
        String cleanContent = clean(content, MAX_CONTENT_LENGTH);
        String cleanAuthor = clean(author, MAX_AUTHOR_LENGTH);
        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (cleanContent == null || cleanAuthor == null || topic == null) {
            return;
        }
        actionLogger.log("Creating new post by author: " + cleanAuthor);
        topic.addPost(new Post(cleanContent, cleanAuthor));
    }

    @Transactional
    public void saveTopic(Topic topic) {
        actionLogger.log("Saving topic: " + topic.getTitle());
        topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        actionLogger.log("Deleting topic with ID: " + id);
        topicRepository.findById(id).ifPresent(topicRepository::delete);
    }

    private String clean(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }
}
