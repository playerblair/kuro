package dev.playerblair.kuro.library;

import dev.playerblair.kuro.library.dto.MangaEntryDto;
import dev.playerblair.kuro.library.dto.MangaEntryRequest;
import dev.playerblair.kuro.library.exception.MangaEntryAlreadyExistsException;
import dev.playerblair.kuro.library.exception.MangaEntryNotFoundException;
import dev.playerblair.kuro.library.model.Progress;
import dev.playerblair.kuro.manga.dto.MangaDto;
import dev.playerblair.kuro.manga.exception.JikanApiMangaNotFoundException;
import dev.playerblair.kuro.manga.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@WebMvcTest(LibraryController.class)
public class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibraryService libraryService;

    @Autowired
    private JacksonTester<List<MangaEntryDto>> jsonLibrary;

    @Autowired
    private JacksonTester<MangaEntryDto> jsonMangaEntry;

    @Autowired
    private JacksonTester<MangaEntryRequest> jsonRequest;

    private MangaEntryDto mangaEntryDto;
    private MangaEntryRequest createRequest;
    private MangaEntryRequest updateRequest;

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


        mangaEntryDto = new MangaEntryDto(
                mangaDto,
                Progress.PLANNING,
                100,
                1,
                5,
                "test notes"
        );

        createRequest = new MangaEntryRequest(
                1L,
                Progress.PLANNING,
                100,
                1,
                5,
                "test notes"
        );

        updateRequest = new MangaEntryRequest(
                null,
                Progress.FINISHED,
                100,
                1,
                5,
                "test notes"
        );
    }

    @Test
    @WithMockUser
    public void getLibrary_shouldReturn200AndResponse() throws Exception {
        // given
        when(libraryService.getLibrary(any(Authentication.class))).thenReturn(List.of(mangaEntryDto));

        // when & then
        mockMvc.perform(get("/api/manga/library")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonLibrary.write(List.of(mangaEntryDto)).getJson()));
        verify(libraryService).getLibrary(any(Authentication.class));
    }

    @Test
    public void getLibrary_givenUnauthorized_shouldReturn401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/manga/library")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getMangaEntry_shouldReturn200AndResponse() throws Exception {
        // given
        Long malId = 1L;
        when(libraryService.getMangaEntry(any(Long.class), any(Authentication.class))).thenReturn(mangaEntryDto);

        // when & then
        mockMvc.perform(get("/api/manga/library/{malId}", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonMangaEntry.write(mangaEntryDto).getJson()));
        verify(libraryService).getMangaEntry(any(Long.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void getMangaEntry_givenInvalidParameters_shouldReturn400AndErrorResponse() throws Exception {
        // given
        Long malId = 0L;

        // when & then
        mockMvc.perform(get("/api/manga/library/{malId}", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("malId")));
    }

    @Test
    @WithMockUser
    public void getMangaEntry_givenMangaEntryNotFound_shouldReturn404AndErrorResponse() throws Exception {
        // given
        Long malId = 1L;
        when(libraryService.getMangaEntry(any(Long.class), any(Authentication.class)))
                .thenThrow(new MangaEntryNotFoundException(malId));

        // when & then
        mockMvc.perform(get("/api/manga/library/{malId}", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("MangaEntry for Manga (malID:" + malId + ") not found.")))
                .andReturn().getResponse();
        verify(libraryService).getMangaEntry(any(Long.class), any(Authentication.class));
    }

    @Test
    public void getMangaEntry_givenUnauthorised_shouldReturn401() throws Exception {
        // given
        Long malId = 1L;

        // when & then
        mockMvc.perform(get("/api/manga/library/{malId}", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void createMangaEntry_shouldReturn201AndResponse() throws Exception {
        // given
        when(libraryService.createMangaEntry(any(MangaEntryRequest.class), any(Authentication.class)))
                .thenReturn(mangaEntryDto);

        // when & then
        mockMvc.perform(post("/api/manga/library")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(createRequest).getJson())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonMangaEntry.write(mangaEntryDto).getJson()));
        verify(libraryService).createMangaEntry(any(MangaEntryRequest.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void createMangaEntry_givenMangaAlreadyExists_shouldReturn400AndResponse() throws Exception {
        // given
        when(libraryService.createMangaEntry(any(MangaEntryRequest.class), any(Authentication.class)))
                .thenThrow(new MangaEntryAlreadyExistsException(createRequest.malId()));

        // when & then
        mockMvc.perform(post("/api/manga/library")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(createRequest).getJson())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MangaEntry for Manga (malID:" + createRequest.malId() + ") already exists.")));
        verify(libraryService).createMangaEntry(any(MangaEntryRequest.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void createMangaEntry_givenMangaNotFound_shouldReturn404AndResponse() throws Exception {
        // given
        when(libraryService.createMangaEntry(any(MangaEntryRequest.class), any(Authentication.class)))
                .thenThrow(new JikanApiMangaNotFoundException(createRequest.malId()));

        // when & then
        mockMvc.perform(post("/api/manga/library")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(createRequest).getJson())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Manga not found with malID: " + createRequest.malId())));
        verify(libraryService).createMangaEntry(any(MangaEntryRequest.class), any(Authentication.class));
    }

    @Test
    public void createMangaEntry_givenUnauthorized_shouldReturn401() throws Exception {
        // when & then
        mockMvc.perform(post("/api/manga/library")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(createRequest).getJson())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void updateMangaEntry_shouldReturn204() throws Exception {
        // given
        Long malId = 1L;

        // when & then
        mockMvc.perform(put("/api/manga/library/{malId}", malId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(updateRequest).getJson())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(libraryService).updateMangaEntry(any(Long.class), any(MangaEntryRequest.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void updateMangaEntry_givenMangaEntryNotFound_shouldReturn404AndResponse() throws Exception {
        // given
        Long malId = 1L;
        doThrow(new MangaEntryNotFoundException(malId)).when(libraryService)
                .updateMangaEntry(any(Long.class), any(MangaEntryRequest.class), any(Authentication.class));

        // when & then
        mockMvc.perform(put("/api/manga/library/{malId}", malId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(updateRequest).getJson())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MangaEntry for Manga (malID:" + malId + ") not found.")));
        verify(libraryService).updateMangaEntry(any(Long.class), any(MangaEntryRequest.class), any(Authentication.class));
    }

    @Test
    public void updateMangaEntry_givenUnauthorized_shouldReturn401() throws Exception {
        // given
        Long malId = 1L;

        // when & then
        mockMvc.perform(put("/api/manga/library/{malId}", malId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest.write(updateRequest).getJson())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void deleteMangaEntry_shouldReturn204() throws Exception {
        // given
        Long malId = 1L;

        // when & then
        mockMvc.perform(delete("/api/manga/library/{malId}", malId)
                    .with(csrf()))
                .andExpect(status().isNoContent());
        verify(libraryService).deleteMangaEntry(any(Long.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void deleteMangaEntry_givenMangaEntryNotFound_shouldReturn404AndResponse() throws Exception {
        // given
        Long malId = 1L;
        doThrow(new MangaEntryNotFoundException(malId)).when(libraryService)
                        .deleteMangaEntry(any(Long.class), any(Authentication.class));

        // then & then
        mockMvc.perform(delete("/api/manga/library/{malId}", malId)
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MangaEntry for Manga (malID:" + malId + ") not found.")));
        verify(libraryService).deleteMangaEntry(any(Long.class), any(Authentication.class));
    }

}
