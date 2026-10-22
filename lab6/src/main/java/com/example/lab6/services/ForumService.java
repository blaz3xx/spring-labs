package com.example.lab6.services;

import com.example.lab6.models.Post;
import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;

import java.util.List;
import java.util.Map;

public interface ForumService {
    List<Topic> getTopics(String keyword, TopicCategory category);
    Map<TopicCategory, Long> countTopicsByCategory();
    Map<Long, Long> countPostsByTopic();
    Topic getTopicById(Long id);
    List<Post> getPostsByTopic(Long topicId);
    void createPost(Long topicId, String content, String author);
    void saveTopic(Topic topic);
    void deleteTopic(Long id);
}