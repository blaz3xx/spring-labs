package com.example.lab6.dto;

import com.example.lab6.models.Topic;
import com.example.lab6.models.TopicCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тема разом із кількістю дописів у ній")
public record PopularTopicResponse(

        @Schema(description = "Ідентифікатор теми", example = "1")
        Long id,

        @Schema(description = "Назва теми", example = "Як налаштувати PostgreSQL для Spring Boot")
        String title,

        @Schema(description = "Ім'я автора", example = "Адмін")
        String author,

        @Schema(description = "Категорія теми", example = "STUDY")
        TopicCategory category,

        @Schema(description = "Кількість дописів у темі", example = "4")
        int posts
) {

    public static PopularTopicResponse from(Topic topic) {
        return new PopularTopicResponse(topic.getId(), topic.getTitle(), topic.getAuthor(),
                topic.getCategory(), topic.getPosts().size());
    }
}
