package dev.playerblair.kuro.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CollectionEntryRequest(
        @Min(value = 0, groups = ValidationGroups.Create.class) Long malId,
        @NotBlank(groups = ValidationGroups.Create.class) String edition,
        @Min(value = 0, groups = ValidationGroups.Create.class) Integer volumeNumber,
        String notes,
        LocalDate purchaseDate
) {
}
