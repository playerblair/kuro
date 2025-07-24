package dev.playerblair.kuro.list;

import dev.playerblair.kuro.list.dto.MangaListDto;
import dev.playerblair.kuro.list.dto.MangaListRequest;
import dev.playerblair.kuro.list.dto.MangaListShortFormDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/manga/lists")
@Validated
@Slf4j
public class MangaListController {

    private final MangaListService mangaListService;

    public MangaListController(MangaListService mangaListService) {
        this.mangaListService = mangaListService;
    }

    @GetMapping
    public ResponseEntity<List<MangaListShortFormDto>> getLists
            (Authentication authentication) {
        List<MangaListShortFormDto> response = mangaListService.getLists(authentication);
        log.info("Fetched {} manga lists.", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MangaListDto> getMangaList
            (@PathVariable @Min(1) Long id, Authentication authentication) {
        MangaListDto response = mangaListService.getMangaList(id, authentication);
        log.info("Fetched MangaList (ID:{}).", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MangaListDto> createMangaList
            (@RequestBody @Valid MangaListRequest request, Authentication authentication) {
        MangaListDto response = mangaListService.createMangaList(request, authentication);
        URI location = URI.create("/manga/lists/" + response.id());
        log.info("Created a MangaList with name:{}.", request.name());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMangaList(
            @PathVariable Long id,
            @RequestBody @Valid MangaListRequest request,
            Authentication authentication
    ) {
        mangaListService.updateMangaList(id, request, authentication);
        log.info("Updated MangaList (ID:{}).", id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMangaList
            (@PathVariable Long id, Authentication authentication) {
        mangaListService.deleteMangaList(id, authentication);
        log.info("Deleted MangaList (ID:{}).", id);
        return ResponseEntity.noContent().build();
    }
}