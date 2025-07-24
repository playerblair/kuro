package dev.playerblair.kuro.library;

import dev.playerblair.kuro.ValidationGroups;
import dev.playerblair.kuro.library.dto.MangaEntryDto;
import dev.playerblair.kuro.library.dto.MangaEntryRequest;
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
@RequestMapping("/api/manga/library")
@Validated
@Slf4j
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public ResponseEntity<List<MangaEntryDto>> getLibrary(Authentication authentication) {
        List<MangaEntryDto> response = libraryService.getLibrary(authentication);
        log.info("Fetched {} manga entries.", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{malId}")
    public ResponseEntity<MangaEntryDto> getMangaEntry(@PathVariable @Min(1) Long malId, Authentication authentication) {
        MangaEntryDto response = libraryService.getMangaEntry(malId, authentication);
        log.info("Fetched MangaEntry for Manga (malId:{}).", malId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MangaEntryDto> createMangaEntry(
            @Validated({ValidationGroups.Create.class, Default.class}) @RequestBody MangaEntryRequest request,
            Authentication authentication
    ) {
        MangaEntryDto response = libraryService.createMangaEntry(request, authentication);
        URI location = URI.create("/manga/library/" + response.manga().malId());
        log.info("Created MangaEntry for Manga (malID:{}).", request.malId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{malId}")
    public ResponseEntity<Void> updateMangaEntry (
            @PathVariable @Min(1) Long malId,
            @Validated({ValidationGroups.Update.class, Default.class}) @RequestBody MangaEntryRequest request,
            Authentication authentication
    ) {
        libraryService.updateMangaEntry(malId, request, authentication);
        log.info("Updated MangaEntry for Manga (malID:{}).", malId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{malId}")
    public ResponseEntity<Void> deleteMangaEntry(@PathVariable @Min(1) Long malId, Authentication authentication) {
        libraryService.deleteMangaEntry(malId, authentication);
        log.info("Deleted MangaEntry for Manga (malID:{}).", malId);
        return ResponseEntity.noContent().build();
    }
}
