package dev.playerblair.kuro.library.dto;

import dev.playerblair.kuro.library.model.Progress;
import dev.playerblair.kuro.ValidationGroups;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MangaEntryRequest(
        @NotNull(groups = ValidationGroups.Create.class) Long malId,
        Progress progress,
        @Min(0) Integer chaptersRead,
        @Min(0) Integer volumesRead,
        @Min(1) @Max(10) Integer rating,
        String notes
) {
}
