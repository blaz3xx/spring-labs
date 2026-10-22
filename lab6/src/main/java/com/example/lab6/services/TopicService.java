package com.example.lab6.services;

import com.example.lab6.dto.CategoryStats;
import com.example.lab6.dto.MergeResponse;
import com.example.lab6.dto.PageResponse;
import com.example.lab6.dto.PopularTopicResponse;
import com.example.lab6.dto.TopicRequest;
import com.example.lab6.dto.TopicResponse;
import com.example.lab6.models.TopicCategory;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;

import java.util.List;

public interface TopicService {
    PageResponse<TopicResponse> findAll(String keyword, TopicCategory category, String author, int page, int size);
    TopicResponse findById(Long id);
    TopicResponse create(TopicRequest request);
    List<TopicResponse> createBatch(List<TopicRequest> requests);
    TopicResponse update(Long id, TopicRequest request);
    TopicResponse mergePatch(Long id, JsonMergePatch patch);
    TopicResponse jsonPatch(Long id, JsonPatch patch);
    MergeResponse merge(Long sourceId, Long targetId);
    void delete(Long id);
    List<PopularTopicResponse> findPopular(int minPosts);
    List<TopicResponse> findLatest();
    List<TopicResponse> findByAuthor(String author);
    List<CategoryStats> categoryStats();
}
