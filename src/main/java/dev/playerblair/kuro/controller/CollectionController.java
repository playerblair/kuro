package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.dto.CollectionEntryDto;
import dev.playerblair.kuro.dto.SimpleResponse;
import dev.playerblair.kuro.model.CollectionEntry;
import dev.playerblair.kuro.request.CollectionEntryRequest;
import dev.playerblair.kuro.request.ValidationGroups;
import dev.playerblair.kuro.service.CollectionService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
@Validated
public class CollectionController {

    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public ResponseEntity<List<CollectionEntryDto>> getCollection(Authentication authentication) {
        return ResponseEntity.ok(collectionService.getCollection(authentication).stream()
                .map(CollectionEntry::toDto)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionEntryDto> getCollectionEntry(
            @PathVariable @NotNull(message = "{not_null.id}") @Min(value = 1, message = "{min.id}") Long id,
            Authentication authentication) {
        return ResponseEntity.ok(collectionService.getCollectionEntry(id, authentication).toDto());
    }

    @GetMapping("/manga/{malId}")
    public ResponseEntity<List<CollectionEntryDto>> getMangaCollection(
            @PathVariable @NotNull(message = "{not_null.malId}") @Min(value = 1, message = "{min.malId}") Long malId,
            Authentication authentication) {
        return null;
    }

    @PostMapping
    public ResponseEntity<CollectionEntryDto> createCollectionEntry(
            @Validated({ValidationGroups.Create.class, Default.class}) @RequestBody CollectionEntryRequest request,
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(collectionService.createCollectionEntry(request, authentication).toDto());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SimpleResponse> updateCollectionEntry(
            @PathVariable @NotNull(message = "{not_null.id}") @Min(value = 1, message = "{min.id}") Long id,
            @Validated({Default.class}) @RequestBody CollectionEntryRequest request,
            Authentication authentication) {
        collectionService.updateCollectionEntry(id, request, authentication);
        SimpleResponse response = new SimpleResponse("CollectionEntry updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SimpleResponse> deleteCollectionEntry(
            @PathVariable @NotNull(message = "{not_null.id}") @Min(value = 1, message = "{min.id}") Long id,
            Authentication authentication) {
        collectionService.deleteCollectionEntry(id, authentication);
        SimpleResponse response = new SimpleResponse("CollectionEntry deleted successfully");
        return ResponseEntity.ok(response);
    }
}
