package dev.playerblair.kuro.collection.dto;

import dev.playerblair.kuro.ValidationGroups;
import dev.playerblair.kuro.collection.model.CollectionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record CollectionEntryRequest(
        @NotNull(groups = ValidationGroups.Create.class) Long malId,
        @NotNull(groups = ValidationGroups.Create.class) CollectionType type,
        @NotNull(groups = ValidationGroups.Create.class) @Positive(groups = ValidationGroups.Create.class) int volumeNumber,
        String edition,
        String notes,
        LocalDate datePurchased
) {
}
