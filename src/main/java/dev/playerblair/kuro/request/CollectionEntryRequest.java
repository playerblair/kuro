package dev.playerblair.kuro.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CollectionEntryRequest(
        @NotNull(message = "{not_null.malId}")
        @Min(value = 1, message = "{min.malId}", groups = ValidationGroups.Create.class)
        Long malId,

        @NotBlank(message = "Edition cannot be blank", groups = ValidationGroups.Create.class)
        String edition,

        @NotNull(message = "Volume no. cannot be null")
        @Min(value = 0, message = "Volume no. cannot be less than 0", groups = ValidationGroups.Create.class)
        Integer volumeNumber,

        String notes,

        LocalDate purchaseDate
) {
}
