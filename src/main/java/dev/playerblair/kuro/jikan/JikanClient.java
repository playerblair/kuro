package dev.playerblair.kuro.jikan;

import dev.playerblair.kuro.jikan.response.JikanGetResponse;
import dev.playerblair.kuro.jikan.response.JikanSearchResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/v4/manga")
public interface JikanClient {

    @GetExchange
    JikanSearchResponse searchManga(@RequestParam("q") String query, @RequestParam("page") int page);

    @GetExchange("/{malId}")
    JikanGetResponse getManga(@PathVariable Long malId);
}