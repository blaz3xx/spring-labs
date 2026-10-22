package com.example.lab6.dto;

import com.example.lab6.models.TopicCategory;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Кількість тем у категорії")
public record CategoryStats(

        @Schema(description = "Категорія", example = "STUDY")
        TopicCategory category,

        @Schema(description = "Кількість тем у категорії", example = "15")
        Long topics
) {
}
