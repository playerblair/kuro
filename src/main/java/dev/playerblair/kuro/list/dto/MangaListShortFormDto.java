package dev.playerblair.kuro.list.dto;

import dev.playerblair.kuro.list.ListStatus;

import java.util.List;

public record MangaListShortFormDto(
        Long id,
        String name,
        String description,
        ListStatus listStatus,
        List<Long> malIds,
        int numberOfEntries
) {
}
