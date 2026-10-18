package com.example.lab5.services;

import java.time.LocalDateTime;

public class ActionLogger {
    private final LocalDateTime createdTime;

    public ActionLogger() {
        this.createdTime = LocalDateTime.now();
    }

    public void log(String action) {
        System.out.println("Action: " + action + " | Logger created at: " + createdTime);
    }
}
