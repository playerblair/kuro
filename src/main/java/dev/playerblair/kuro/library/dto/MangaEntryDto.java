package dev.playerblair.kuro.library.dto;

import dev.playerblair.kuro.manga.dto.MangaDto;

import dev.playerblair.kuro.library.model.Progress;

public record MangaEntryDto(
        MangaDto manga,
        Progress progress,
        Integer volumesRead,
        Integer chaptersRead,
        Integer rating,
        String notes
) {
}
