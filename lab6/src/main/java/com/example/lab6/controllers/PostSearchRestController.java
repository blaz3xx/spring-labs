package com.example.lab6.controllers;

import com.example.lab6.dto.PostResponse;
import com.example.lab6.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Пошук дописів", description = "Пошук дописів у всіх темах форуму")
public class PostSearchRestController {

    private final PostService postService;

    public PostSearchRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/search")
    @Operation(
            summary = "Пошук дописів за текстом",
            description = "Повертає дописи з усіх тем, текст яких містить заданий фрагмент (без урахування регістру), "
                    + "від найновішого. Реалізовано JPQL-запитом з LIKE, оголошеним через @Query.")
    @ApiResponse(responseCode = "200", description = "Список знайдених дописів (може бути порожнім)")
    @ApiResponse(responseCode = "400", description = "Параметр text порожній, відсутній або довший за 100 символів",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public List<PostResponse> search(
            @Parameter(description = "Фрагмент тексту допису, від 1 до 100 символів", example = "spring")
            @RequestParam
            @NotBlank(message = "Текст для пошуку обов'язковий")
            @Size(max = 100, message = "Текст для пошуку має містити не більше 100 символів") String text) {
        return postService.search(text);
    }

    @GetMapping
    @Operation(
            summary = "Дописи автора",
            description = "Повертає всі дописи вказаного автора з усіх тем (без урахування регістру), від найновішого. "
                    + "Запит згенеровано Spring Data за назвою методу findByAuthorIgnoreCaseOrderByCreationDateDesc.")
    @ApiResponse(responseCode = "200", description = "Список дописів автора (може бути порожнім)")
    @ApiResponse(responseCode = "400", description = "Параметр author порожній або відсутній",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    public List<PostResponse> byAuthor(
            @Parameter(description = "Ім'я автора", example = "Аня")
            @RequestParam @NotBlank(message = "Ім'я автора обов'язкове") String author) {
        return postService.findByAuthor(author);
    }
}
