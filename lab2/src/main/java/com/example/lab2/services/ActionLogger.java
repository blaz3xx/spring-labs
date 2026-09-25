package com.example.lab2.services;

import java.time.LocalDateTime;

public class ActionLogger {
    private final LocalDateTime createdTime;

    public ActionLogger() {
        this.createdTime = LocalDateTime.now();
    }

    public void log(String action) {
        System.out.println("Дія: " + action + " | Логер створено о: " + createdTime);
    }
}
