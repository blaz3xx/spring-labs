package com.example.lab3.dto;

import com.example.lab3.models.TopicCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TopicCreateDTO {

    @NotBlank(message = "Автор не може бути порожнім")
    private String author;

    @NotBlank(message = "Заголовок обов'язковий")
    @Size(min = 5, max = 100, message = "Заголовок має містити від 5 до 100 символів")
    private String title;

    @NotNull(message = "Категорія обов'язкова")
    private TopicCategory category;

    // Геттеры и сеттеры
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public TopicCategory getCategory() { return category; }
    public void setCategory(TopicCategory category) { this.category = category; }
}