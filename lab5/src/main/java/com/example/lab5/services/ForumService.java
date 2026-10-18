package com.example.lab5.services;

import com.example.lab5.models.Post;
import com.example.lab5.models.Topic;
import com.example.lab5.models.TopicCategory;

import java.util.List;
import java.util.Map;

public interface ForumService {
    List<Topic> getTopics(String keyword, TopicCategory category);
    Map<TopicCategory, Long> countTopicsByCategory();
    Map<Long, Long> countPostsByTopic();
    Topic getTopicById(Long id);
    List<Post> getPostsByTopic(Long topicId);
    void createPost(Post post);
    void saveTopic(Topic topic);
    void deleteTopic(Long id);
}