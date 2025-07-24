package dev.playerblair.kuro.manga.model;

public enum Genre {
    ACTION, ADVENTURE, AVANT_GARDE, AWARD_WINNING, BOYS_LOVE, COMEDY,
    DRAMA, ECCHI, EROTICA, FANTASY, GIRLS_LOVE, GOURMET, HENTAI, HORROR,
    MYSTERY, ROMANCE, SCI_FI, SLICE_OF_LIFE, SPORTS, SUPERNATURAL, SUSPENSE;

    public static Genre fromString(String key) {
        return switch (key.toLowerCase()) {
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
            default -> throw new IllegalArgumentException("Unknown manga genre: " + key);
        };
    }
}