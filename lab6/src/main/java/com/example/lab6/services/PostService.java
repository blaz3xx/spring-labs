package com.example.lab6.services;

import com.example.lab6.dto.PostRequest;
import com.example.lab6.dto.PostResponse;

import java.util.List;

public interface PostService {
    List<PostResponse> findByTopic(Long topicId);
    PostResponse findById(Long topicId, Long postId);
    PostResponse create(Long topicId, PostRequest request);
    void delete(Long topicId, Long postId);
    List<PostResponse> search(String text);
    List<PostResponse> findByAuthor(String author);
}
