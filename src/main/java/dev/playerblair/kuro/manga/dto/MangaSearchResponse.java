package dev.playerblair.kuro.manga.dto;

import java.util.Set;

public record MangaSearchResponse(
        Pagination pagination,
        Set<MangaDto> data
) {
}
