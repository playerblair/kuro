package dev.playerblair.kuro.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Status {
    PUBLISHING("Publishing"),
    COMPLETED("Completed"),
    DISCONTINUED("Discontinued"),
    ON_HIATUS("On Hiatus"),
    UPCOMING("Upcoming");

    @JsonValue
    private final String label;

    Status(String label) {
        this.label = label;
    }

    public static Status fromString(String value) {
        return switch (value.toLowerCase()) {
            case "publishing" -> Status.PUBLISHING;
            case "finished" -> Status.COMPLETED;
            case "discontinued" -> Status.DISCONTINUED;
            case "on hiatus" -> Status.ON_HIATUS;
            case "upcoming" -> Status.UPCOMING;
            default -> throw new IllegalArgumentException("Invalid status: " + value);
        };
    }
}
