package dev.playerblair.kuro.list.dto;

import dev.playerblair.kuro.list.ListStatus;
import dev.playerblair.kuro.manga.dto.MangaDto;

import java.util.List;

public record MangaListDto(
        Long id,
        String name,
        String description,
        ListStatus listStatus,
        List<Long> malIds,
        List<MangaDto> manga
) {
}
