package dev.playerblair.kuro.collection.exception;

import dev.playerblair.kuro.collection.model.CollectionType;

public class CollectionEntryAlreadyExistsException extends RuntimeException {
    public CollectionEntryAlreadyExistsException
            (Long malId, CollectionType collectionType, int volumeNumber, String edition) {
        super(String.format(
                "CollectionEntry for the '%s' edition of volume #%d of Manga (malID:%d), with type:%s already exists.",
                edition, volumeNumber, malId, collectionType
        ));
    }
}
