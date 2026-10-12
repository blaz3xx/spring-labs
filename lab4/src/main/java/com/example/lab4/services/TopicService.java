package com.example.lab4.services;

import com.example.lab4.dto.PageResponse;
import com.example.lab4.dto.TopicRequest;
import com.example.lab4.dto.TopicResponse;
import com.example.lab4.exceptions.NotFoundException;
import com.example.lab4.models.Topic;
import com.example.lab4.models.TopicCategory;
import com.example.lab4.repositories.PostRepository;
import com.example.lab4.repositories.TopicRepository;
import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.StringReader;
import java.util.Set;
import java.util.function.UnaryOperator;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final PostRepository postRepository;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    public TopicService(TopicRepository topicRepository,
                        PostRepository postRepository,
                        JsonMapper jsonMapper,
                        Validator validator) {
        this.topicRepository = topicRepository;
        this.postRepository = postRepository;
        this.jsonMapper = jsonMapper;
        this.validator = validator;
    }

    public PageResponse<TopicResponse> findAll(String keyword, TopicCategory category, String author, int page, int size) {
        return PageResponse.of(topicRepository.search(keyword, category, author), page, size, TopicResponse::from);
    }

    public TopicResponse findById(Long id) {
        return TopicResponse.from(getTopic(id));
    }

    public TopicResponse create(TopicRequest request) {
        Topic topic = new Topic(null, request.title(), request.author(), request.category());
        return TopicResponse.from(topicRepository.save(topic));
    }

    public TopicResponse update(Long id, TopicRequest request) {
        Topic topic = getTopic(id);
        topic.setTitle(request.title());
        topic.setAuthor(request.author());
        topic.setCategory(request.category());
        return TopicResponse.from(topicRepository.save(topic));
    }

    public TopicResponse mergePatch(Long id, JsonMergePatch patch) {
        return applyPatch(id, patch::apply);
    }

    public TopicResponse jsonPatch(Long id, JsonPatch patch) {
        return applyPatch(id, current -> patch.apply((JsonStructure) current));
    }

    public void delete(Long id) {
        getTopic(id);
        postRepository.deleteByTopicId(id);
        topicRepository.deleteById(id);
    }

    private TopicResponse applyPatch(Long id, UnaryOperator<JsonValue> patch) {
        TopicRequest current = TopicRequest.from(getTopic(id));
        JsonValue patched = patch.apply(toJson(current));
        TopicRequest updated = fromJson(patched);
        validate(updated);
        return update(id, updated);
    }

    private JsonValue toJson(TopicRequest request) {
        String json = jsonMapper.writeValueAsString(request);
        return Json.createReader(new StringReader(json)).readValue();
    }

    private TopicRequest fromJson(JsonValue json) {
        return jsonMapper.readerFor(TopicRequest.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(json.toString());
    }

    private void validate(TopicRequest request) {
        Set<ConstraintViolation<TopicRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Topic getTopic(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Тему з id=" + id + " не знайдено"));
    }
}
