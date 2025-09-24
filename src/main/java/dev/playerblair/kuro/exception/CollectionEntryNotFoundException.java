package dev.playerblair.kuro.exception;

public class CollectionEntryNotFoundException extends RuntimeException {
    public CollectionEntryNotFoundException(Long id) {
        super("CollectionEntry not found with ID: " + id);
    }
}
