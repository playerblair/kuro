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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@WebMvcTest(MangaController.class)
public class MangaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MangaService mangaService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private JacksonTester<MangaSearchResponse> jsonMangaSearchResponse;

    @Autowired
    private JacksonTester<MangaResponse> jsonMangaResponse;

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
    @WithMockUser
    public void searchManga_shouldReturn200AndResponse() throws Exception {
        // given
        String query = "Test Manga";
        int page = 1;
        given(mangaService.searchManga(query, page)).willReturn(mangaSearchResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/manga/search")
                        .param("query", query)
                        .param("page", String.valueOf(page))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString())
                .isEqualTo(jsonMangaSearchResponse.write(mangaSearchResponse).getJson());
    }

    @Test
    @WithMockUser
    public void searchManga_givenInvalidParams_shouldReturn400AndErrorResponse() throws Exception {
        // given
        String query = "";
        int page = 0;

        // when
        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/manga/search")
                                .param("query", query)
                                .param("page", String.valueOf(page))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("query", "page");
    }

    @Test
    @WithMockUser
    public void searchManga_givenClientFails_shouldReturn5xxAndErrorResponse() throws Exception {
        // given
        String query = "Test Manga";
        int page = 1;
        given(mangaService.searchManga(query, page)).willThrow(new JikanApiException("Unexpected error occurred while accessing Jikan API"));

        // when
        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/manga/search")
                                .param("query", query)
                                .param("page", String.valueOf(page))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).contains("500", "Unexpected error occurred while accessing Jikan API");
    }

    @Test
    public void searchManga_givenUnauthorised_shouldReturn401() throws Exception {
        // given
        String query = "Test Manga";
        int page = 1;
        given(mangaService.searchManga(query, page)).willReturn(mangaSearchResponse);

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
        given(mangaService.getManga(malId)).willReturn(mangaResponse);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/manga/{malId}", malId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString())
                .isEqualTo(jsonMangaResponse.write(mangaResponse).getJson());
    }

    @Test
    @WithMockUser
    public void getManga_givenInvalidId_shouldReturn400AndErrorResponse() throws Exception {
        // given
        Long malId = 0L;

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/manga/{malId}", malId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("malId");
    }

    @Test
    @WithMockUser
    public void getManga_givenMangaNotFound_shouldReturn404AndErrorResponse() throws Exception {
        // given
        Long malId = 3L;
        given(mangaService.getManga(malId)).willThrow(new JikanApiMangaNotFoundException(malId));

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/manga/{malId}", malId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("Manga not found with malID: " + malId);
    }

    @Test
    @WithMockUser
    public void getManga_givenClientFails_shouldReturn5xxAndErrorResponse() throws Exception {
        // given
        Long malId = 1L;
        given(mangaService.getManga(malId)).willThrow(new JikanApiException("Unexpected error occurred while accessing Jikan API"));

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/manga/{malId}", malId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).contains("Unexpected error occurred while accessing Jikan API");
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
