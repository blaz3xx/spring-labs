package com.example.lab5.repositories;

import com.example.lab5.models.Topic;
import com.example.lab5.models.TopicCategory;
import java.util.List;
import java.util.Optional;

public interface TopicRepository {
    Long create(Topic topic);
    Optional<Topic> read(Long id);
    int update(Topic topic);
    int delete(Long id);
    List<Topic> findAll();

    List<Topic> search(String keyword, TopicCategory category, String author);
}