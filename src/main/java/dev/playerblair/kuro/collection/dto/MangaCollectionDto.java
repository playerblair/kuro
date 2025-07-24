package dev.playerblair.kuro.collection.dto;

import dev.playerblair.kuro.manga.dto.MangaDto;

import java.util.Set;

public record MangaCollectionDto(
        MangaDto manga,
        Set<CollectionEntryShortFormDto> collection
) {
}
