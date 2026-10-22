package com.example.lab6.dto;

import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Тема форуму, яку повертає сервер")
public record TopicResponse(

        @Schema(description = "Ідентифікатор теми", example = "1")
        Long id,

        @Schema(description = "Назва теми", example = "Як швидко вивчити Spring Boot?")
        String title,

        @Schema(description = "Ім'я автора", example = "Студент")
        String author,

        @Schema(description = "Категорія теми", example = "STUDY")
        TopicCategory category,

        @Schema(description = "Дата і час створення теми", example = "2026-10-13T12:00:00")
        LocalDateTime createdAt
) {

    public static TopicResponse from(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getAuthor(),
                topic.getCategory(),
                topic.getCreationDate()
        );
    }
}
