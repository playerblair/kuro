package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.jikan.response.JikanSearchResponse;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.service.MangaService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manga")
@Validated
public class MangaController {

    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @GetMapping("/search")
    public ResponseEntity<JikanSearchResponse> searchManga(
            @NotBlank(message = "{not_blank.query}") @RequestParam String query,
             @Min(value = 1, message = "{min.page}") @RequestParam(required = false, defaultValue = "1") int page) {
        return ResponseEntity.ok(mangaService.searchManga(query, page));
    }

    @GetMapping("/{malId}")
    public ResponseEntity<Manga> getManga(@Min(value = 1, message = "{min.malId}") @PathVariable Long malId) {
        return ResponseEntity.ok(mangaService.getManga(malId));
    }
}
