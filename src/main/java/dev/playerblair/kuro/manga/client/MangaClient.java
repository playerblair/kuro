package dev.playerblair.kuro.manga.client;

import dev.playerblair.kuro.manga.dto.MangaResponse;
import dev.playerblair.kuro.manga.dto.MangaSearchResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/v4/manga")
public interface MangaClient {

    @GetExchange
    MangaSearchResponse searchManga(@RequestParam("q") String query, @RequestParam("page") int page);

    @GetExchange("/{malId}")
    MangaResponse getManga(@PathVariable Long malId);
}
