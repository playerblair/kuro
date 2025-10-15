package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.dto.LibraryEntryDto;
import dev.playerblair.kuro.dto.SimpleResponse;
import dev.playerblair.kuro.model.LibraryEntry;
import dev.playerblair.kuro.request.LibraryEntryRequest;
import dev.playerblair.kuro.request.ValidationGroups;
import dev.playerblair.kuro.service.LibraryService;
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
@RequestMapping("/api/library")
@Validated
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public ResponseEntity<List<LibraryEntryDto>> getLibrary(Authentication authentication) {
        return ResponseEntity.ok(libraryService.getLibrary(authentication).stream()
                .map(LibraryEntry::toDto)
                .toList());
    }

    @GetMapping("/{malId}")
    public ResponseEntity<LibraryEntryDto> getLibraryEntry(
            @PathVariable @NotNull(message = "{not_null.malId}") @Min(value = 1, message = "{min.malId}") Long malId,
            Authentication authentication) {
        return ResponseEntity.ok(libraryService.getLibraryEntry(malId, authentication).toDto());
    }

    @PostMapping
    public ResponseEntity<LibraryEntryDto> createLibraryEntry(
            @Validated({ValidationGroups.Create.class, Default.class}) @RequestBody LibraryEntryRequest request,
            Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(libraryService.createLibraryEntry(request, authentication).toDto());
    }

    @PatchMapping("/{malId}")
    public ResponseEntity<SimpleResponse> updateLibraryEntry(
            @PathVariable @NotNull(message = "{not_null.malId}") @Min(value = 1, message = "{min.malId}") Long malId,
            @Validated(Default.class) @RequestBody LibraryEntryRequest request,
            Authentication authentication) {
        libraryService.updateLibraryEntry(malId, request, authentication);
        SimpleResponse response = new SimpleResponse("LibraryEntry updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{malId}")
    public ResponseEntity<SimpleResponse> deleteLibraryEntry(
            @PathVariable @NotNull(message = "{not_null.malId}") @Min(value = 1, message = "{min.malId}") Long malId,
            Authentication authentication) {
        libraryService.deleteLibraryEntry(malId, authentication);
        SimpleResponse response = new SimpleResponse("LibraryEntry deleted successfully");
        return ResponseEntity.ok(response);
    }
}
