package dev.playerblair.kuro.dto;

import dev.playerblair.kuro.model.Manga;

import java.time.LocalDate;

public record CollectionEntryDto(
        Long id,
        Manga manga,
        String edition,
        int volumeNumber,
        String notes,
        LocalDate purchaseDate
) {
}
