package dev.playerblair.kuro.collection;

import dev.playerblair.kuro.ValidationGroups;
import dev.playerblair.kuro.collection.dto.CollectionEntryDto;
import dev.playerblair.kuro.collection.dto.CollectionEntryRequest;
import dev.playerblair.kuro.collection.dto.MangaCollectionDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.groups.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/manga")
@Validated
@Slf4j
public class CollectionEntryController {

    private final CollectionEntryService collectionEntryService;

    public CollectionEntryController(CollectionEntryService collectionEntryService) {
        this.collectionEntryService = collectionEntryService;
    }

    @GetMapping("/collection")
    public ResponseEntity<List<CollectionEntryDto>> getCollection(Authentication authentication) {
        List<CollectionEntryDto> response = collectionEntryService.getCollection(authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/collection/{id}")
    public ResponseEntity<CollectionEntryDto> getCollectionEntry
            (@PathVariable @Min(1) Long id, Authentication authentication) {
        CollectionEntryDto response = collectionEntryService.getCollectionEntry(id, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{malId}/collection")
    public ResponseEntity<MangaCollectionDto> getMangaCollection
            (@PathVariable @Min(1) Long malId, Authentication authentication) {
        MangaCollectionDto response = collectionEntryService.getMangaCollection(malId, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/collection")
    public ResponseEntity<CollectionEntryDto> createVolumeEntry(
            @Validated({ValidationGroups.Create.class, Default.class}) @RequestBody CollectionEntryRequest request,
            Authentication authentication
    ) {
        CollectionEntryDto response = collectionEntryService.createCollectionEntry(request, authentication);
        URI location = URI.create("/collection/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/collection/{id}")
    public ResponseEntity<Void> updateVolumeEntry(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody CollectionEntryRequest request,
            Authentication authentication
    ) {
        collectionEntryService.updateCollectionEntry(id, request, authentication);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/collection/{id}")
    public ResponseEntity<Void> deleteVolumeEntry
            (@PathVariable @Min(1) Long id, Authentication authentication) {
        collectionEntryService.deleteCollectionEntry(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
