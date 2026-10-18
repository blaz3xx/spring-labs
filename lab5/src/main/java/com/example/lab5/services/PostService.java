package com.example.lab5.services;

import com.example.lab5.dto.PostRequest;
import com.example.lab5.dto.PostResponse;
import java.util.List;

public interface PostService {
    List<PostResponse> findByTopic(Long topicId);
    PostResponse findById(Long topicId, Long postId);
    PostResponse create(Long topicId, PostRequest request);
    void delete(Long topicId, Long postId);
}