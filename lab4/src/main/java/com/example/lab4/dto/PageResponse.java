package com.example.lab4.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.function.Function;

@Schema(description = "Одна сторінка результатів разом з інформацією про пагінацію")
public record PageResponse<T>(

        @Schema(description = "Елементи поточної сторінки")
        List<T> content,

        @Schema(description = "Номер поточної сторінки, починаючи з 0", example = "0")
        int page,

        @Schema(description = "Максимальна кількість елементів на сторінці", example = "10")
        int size,

        @Schema(description = "Загальна кількість елементів, що відповідають фільтру", example = "3")
        long totalElements,

        @Schema(description = "Загальна кількість сторінок", example = "1")
        int totalPages
) {

    public static <E, T> PageResponse<T> of(List<E> all, int page, int size, Function<E, T> mapper) {
        List<T> content = all.stream()
                .skip((long) page * size)
                .limit(size)
                .map(mapper)
                .toList();
        int totalPages = (int) Math.ceil((double) all.size() / size);
        return new PageResponse<>(content, page, size, all.size(), totalPages);
    }
}
