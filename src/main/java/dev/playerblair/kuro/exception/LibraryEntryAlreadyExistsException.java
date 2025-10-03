package dev.playerblair.kuro.exception;

public class LibraryEntryAlreadyExistsException extends RuntimeException {
    public LibraryEntryAlreadyExistsException(Long malId) {
        super("LibraryEntry already exists for Manga with malID: " + malId);
    }
}
