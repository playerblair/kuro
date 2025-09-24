package dev.playerblair.kuro.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Progress {
    PLANNING("Planning"),
    READING("Reading"),
    FINISHED("Finished"),
    DROPPED("Dropped");

    @JsonValue
    private String label;

    Progress(String label) {
        this.label = label;
    }

    public static Progress fromString(String value) {
        return switch (value.toLowerCase()) {
            case "planning" -> Progress.PLANNING;
            case "reading" -> Progress.READING;
            case "finished" -> Progress.FINISHED;
            case "dropped" -> Progress.DROPPED;
            default -> throw new IllegalArgumentException("Invalid progress: " + value);
        };
    }
}
