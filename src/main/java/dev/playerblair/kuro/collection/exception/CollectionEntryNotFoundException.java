package dev.playerblair.kuro.collection.exception;

public class CollectionEntryNotFoundException extends RuntimeException {
    public CollectionEntryNotFoundException(Long id) {
        super("CollectionEntry (ID:" + id + ") not found.");
    }
}
