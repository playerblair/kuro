package dev.playerblair.kuro.collection.dto;

import dev.playerblair.kuro.collection.model.CollectionType;

import java.time.LocalDate;

public record CollectionEntryShortFormDto(
        Long id,
        CollectionType collectionType,
        int volumeNumber,
        String edition,
        String notes,
        LocalDate datePurchased
) {
}
