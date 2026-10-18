package com.example.lab5.models;

import java.time.LocalDateTime;

public class Topic {
    private Long id;
    private String title;
    private String author;
    private TopicCategory category;
    private LocalDateTime creationDate;

    public Topic() {
        this.category = TopicCategory.OTHER;
        this.creationDate = LocalDateTime.now();
    }

    public Topic(Long id, String title, String author) {
        this(id, title, author, TopicCategory.OTHER);
    }

    public Topic(Long id, String title, String author, TopicCategory category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public TopicCategory getCategory() {
        return category;
    }

    public void setCategory(TopicCategory category) {
        this.category = category;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}