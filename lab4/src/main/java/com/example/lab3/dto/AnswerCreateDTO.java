package com.example.lab3.dto;

import jakarta.validation.constraints.NotBlank;

public class AnswerCreateDTO {

    @NotBlank(message = "Имя автора обязательно")
    private String author;

    @NotBlank(message = "Текст ответа не может быть пустым")
    private String answerText;

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
}