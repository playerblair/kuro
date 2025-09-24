package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.dto.CollectionEntryDto;
import dev.playerblair.kuro.model.CollectionEntry;
import dev.playerblair.kuro.request.CollectionEntryRequest;
import dev.playerblair.kuro.request.ValidationGroups;
import dev.playerblair.kuro.service.CollectionService;
import lombok.Builder;
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
    public ResponseEntity<CollectionEntryDto> getCollectionEntry
            (@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(collectionService.getCollectionEntry(id, authentication).toDto());
    }

    @GetMapping("/manga/{malId}")
    public ResponseEntity<List<CollectionEntryDto>> getMangaCollection
            (@PathVariable Long malId, Authentication authentication) {
        return null;
    }

    @PostMapping
    public ResponseEntity<CollectionEntryDto> createCollectionEntry(
            @Validated({ValidationGroups.Create.class, Builder.Default.class}) CollectionEntryRequest request,
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(collectionService.createCollectionEntry(request, authentication).toDto());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCollectionEntry(
            @PathVariable Long id,
            @Validated({ValidationGroups.Update.class, Builder.Default.class}) CollectionEntryRequest request,
            Authentication authentication) {
        collectionService.updateCollectionEntry(id, request, authentication);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollectionEntry(@PathVariable Long id, Authentication authentication) {
        collectionService.deleteCollectionEntry(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
