package dev.playerblair.kuro.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Genre {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    AVANT_GARDE("Avant Garde"),
    AWARD_WINNING("Award Winning"),
    BOYS_LOVE("Yaoi"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    ECCHI("Ecchi"),
    EROTICA("Erotica"),
    FANTASY("Fantasy"),
    GIRLS_LOVE("Yuri"),
    GOURMET("Gourmet"),
    HENTAI("Hentai"),
    HORROR("Horror"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCI_FI("Sci-fi"),
    SLICE_OF_LIFE("Slice of Life"),
    SPORTS("Sports"),
    SUPERNATURAL("Supernatural"),
    SUSPENSE("Suspense");

    @JsonValue
    private final String label;

    Genre(String label) {
        this.label = label;
    }

    public static Genre fromString(String value) {
        return switch (value.toLowerCase()) {
            case "action" -> ACTION;
            case "adventure" -> ADVENTURE;
            case "avant garde" -> AVANT_GARDE;
            case "award winning" -> AWARD_WINNING;
            case "boys love" -> BOYS_LOVE;
            case "comedy" -> COMEDY;
            case "drama" -> DRAMA;
            case "ecchi" -> ECCHI;
            case "erotica" -> EROTICA;
            case "fantasy" -> FANTASY;
            case "girls love" -> GIRLS_LOVE;
            case "gourmet" -> GOURMET;
            case "hentai" -> HENTAI;
            case "horror" -> HORROR;
            case "mystery" -> MYSTERY;
            case "romance" -> ROMANCE;
            case "sci-fi" -> SCI_FI;
            case "slice of life" -> SLICE_OF_LIFE;
            case "sports" -> SPORTS;
            case "supernatural" -> SUPERNATURAL;
            case "suspense" -> SUSPENSE;
            default -> throw new IllegalArgumentException("Invalid genre: " + value);
        };
    }
}
