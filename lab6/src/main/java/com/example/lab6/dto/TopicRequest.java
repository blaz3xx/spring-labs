package com.example.lab6.dto;

import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Дані теми, які надсилає клієнт під час створення або повного оновлення")
public record TopicRequest(

        @Schema(description = "Назва теми, від 5 до 100 символів", example = "Як налаштувати Spring Boot?")
        @NotBlank(message = "Назва теми обов'язкова")
        @Size(min = 5, max = 100, message = "Назва теми має містити від 5 до 100 символів")
        String title,

        @Schema(description = "Ім'я автора теми, до 50 символів", example = "Артем")
        @NotBlank(message = "Ім'я автора обов'язкове")
        @Size(max = 50, message = "Ім'я автора має містити не більше 50 символів")
        String author,

        @Schema(description = "Категорія теми", example = "STUDY")
        @NotNull(message = "Категорія обов'язкова")
        TopicCategory category
) {

    public static TopicRequest from(Topic topic) {
        return new TopicRequest(topic.getTitle(), topic.getAuthor(), topic.getCategory());
    }
}
