package dev.playerblair.kuro.manga.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pagination(
        @JsonProperty("current_page") int currentPage,
        @JsonProperty("has_next") boolean hasNext,
        @JsonProperty("last_visible_page") int lastVisiblePage
) {
}
