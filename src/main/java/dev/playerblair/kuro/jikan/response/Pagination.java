package dev.playerblair.kuro.jikan.response;

import com.fasterxml.jackson.annotation.JsonSetter;

public record Pagination(
        @JsonSetter("last_visible_page") int lastVisiblePage,
        @JsonSetter("has_next_page") boolean hasNextPage,
        @JsonSetter("current_page") int currentPage
) {
}
