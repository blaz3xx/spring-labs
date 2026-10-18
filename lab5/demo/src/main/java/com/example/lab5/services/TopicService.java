package com.example.lab5.services;

import com.example.lab5.dto.PageResponse;
import com.example.lab5.dto.TopicRequest;
import com.example.lab5.dto.TopicResponse;
import com.example.lab5.models.TopicCategory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;

public interface TopicService {
    PageResponse<TopicResponse> findAll(String keyword, TopicCategory category, String author, int page, int size);
    TopicResponse findById(Long id);
    TopicResponse create(TopicRequest request);
    TopicResponse update(Long id, TopicRequest request);
    TopicResponse mergePatch(Long id, JsonMergePatch patch);
    TopicResponse jsonPatch(Long id, JsonPatch patch);
    void delete(Long id);
}