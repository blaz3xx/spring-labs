package com.example.lab3.controllers;

import com.example.lab3.dto.PostCreateDTO;
import com.example.lab3.dto.TopicCreateDTO;
import com.example.lab3.models.Post;
import com.example.lab3.models.Topic;
import com.example.lab3.models.TopicCategory;
import com.example.lab3.repositories.PostRepository;
import com.example.lab3.repositories.TopicRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Форум API", description = "REST API для управління темами та постами")
public class TopicRestController {

    private final TopicRepository topicRepository;
    private final PostRepository postRepository;

    public TopicRestController(TopicRepository topicRepository, PostRepository postRepository) {
        this.topicRepository = topicRepository;
        this.postRepository = postRepository;
    }

    // ================= ТЕМИ (TOPICS) =================

    @Operation(summary = "Отримання списку тем", description = "Підтримує фільтрацію та пагінацію")
    @ApiResponse(responseCode = "200", description = "Успішно")
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics(
            @Parameter(description = "Ключове слово для пошуку") @RequestParam(required = false) String keyword,
            @Parameter(description = "Номер сторінки") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Кількість елементів") @RequestParam(defaultValue = "10") int size) {
        
        List<Topic> topics;
        if (keyword != null && !keyword.isBlank()) {
            topics = topicRepository.searchByKeywordWithPagination(keyword, page, size);
        } else {
            topics = topicRepository.findAllWithPagination(page, size);
        }
        return ResponseEntity.ok(topics);
    }

    @Operation(summary = "Отримати тему за ID")
    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        return topicRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Створити нову тему")
    @ApiResponse(responseCode = "201", description = "Тему створено")
    @PostMapping
    public ResponseEntity<Topic> createTopic(@Valid @RequestBody TopicCreateDTO dto) {
        Topic topic = new Topic(null, dto.getTitle(), dto.getAuthor(), dto.getCategory());
        Topic savedTopic = topicRepository.save(topic);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTopic);
    }

    @Operation(summary = "Часткове оновлення теми (PATCH - RFC 7386)")
    @PatchMapping("/{id}")
    public ResponseEntity<Topic> partialUpdateTopic(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Topic> optionalTopic = topicRepository.findById(id);
        if (optionalTopic.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Topic existingTopic = optionalTopic.get();

        if (updates.containsKey("title")) {
            existingTopic.setTitle((String) updates.get("title"));
        }
        if (updates.containsKey("category")) {
            existingTopic.setCategory(TopicCategory.valueOf((String) updates.get("category")));
        }

        Topic updatedTopic = topicRepository.save(existingTopic);
        return ResponseEntity.ok(updatedTopic);
    }

    @Operation(summary = "Видалити тему")
    @ApiResponse(responseCode = "204", description = "Видалено (No Content)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        if (!topicRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        topicRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ================= ПОСТИ (POSTS) =================

    @Operation(summary = "Отримати всі пости для конкретної теми")
    @ApiResponse(responseCode = "200", description = "Успішно")
    @ApiResponse(responseCode = "404", description = "Тему не знайдено")
    @GetMapping("/{topicId}/posts")
    public ResponseEntity<List<Post>> getPostsByTopic(@PathVariable Long topicId) {
        if (!topicRepository.existsById(topicId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(postRepository.findByTopicId(topicId));
    }

    @Operation(summary = "Додати пост до теми")
    @ApiResponse(responseCode = "201", description = "Пост створено")
    @PostMapping("/{topicId}/posts")
    public ResponseEntity<Post> addPostToTopic(@PathVariable Long topicId, @Valid @RequestBody PostCreateDTO dto) {
        if (!topicRepository.existsById(topicId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Post post = new Post(null, dto.getContent(), dto.getAuthor(), topicId);
        Post savedPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @Operation(summary = "Видалити пост")
    @DeleteMapping("/{topicId}/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long topicId, @PathVariable Long postId) {
        if (!topicRepository.existsById(topicId) || !postRepository.existsById(postId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        postRepository.deleteById(postId);
        return ResponseEntity.noContent().build();
    }
}