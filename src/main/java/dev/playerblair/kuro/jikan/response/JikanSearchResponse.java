package dev.playerblair.kuro.jikan.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import dev.playerblair.kuro.model.Manga;

import java.util.Set;

public record JikanSearchResponse(
        Pagination pagination,
        Set<Manga> data
) {
}
