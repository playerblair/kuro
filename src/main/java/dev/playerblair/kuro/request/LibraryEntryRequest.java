package dev.playerblair.kuro.request;

import dev.playerblair.kuro.model.Progress;
import jakarta.validation.constraints.*;

public record LibraryEntryRequest(
        @NotNull(message = "{not_null.malId}")
        @Min(value = 1, message = "{min.malId}", groups = ValidationGroups.Create.class)
        Long malId,

        @NotNull(message = "Progress cannot be null")
        Progress progress,

        @Min(value = 0, message = "Chapters read cannot be less than 0")
        Integer chaptersRead,

        @Min(value = 0, message = "Volumes read cannot be less than 0")
        Integer volumesRead,

        @Min(value = 1, message = "Rating cannot be lower than 1")
        @Max(value = 10, message = "Rating cannot be greater than 10")
        Integer rating,

        String notes
) {
}
