package dev.playerblair.kuro.manga.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.playerblair.kuro.manga.model.Author;
import dev.playerblair.kuro.manga.model.Genre;
import dev.playerblair.kuro.manga.model.MangaType;
import dev.playerblair.kuro.manga.model.Status;

import java.util.Set;

@JsonDeserialize(using = MangaDtoDeserializer.class)
public record MangaDto(
        Long malId,
        String title,
        String titleEnglish,
        MangaType type,
        Integer chapters,
        Integer volumes,
        Status status,
        String synopsis,
        Set<Author> authors,
        Set<Genre> genres,
        String url,
        String imageUrl
) {
}
