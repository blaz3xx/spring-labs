package com.example.lab6.controllers;

import com.example.lab6.dto.PostRequest;
import com.example.lab6.dto.PostResponse;
import com.example.lab6.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/topics/{topicId}/posts")
@Tag(name = "Дописи", description = "Перегляд, додавання та видалення дописів у темі")
public class PostRestController {

    private final PostService postService;

    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @Operation(
            summary = "Отримати дописи теми",
            description = "Повертає всі дописи вказаної теми, відсортовані за id. Якщо теми немає, повертається 404.")
    @ApiResponse(responseCode = "200", description = "Список дописів повернуто")
    @ApiResponse(responseCode = "404", description = "Тему не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public List<PostResponse> findAll(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long topicId) {
        return postService.findByTopic(topicId);
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "Отримати допис за id",
            description = "Повертає один допис вказаної теми. Якщо тема або допис не існують, "
                    + "або допис належить іншій темі, повертається 404.")
    @ApiResponse(responseCode = "200", description = "Допис знайдено")
    @ApiResponse(responseCode = "404", description = "Тему або допис не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public PostResponse findById(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long topicId,
                                 @Parameter(description = "Ідентифікатор допису", example = "1") @PathVariable Long postId) {
        return postService.findById(topicId, postId);
    }

    @PostMapping
    @Operation(
            summary = "Додати допис до теми",
            description = "Створює новий допис у вказаній темі. У разі успіху повертає створений допис, статус 201 "
                    + "і заголовок Location з адресою нового допису.")
    @ApiResponse(responseCode = "201", description = "Допис створено")
    @ApiResponse(responseCode = "400", description = "Дані не пройшли перевірку або тіло запиту некоректне",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Тему не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<PostResponse> create(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long topicId,
                                               @Valid @RequestBody PostRequest request) {
        PostResponse created = postService.create(topicId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{postId}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Видалити допис",
            description = "Видаляє допис із вказаної теми. У разі успіху повертає 204 без тіла відповіді.")
    @ApiResponse(responseCode = "204", description = "Допис видалено")
    @ApiResponse(responseCode = "404", description = "Тему або допис не знайдено",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public void delete(@Parameter(description = "Ідентифікатор теми", example = "1") @PathVariable Long topicId,
                       @Parameter(description = "Ідентифікатор допису", example = "1") @PathVariable Long postId) {
        postService.delete(topicId, postId);
    }
}
