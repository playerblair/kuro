package dev.playerblair.kuro.manga;

import dev.playerblair.kuro.manga.dto.MangaDto;
import dev.playerblair.kuro.manga.dto.MangaResponse;
import dev.playerblair.kuro.manga.dto.MangaSearchResponse;
import dev.playerblair.kuro.manga.dto.Pagination;
import dev.playerblair.kuro.manga.exception.JikanApiException;
import dev.playerblair.kuro.manga.exception.JikanApiMangaNotFoundException;
import dev.playerblair.kuro.manga.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureJsonTesters
@WebMvcTest(MangaController.class)
public class MangaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MangaService mangaService;

    @Autowired
    private JacksonTester<MangaSearchResponse> jsonMangaSearchResponse;

    @Autowired
    private JacksonTester<MangaResponse> jsonMangaResponse;

    private MangaResponse mangaResponse;
    private MangaSearchResponse mangaSearchResponse;

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
        Manga manga = Manga.builder()
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
    @WithMockUser
    public void searchManga_shouldReturn200AndResponse() throws Exception {
        // given
        String query = "Test Manga";
        int page = 1;
        when(mangaService.searchManga(query, page)).thenReturn(mangaSearchResponse);

        // when & then
        mockMvc.perform(get("/api/manga/search")
                    .param("query", query)
                    .param("page", String.valueOf(page))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonMangaSearchResponse.write(mangaSearchResponse).getJson()));
        verify(mangaService).searchManga(query, page);
    }

    @Test
    @WithMockUser
    public void searchManga_givenInvalidParams_shouldReturn400AndErrorResponse() throws Exception {
        // given
        String query = "";
        int page = 0;

        // when & then
        mockMvc.perform(get("/api/manga/search")
                    .param("query", query)
                    .param("page", String.valueOf(page))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(allOf(containsString("query"), containsString("page"))));
    }

    @Test
    @WithMockUser
    public void searchManga_givenClientFails_shouldReturn5xxAndErrorResponse() throws Exception {
        // given
        String query = "Test Manga";
        int page = 1;
        when(mangaService.searchManga(query, page)).thenThrow(new JikanApiException("Unexpected error occurred while accessing Jikan API"));

        // when & then
        mockMvc.perform(get("/api/manga/search")
                    .param("query", query)
                    .param("page", String.valueOf(page))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(allOf(containsString("500"),
                        containsString("Unexpected error occurred while accessing Jikan API"))));
        verify(mangaService).searchManga(query, page);
    }

    @Test
    public void searchManga_givenUnauthorised_shouldReturn401() throws Exception {
        // given
        String query = "Test Manga";
        int page = 1;

        // when & then
        mockMvc.perform(get("/api/manga/search")
                    .param("query", query)
                    .param("page", String.valueOf(page))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getManga_shouldReturn200AndResponse() throws Exception {
        // given
        Long malId = 1L;
        when(mangaService.getManga(malId)).thenReturn(mangaResponse);

        // when & then
        mockMvc.perform(get("/api/manga/{malId}", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonMangaResponse.write(mangaResponse).getJson()));
        verify(mangaService).getManga(malId);
    }

    @Test
    @WithMockUser
    public void getManga_givenInvalidId_shouldReturn400AndErrorResponse() throws Exception {
        // given
        Long malId = 0L;

        // when & then
        mockMvc.perform(get("/api/manga/{malId}", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("malId")));
    }

    @Test
    @WithMockUser
    public void getManga_givenMangaNotFound_shouldReturn404AndErrorResponse() throws Exception {
        // given
        Long malId = 3L;
        when(mangaService.getManga(malId)).thenThrow(new JikanApiMangaNotFoundException(malId));

        // when & then
        mockMvc.perform(get("/api/manga/{malId}", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Manga not found with malID: " + malId)));
        verify(mangaService).getManga(malId);
    }

    @Test
    @WithMockUser
    public void getManga_givenClientFails_shouldReturn5xxAndErrorResponse() throws Exception {
        // given
        Long malId = 1L;
        when(mangaService.getManga(malId)).thenThrow(new JikanApiException("Unexpected error occurred while accessing Jikan API"));

        // when & then
        mockMvc.perform(get("/api/manga/{malId}", malId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(allOf(containsString("500"),
                        containsString("Unexpected error occurred while accessing Jikan API"))));
        verify(mangaService).getManga(malId);
    }

    @Test
    public void getManga_givenUnauthorised_shouldReturn401() throws Exception {
        // given
        Long malId = 1L;

        // when & then
        mockMvc.perform(get("/api/manga/{malId}", malId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
