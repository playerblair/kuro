package dev.playerblair.kuro.library.exception;

public class MangaEntryAlreadyExistsException extends RuntimeException {
    public MangaEntryAlreadyExistsException(Long malId) {
        super("MangaEntry for Manga (malID:" + malId + ") already exists.");
    }
}
