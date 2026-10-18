package com.example.lab5.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Дані нового допису")
public record PostRequest(

        @Schema(description = "Ім'я автора допису, до 50 символів", example = "Аня")
        @NotBlank(message = "Ім'я автора обов'язкове")
        @Size(max = 50, message = "Ім'я автора має містити не більше 50 символів")
        String author,

        @Schema(description = "Текст допису, до 2000 символів", example = "Почніть з офіційного гайду на spring.io")
        @NotBlank(message = "Текст допису обов'язковий")
        @Size(max = 2000, message = "Текст допису має містити не більше 2000 символів")
        String content
) {
}
