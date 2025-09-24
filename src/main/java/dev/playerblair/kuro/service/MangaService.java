package dev.playerblair.kuro.service;

import dev.playerblair.kuro.jikan.JikanClient;
import dev.playerblair.kuro.jikan.response.JikanSearchResponse;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.repository.MangaRepository;
import org.springframework.stereotype.Service;

@Service
public class MangaService {

    private final JikanClient jikanClient;

    public MangaService(JikanClient jikanClient) {
        this.jikanClient = jikanClient;
    }

    public JikanSearchResponse searchManga(String query, int page) {
        return jikanClient.searchManga(query, page);
    }

    public Manga getManga(Long malId) {
        return jikanClient.getManga(malId).data();
    }
}
