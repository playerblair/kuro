package dev.playerblair.kuro.manga;

import dev.playerblair.kuro.manga.client.MangaClient;
import dev.playerblair.kuro.manga.dto.MangaDto;
import dev.playerblair.kuro.manga.dto.MangaResponse;
import dev.playerblair.kuro.manga.dto.MangaSearchResponse;
import dev.playerblair.kuro.manga.dto.Pagination;
import dev.playerblair.kuro.manga.exception.JikanApiMangaNotFoundException;
import dev.playerblair.kuro.manga.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MangaServiceTest {

    @Mock
    private MangaClient mangaClient;

    @Mock
    private MangaRepository mangaRepository;

    @InjectMocks
    private MangaService mangaService;

    private MangaResponse mangaResponse;
    private MangaSearchResponse mangaSearchResponse;
    private Manga manga;

    @BeforeEach
    public void setUp() {
        MangaDto mangaDto = new MangaDto(
                1L,
                "Test Manga",
                "Test Manga",
                MangaType.MANGA,
                100,
                10,
                Status.COMPLETED,
                "test synopsis",
                Set.of(new Author(1L, "Test Author", "http://www.example.com/authors/1")),
                Set.of(Genre.ROMANCE),
                "http://www.example.com/manga/1",
                "http://www.example.com/manga/1/cover"
        );
        mangaResponse = new MangaResponse(mangaDto);
        mangaSearchResponse = new MangaSearchResponse(
                new Pagination(1, false, 1),
                Set.of(mangaDto)
        );
        manga = Manga.builder()
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

    @Test
    public void searchManga_shouldReturnResponse() {
        // given
        String query = "Test Manga";
        int page = 1;
        when(mangaClient.searchManga(query, page)).thenReturn(mangaSearchResponse);

        // when
        MangaSearchResponse response = mangaService.searchManga(query, page);

        // then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(mangaSearchResponse);
        verify(mangaClient).searchManga(query, page);
    }
    
    @Test
    public void searchManga_givenClientFails_shouldThrowException() {
        // given
        String query = "Test Manga";
        int page = 1;
        when(mangaClient.searchManga(query, page)).thenThrow(new RestClientException("API Epic Fail"));

        // when & then
        RestClientException exception = assertThrows(
                RestClientException.class,
                () -> mangaService.searchManga(query, page)
        );
        assertThat("API Epic Fail").isEqualTo(exception.getMessage());
        verify(mangaClient).searchManga(query, page);
    }

    @Test
    public void getManga_shouldReturnResponse() {
        // given
        Long malId = 1L;
        when(mangaClient.getManga(malId)).thenReturn(mangaResponse);

        // when
        MangaResponse response = mangaService.getManga(malId);

        // then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(mangaResponse);
        verify(mangaClient).getManga(malId);
    }

    @Test
    public void getManga_givenMangaNotFound_shouldThrowException() {
        // given
        Long malId = 1L;
        HttpClientErrorException notFoundException = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Manga Not Found");
        when(mangaClient.getManga(malId)).thenThrow(notFoundException);

        // when & then
        assertThrows(
                JikanApiMangaNotFoundException.class,
                () -> mangaService.getManga(malId)
        );
        verify(mangaClient).getManga(malId);
    }

    @Test
    public void getManga_givenClientFails_shouldThrowException() {
        // given
        Long malId = 1L;
        when(mangaClient.getManga(malId)).thenThrow(new RestClientException("API Epic Fail"));

        // when & then
        RestClientException exception = assertThrows(
                RestClientException.class,
                () -> mangaService.getManga(malId)
        );
        assertThat("API Epic Fail").isEqualTo(exception.getMessage());
        verify(mangaClient).getManga(malId);
    }

    @Test
    public void saveManga_shouldReturnManga() {
        // given
        Long malId = 1L;
        when(mangaClient.getManga(malId)).thenReturn(mangaResponse);
        when(mangaRepository.save(any(Manga.class))).thenReturn(manga);

        // when
        Manga savedManga = mangaService.saveManga(malId);

        // then
        assertThat(savedManga).isNotNull();
        assertThat(savedManga).usingRecursiveComparison().isEqualTo(manga);
        verify(mangaClient).getManga(malId);
    }
}
