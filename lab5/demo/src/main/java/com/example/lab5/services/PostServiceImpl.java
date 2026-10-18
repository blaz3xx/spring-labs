package com.example.lab5.services;

import com.example.lab5.dto.PostRequest;
import com.example.lab5.dto.PostResponse;
import com.example.lab5.exceptions.NotFoundException;
import com.example.lab5.models.Post;
import com.example.lab5.repositories.PostRepository;
import com.example.lab5.repositories.TopicRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    public PostServiceImpl(PostRepository postRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
    }

    public List<PostResponse> findByTopic(Long topicId) {
        checkTopicExists(topicId);
        return postRepository.findByTopicId(topicId).stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse findById(Long topicId, Long postId) {
        return PostResponse.from(getPost(topicId, postId));
    }

    public PostResponse create(Long topicId, PostRequest request) {
        checkTopicExists(topicId);
        Post post = new Post(null, request.content().strip(), request.author().strip(), topicId);
        
        Long generatedId = postRepository.create(post);
        return findById(topicId, generatedId);
    }

    public void delete(Long topicId, Long postId) {
        getPost(topicId, postId);
        postRepository.delete(postId); 
    }

    private Post getPost(Long topicId, Long postId) {
        checkTopicExists(topicId);
        return postRepository.read(postId) 
                .filter(post -> post.getTopicId().equals(topicId))
                .orElseThrow(() -> new NotFoundException("Допис з id=" + postId + " у темі з id=" + topicId + " не знайдено"));
    }

    private void checkTopicExists(Long topicId) {
        if (topicRepository.read(topicId).isEmpty()) {
            throw new NotFoundException("Тему з id=" + topicId + " не знайдено");
        }
    }
}