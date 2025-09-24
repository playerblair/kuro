package dev.playerblair.kuro.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Type {
    MANGA("Manga"),
    MANHWA("Manhwa"),
    MANHUA("Manhua"),
    NOVEL("Novel"),
    LIGHT_NOVEL("Light Novel"),
    ONE_SHOT("One-shot"),
    DOUJINSHI("Doujinshi");

    @JsonValue
    private final String label;

    Type(String label) {
        this.label = label;
    }

    public static Type fromString(String value) {
        return switch (value.toLowerCase()) {
            case "manga" -> Type.MANGA;
            case "manhwa" -> Type.MANHWA;
            case "manhua" -> Type.MANHUA;
            case "novel" -> Type.NOVEL;
            case "light novel" -> Type.LIGHT_NOVEL;
            case "one-shot" -> Type.ONE_SHOT;
            case "doujinshi" -> Type.DOUJINSHI;
            default -> throw new IllegalArgumentException("Invalid type: " + value);
        };
    }
}
