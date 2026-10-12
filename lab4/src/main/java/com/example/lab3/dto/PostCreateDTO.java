package com.example.lab3.dto;

import jakarta.validation.constraints.NotBlank;

public class PostCreateDTO {

    @NotBlank(message = "Ім'я автора обов'язкове")
    private String author;

    @NotBlank(message = "Текст поста не може бути порожнім")
    private String content;

    // Геттеры и сеттеры
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}