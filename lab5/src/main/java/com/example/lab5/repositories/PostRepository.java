package com.example.lab5.repositories;

import com.example.lab5.models.Post;
import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Long create(Post post);
    Optional<Post> read(Long id);
    int update(Post post);
    int delete(Long id);
    List<Post> findAll();
    List<Post> findByTopicId(Long topicId); 
    int deleteByTopicId(Long topicId);      
}