package com.example.lab3.services;

import com.example.lab3.models.Post;
import com.example.lab3.models.Topic;
import com.example.lab3.repositories.PostRepository;
import com.example.lab3.repositories.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        String content = clean(post.getContent(), MAX_CONTENT_LENGTH);
        String author = clean(post.getAuthor(), MAX_AUTHOR_LENGTH);

        if(content == null) {
            return;
        }

        post.setContent(content);
        post.setAuthor(author);

        actionLogger.log("Створення нового допису автором: " + author);
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
