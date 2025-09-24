package dev.playerblair.kuro.dto;

import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.model.Progress;

public record LibraryEntryDto(
        Long id,
        Manga manga,
        Progress progress,
        int chaptersRead,
        int volumesRead,
        int rating,
        String notes
) {
}
