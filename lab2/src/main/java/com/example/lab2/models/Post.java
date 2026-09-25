package com.example.lab2.models;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private String content;
    private String author;
    private Long topicId;
    private LocalDateTime creationDate;

    public Post() {
        this.creationDate = LocalDateTime.now();
    }

    public Post(Long id, String content, String author, Long topicId) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.topicId = topicId;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
