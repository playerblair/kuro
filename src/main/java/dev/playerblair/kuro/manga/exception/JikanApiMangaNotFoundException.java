package dev.playerblair.kuro.manga.exception;

public class JikanApiMangaNotFoundException extends RuntimeException {
    public JikanApiMangaNotFoundException(Long malId) {
        super("Manga not found with malID: " + malId);
    }
}
