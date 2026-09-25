package com.example.lab2.models;

import java.time.LocalDateTime;

public class Topic {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime creationDate;

    public Topic() {
        this.creationDate = LocalDateTime.now();
    }

    public Topic(Long id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
