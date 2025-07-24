package dev.playerblair.kuro.manga;

import dev.playerblair.kuro.manga.client.MangaClient;
import dev.playerblair.kuro.manga.dto.MangaDto;
import dev.playerblair.kuro.manga.dto.MangaResponse;
import dev.playerblair.kuro.manga.dto.MangaSearchResponse;
import dev.playerblair.kuro.manga.exception.*;
import dev.playerblair.kuro.manga.model.Manga;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
public class MangaService {

    private final MangaClient mangaClient;
    private final MangaRepository mangaRepository;

    public MangaService(MangaClient mangaClient, MangaRepository mangaRepository) {
        this.mangaClient = mangaClient;
        this.mangaRepository = mangaRepository;
    }

    @CircuitBreaker(name = "mangaSearch", fallbackMethod = "fallbackSearch")
    public MangaSearchResponse searchManga(String query, int page) {
        try {
            log.debug("Searching for manga using query:{}, page:{} from Jikan API.", query, page);
            return mangaClient.searchManga(query, page);
        } catch (RestClientException ex) {
            log.error("Error occurred while searching manga with query:{}, page:{} from Jikan API.", query, page);
            throw ex;
        }
    }

    @CircuitBreaker(name = "mangaGet", fallbackMethod = "fallbackGet")
    public MangaResponse getManga(Long malId) {
        try {
            log.debug("Fetching Manga (malID:{}) from Jikan API.", malId);
            return mangaClient.getManga(malId);
        } catch (HttpClientErrorException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();

            if (statusCode == HttpStatus.NOT_FOUND) {
                log.error("Manga (malID:{}) not found.", malId);
                throw new JikanApiMangaNotFoundException(malId);
            } else {
                throw ex;
            }
        } catch (RestClientException ex) {
            log.error("Error occurred while fetching Manga (malID:{}).", malId);
            throw ex;
        }
    }

    public MangaSearchResponse fallbackSearch(String query, int page, Throwable t) {
        log.warn("Circuit breaker fallback triggered for manga search. Query: {}, Page: {}.", query, page);

        handleJikanApiError(t);

        return null;
    }

    public MangaResponse fallbackGet(Long malId, Throwable t) {
        log.warn("Circuit breaker fallback triggered for manga retrieval. malID:{}.", malId);

        handleJikanApiError(t);

        return null;
    }

    public Manga saveManga(Long malId) {
        return mangaRepository.findById(malId).orElseGet(() -> {
            log.info("Manga (malID:{}) not found in local DB. Fetching from Jikan API.", malId);

            Manga saved = mangaRepository.save(toManga(getManga(malId).data()));

            log.debug("Saved Manga (malID:{}) in local DB.", malId);
            return saved;
        });
    }

    // TODO: Refresh Manga

    private void handleJikanApiError(Throwable t) {
        switch (t) {
            case HttpClientErrorException.TooManyRequests rateLimitEx -> {
                String body = rateLimitEx.getResponseBodyAsString();

                if (body.contains("MyAnimeList")) {
                    throw new JikanApiRateLimitException("MyAnimeList is rate-limiting Jikan servers. Please retry after a delay.");
                } else {
                    throw new JikanApiRateLimitException("Jikan API rate limit exceeded. Please retry after a delay.");
                }
            }
            case HttpServerErrorException.InternalServerError internalServerError ->
                    throw new JikanApiInternalServerErrorException("Jikan API encountered an internal error.");
            case HttpServerErrorException.ServiceUnavailable serviceUnavailable ->
                    throw new JikanApiServiceUnavailableException();
            case null, default -> throw new JikanApiException("Unexpected error occurred while accessing Jikan API: " + t.getMessage());
        }
    }

    public MangaDto toMangaDto(Manga manga) {
        return new MangaDto(
                manga.getMalId(),
                manga.getTitle(),
                manga.getTitleEnglish(),
                manga.getType(),
                manga.getChapters(),
                manga.getVolumes(),
                manga.getStatus(),
                manga.getSynopsis(),
                manga.getAuthors(),
                manga.getGenres(),
                manga.getUrl(),
                manga.getImageUrl()
        );
    }

    private Manga toManga(MangaDto mangaDto) {
        return Manga.builder()
                .malId(mangaDto.malId())
                .title(mangaDto.title())
                .titleEnglish(mangaDto.titleEnglish())
                .type(mangaDto.type())
                .chapters(mangaDto.chapters())
                .volumes(mangaDto.volumes())
                .status(mangaDto.status())
                .synopsis(mangaDto.synopsis())
                .authors(mangaDto.authors())
                .genres(mangaDto.genres())
                .url(mangaDto.url())
                .imageUrl(mangaDto.imageUrl())
                .build();
    }
}
