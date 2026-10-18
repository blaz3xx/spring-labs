package com.example.lab5.services;

import com.example.lab5.models.Post;
import com.example.lab5.models.Topic;
import com.example.lab5.models.TopicCategory;
import com.example.lab5.repositories.PostRepository;
import com.example.lab5.repositories.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
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
        Map<TopicCategory, Long> counts = topicRepository.findAll().stream()
                .collect(Collectors.groupingBy(Topic::getCategory, Collectors.counting()));
        return Arrays.stream(TopicCategory.values())
                .collect(Collectors.toMap(Function.identity(), c -> counts.getOrDefault(c, 0L),
                        (a, b) -> a, LinkedHashMap::new));
    }

    public Map<Long, Long> countPostsByTopic() {
        return postRepository.findAll().stream()
                .collect(Collectors.groupingBy(Post::getTopicId, Collectors.counting()));
    }

    public Topic getTopicById(Long id) {
        return topicRepository.read(id).orElse(null); 
    }

    public List<Post> getPostsByTopic(Long topicId) {
        actionLogger.log("Viewing posts of topic " + topicId);
        return postRepository.findByTopicId(topicId);
    }

    public void createPost(Post post) {
        String content = clean(post.getContent(), MAX_CONTENT_LENGTH);
        String author = clean(post.getAuthor(), MAX_AUTHOR_LENGTH);

        if(content == null) {
            return;
        }

        post.setContent(content);
        post.setAuthor(author);

        actionLogger.log("Creating new post by author: " + author);
        postRepository.create(post); 
    }

    public void saveTopic(Topic topic) {
        actionLogger.log("Saving topic: " + topic.getTitle());
        if (topic.getId() == null) {
            topicRepository.create(topic);
        } else {
            topicRepository.update(topic);
        }
    }

    @Transactional 
    public void deleteTopic(Long id) {
        actionLogger.log("Deleting topic with ID: " + id);
        postRepository.deleteByTopicId(id);
        topicRepository.delete(id); 
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