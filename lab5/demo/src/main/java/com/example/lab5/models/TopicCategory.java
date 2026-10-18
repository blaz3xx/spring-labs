package com.example.lab5.models;

public enum TopicCategory {
    STUDY("Навчання"),
    GAMES("Ігри"),
    OTHER("Інше");

    private final String label;

    TopicCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}