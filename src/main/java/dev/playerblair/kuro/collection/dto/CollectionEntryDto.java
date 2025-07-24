package dev.playerblair.kuro.collection.dto;

import dev.playerblair.kuro.collection.model.CollectionType;
import dev.playerblair.kuro.manga.dto.MangaDto;

import java.time.LocalDate;

public record CollectionEntryDto(
        Long id,
        MangaDto manga,
        CollectionType collectionType,
        int volumeNumber,
        String edition,
        String notes,
        LocalDate datePurchased
) {
}
