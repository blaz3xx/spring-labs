package com.example.lab6.dto;

public record PageLink(String label, String href, boolean current) {

    public static PageLink gap() {
        return new PageLink("…", null, false);
    }
}
