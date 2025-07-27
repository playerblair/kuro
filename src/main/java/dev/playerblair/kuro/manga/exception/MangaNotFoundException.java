package dev.playerblair.kuro.manga.exception;

public class MangaNotFoundException extends RuntimeException {
    public MangaNotFoundException(Long malId) {
        super("Manga not found with malID: " + malId + ".");
    }
}
