package dev.playerblair.kuro.exception;

public class LibraryEntryNotFoundException extends RuntimeException {
    public LibraryEntryNotFoundException(Long malId) {
        super("LibraryEntry not found for Manga with malID: " + malId);
    }
}
