package dev.playerblair.kuro.collection.exception;

public class CollectionEntryNotFoundException extends RuntimeException {
    public CollectionEntryNotFoundException(Long id) {
        super("CollectionEntry not found with ID: " + id + ".");
    }
}
