package com.example.lab2.services;

import com.example.lab2.models.Post;
import com.example.lab2.models.Topic;
import com.example.lab2.repositories.PostRepository;
import com.example.lab2.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service 
public class ForumService {

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

    public List<Topic> getAllTopics() {
        actionLogger.log("Перегляд списку тем");
        return topicRepository.findAll();
    }

    public Topic getTopicById(Long id) {
        return topicRepository.findById(id).orElse(null);
    }

    public List<Post> getPostsByTopic(Long topicId) {
        actionLogger.log("Перегляд дописів теми " + topicId);
        return postRepository.findByTopicId(topicId);
    }

    public void createPost(Post post) {
        actionLogger.log("Створення нового допису автором: " + post.getAuthor());
        postRepository.save(post);
    }

    public void saveTopic(Topic topic) {
        actionLogger.log("Збереження теми: " + topic.getTitle());
        topicRepository.save(topic);
    }

    public void deleteTopic(Long id) {
        actionLogger.log("Видалення теми з ID: " + id);
        topicRepository.deleteById(id);
    }
}
