package dev.playerblair.kuro.manga.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MangaType {
    MANGA, MANWHA, MANHUA, NOVEL, LIGHT_NOVEL, DOUJINSHI, ONE_SHOT;

    @JsonCreator
    public static MangaType fromString(String key) {
        return switch (key.toLowerCase()) {
            case "manga" -> MANGA;
            case "manwha" -> MANWHA;
            case "manhua" -> MANHUA;
            case "novel" -> NOVEL;
            case "light novel" -> LIGHT_NOVEL;
            case "doujinshi" -> DOUJINSHI;
            case "one-shot" -> ONE_SHOT;
            default -> throw new IllegalArgumentException("Unknown manga type: " + key);
        };
    }
}
