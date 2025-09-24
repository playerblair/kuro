package dev.playerblair.kuro.request;

import dev.playerblair.kuro.model.Progress;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LibraryEntryRequest(
        @Min(value = 0, groups = ValidationGroups.Create.class) Long malId,
        Progress progress,
        @Min(0) Integer chaptersRead,
        @Min(0)Integer volumesRead,
        @Min(0) @Max(10) Integer rating,
        String notes
) {
}
