package com.example.lab4.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
