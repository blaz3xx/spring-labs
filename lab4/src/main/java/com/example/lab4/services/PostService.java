package com.example.lab4.services;

import com.example.lab4.dto.PostRequest;
import com.example.lab4.dto.PostResponse;
import com.example.lab4.exceptions.NotFoundException;
import com.example.lab4.models.Post;
import com.example.lab4.repositories.PostRepository;
import com.example.lab4.repositories.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    public PostService(PostRepository postRepository, TopicRepository topicRepository) {
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
        return PostResponse.from(postRepository.save(post));
    }

    public void delete(Long topicId, Long postId) {
        getPost(topicId, postId);
        postRepository.deleteById(postId);
    }

    private Post getPost(Long topicId, Long postId) {
        checkTopicExists(topicId);
        return postRepository.findById(postId)
                .filter(post -> post.getTopicId().equals(topicId))
                .orElseThrow(() -> new NotFoundException("Допис з id=" + postId + " у темі з id=" + topicId + " не знайдено"));
    }

    private void checkTopicExists(Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            throw new NotFoundException("Тему з id=" + topicId + " не знайдено");
        }
    }
}
