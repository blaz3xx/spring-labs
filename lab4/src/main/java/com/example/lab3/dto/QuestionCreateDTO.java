package com.example.lab3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public class QuestionCreateDTO {

    @NotBlank(message = "Автор не может быть пустым")
    private String author;

    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 5, max = 100, message = "Заголовок должен быть от 5 до 100 символов")
    private String questionTitle;

    @NotBlank(message = "Описание обязательно")
    private String questionDescription;

    private List<String> keywords;

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getQuestionTitle() { return questionTitle; }
    public void setQuestionTitle(String questionTitle) { this.questionTitle = questionTitle; }
    public String getQuestionDescription() { return questionDescription; }
    public void setQuestionDescription(String questionDescription) { this.questionDescription = questionDescription; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords; }
}