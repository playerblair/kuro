package dev.playerblair.kuro.manga.exception;

public class MangaNotFoundException extends RuntimeException {
    public MangaNotFoundException(Long malId) {
        super("MangaList not found with ID: " + malId + ".");
    }
}
