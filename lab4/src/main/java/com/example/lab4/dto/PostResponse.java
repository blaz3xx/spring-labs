package com.example.lab4.dto;

import com.example.lab4.models.Post;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Допис у темі, який повертає сервер")
public record PostResponse(

        @Schema(description = "Ідентифікатор допису", example = "1")
        Long id,

        @Schema(description = "Ідентифікатор теми, до якої належить допис", example = "1")
        Long topicId,

        @Schema(description = "Ім'я автора", example = "Викладач")
        String author,

        @Schema(description = "Текст допису", example = "Треба читати офіційну документацію")
        String content,

        @Schema(description = "Дата і час створення допису", example = "2026-10-13T12:00:00")
        LocalDateTime createdAt
) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTopicId(),
                post.getAuthor(),
                post.getContent(),
                post.getCreationDate()
        );
    }
}
