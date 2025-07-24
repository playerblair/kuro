package dev.playerblair.kuro.list.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MangaListRequest(
        @NotEmpty String name,
        String description,
        List<Long> manga
) {
}
