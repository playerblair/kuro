package dev.playerblair.kuro.library.exception;

public class MangaEntryNotFoundException extends RuntimeException {
    public MangaEntryNotFoundException(Long malId) {
        super("MangaEntry for Manga (malID:" + malId + ") not found.");
    }
}
