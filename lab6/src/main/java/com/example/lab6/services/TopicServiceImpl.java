package com.example.lab6.services;

import com.example.lab6.dto.CategoryStats;
import com.example.lab6.dto.MergeResponse;
import com.example.lab6.dto.PageResponse;
import com.example.lab6.dto.PopularTopicResponse;
import com.example.lab6.dto.TopicRequest;
import com.example.lab6.dto.TopicResponse;
import com.example.lab6.exceptions.NotFoundException;
import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import com.example.lab6.repositories.PostRepository;
import com.example.lab6.repositories.TopicRepository;
import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonPatch;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

@Service
@Transactional(readOnly = true)
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final PostRepository postRepository;
    private final JsonMapper jsonMapper;
    private final Validator validator;

    public TopicServiceImpl(TopicRepository topicRepository,
                            PostRepository postRepository,
                            JsonMapper jsonMapper,
                            Validator validator) {
        this.topicRepository = topicRepository;
        this.postRepository = postRepository;
        this.jsonMapper = jsonMapper;
        this.validator = validator;
    }

    public PageResponse<TopicResponse> findAll(String keyword, TopicCategory category, String author, int page, int size) {
        List<Topic> topics = topicRepository.search(blankToNull(keyword), category, blankToNull(author));
        return PageResponse.of(topics, page, size, TopicResponse::from);
    }

    public TopicResponse findById(Long id) {
        return TopicResponse.from(getTopic(id));
    }

    @Transactional
    public TopicResponse create(TopicRequest request) {
        return TopicResponse.from(topicRepository.save(toEntity(request)));
    }

    @Transactional
    public List<TopicResponse> createBatch(List<TopicRequest> requests) {
        return requests.stream()
                .map(request -> topicRepository.save(toEntity(request)))
                .map(TopicResponse::from)
                .toList();
    }

    @Transactional
    public TopicResponse update(Long id, TopicRequest request) {
        Topic topic = getTopic(id);
        topic.setTitle(request.title().strip());
        topic.setAuthor(request.author().strip());
        topic.setCategory(request.category());
        return TopicResponse.from(topicRepository.save(topic));
    }

    @Transactional
    public TopicResponse mergePatch(Long id, JsonMergePatch patch) {
        return applyPatch(id, patch::apply);
    }

    @Transactional
    public TopicResponse jsonPatch(Long id, JsonPatch patch) {
        return applyPatch(id, current -> patch.apply((JsonStructure) current));
    }

    @Transactional
    public MergeResponse merge(Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            throw new IllegalArgumentException("Тему не можна об'єднати саму з собою");
        }
        Topic source = getTopic(sourceId);
        Topic target = getTopic(targetId);
        int moved = postRepository.moveAll(source, target);
        topicRepository.deleteById(sourceId);
        return new MergeResponse(TopicResponse.from(getTopic(targetId)), sourceId, moved);
    }

    @Transactional
    public void delete(Long id) {
        topicRepository.delete(getTopic(id));
    }

    public List<PopularTopicResponse> findPopular(int minPosts) {
        return topicRepository.findPopular(minPosts).stream()
                .map(PopularTopicResponse::from)
                .toList();
    }

    public List<TopicResponse> findLatest() {
        return topicRepository.findTop5ByOrderByCreationDateDesc().stream()
                .map(TopicResponse::from)
                .toList();
    }

    public List<TopicResponse> findByAuthor(String author) {
        return topicRepository.findByAuthorIgnoreCaseOrderByCreationDateDesc(author.strip()).stream()
                .map(TopicResponse::from)
                .toList();
    }

    public List<CategoryStats> categoryStats() {
        return topicRepository.countByCategoryGrouped();
    }

    private Topic toEntity(TopicRequest request) {
        return new Topic(null, request.title().strip(), request.author().strip(), request.category());
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

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
