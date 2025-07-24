package dev.playerblair.kuro.manga.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    PUBLISHING, COMPLETED, ON_HIATUS, DISCONTINUED, UPCOMING;

    @JsonCreator
    public static Status fromString(String key) {
        return switch (key.toLowerCase()) {
            case "publishing" -> PUBLISHING;
            case "finished" -> COMPLETED;
            case "on hiatus" -> ON_HIATUS;
            case "discontinued" -> DISCONTINUED;
            case "upcoming" -> UPCOMING;
            default -> throw new IllegalArgumentException("Unknown manga status: " + key);
        };
    }
}
