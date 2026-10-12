package com.example.lab4.services;

import com.example.lab4.models.Post;
import com.example.lab4.models.Topic;
import com.example.lab4.models.TopicCategory;
import com.example.lab4.repositories.PostRepository;
import com.example.lab4.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ForumService {

    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int MAX_AUTHOR_LENGTH = 50;

    private final TopicRepository topicRepository;

    private PostRepository postRepository;

    @Autowired
    private ActionLogger actionLogger;

    @Autowired
    public ForumService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Autowired
    public void setPostRepository(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Topic> getTopics(TopicCategory category, String keyword) {
        actionLogger.log("Viewing topic list");
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
        return topicRepository.findById(id).orElse(null);
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
        postRepository.save(post);
    }

    public void saveTopic(Topic topic) {
        actionLogger.log("Saving topic: " + topic.getTitle());
        topicRepository.save(topic);
    }

    public void deleteTopic(Long id) {
        actionLogger.log("Deleting topic with ID: " + id);
        postRepository.deleteByTopicId(id);
        topicRepository.deleteById(id);
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
