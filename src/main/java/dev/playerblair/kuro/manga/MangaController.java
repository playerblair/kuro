package dev.playerblair.kuro.manga;

import dev.playerblair.kuro.manga.dto.MangaResponse;
import dev.playerblair.kuro.manga.dto.MangaSearchResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manga")
@Validated
@Slf4j
public class MangaController {

    private final MangaService mangaService;

    public MangaController(MangaService mangaService) {
        this.mangaService = mangaService;
    }

    @GetMapping("/search")
    public ResponseEntity<MangaSearchResponse> searchManga
            (@RequestParam @NotEmpty String query, @RequestParam(required = false, defaultValue = "1") @Min(1) int page) {
        MangaSearchResponse response = mangaService.searchManga(query, page);
        log.info("Found {} search results.", response.data().size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{malId}")
    public ResponseEntity<MangaResponse> getManga(@PathVariable("malId") @Min(1) Long malId) {
        MangaResponse response = mangaService.getManga(malId);
        log.info("Found Manga (malID:{}).", response.data().malId());
        return ResponseEntity.ok(response);
    }
}
