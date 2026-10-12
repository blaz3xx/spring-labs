package com.example.lab4.models;

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