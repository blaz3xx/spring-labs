package com.example.lab6.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Результат об'єднання двох тем")
public record MergeResponse(

        @Schema(description = "Тема, до якої перенесено дописи")
        TopicResponse target,

        @Schema(description = "Ідентифікатор видаленої теми-джерела", example = "5")
        Long removedTopicId,

        @Schema(description = "Скільки дописів перенесено", example = "2")
        int movedPosts
) {
}
