package dev.playerblair.kuro.list;

public class MangaListNotFoundException extends RuntimeException {
    public MangaListNotFoundException(Long malId) {
        super("MangaList not found with ID: " + malId + ".");
    }
}
