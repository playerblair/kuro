package dev.playerblair.kuro.collection;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.collection.dto.CollectionEntryDto;
import dev.playerblair.kuro.collection.dto.CollectionEntryRequest;
import dev.playerblair.kuro.collection.dto.CollectionEntryShortFormDto;
import dev.playerblair.kuro.collection.dto.MangaCollectionDto;
import dev.playerblair.kuro.collection.exception.CollectionEntryAlreadyExistsException;
import dev.playerblair.kuro.collection.exception.CollectionEntryNotFoundException;
import dev.playerblair.kuro.collection.model.CollectionEntry;
import dev.playerblair.kuro.collection.model.CollectionType;
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
@WebMvcTest(CollectionEntryController.class)
public class CollectionEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CollectionEntryService collectionEntryService;

    @Autowired
    private JacksonTester<List<CollectionEntryDto>> jsonCollection;

    @Autowired
    private JacksonTester<CollectionEntryDto> jsonCollectionEntry;

    @Autowired
    private JacksonTester<MangaCollectionDto> jsonMangaCollection;

    @Autowired
    private JacksonTester<CollectionEntryRequest> jsonRequest;

    private CollectionEntryDto collectionEntryDto;
    private MangaCollectionDto mangaCollectionDto;
    private CollectionEntryRequest createRequest;
    private CollectionEntryRequest updateRequest;

    @BeforeEach
    public void setUp() {
        User testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role("USER")
                .build();

        Manga manga = Manga.builder()
                .malId(1L)
                .title("Test Manga")
                .titleEnglish("Test Manga")
                .type(MangaType.MANGA)
                .chapters(100)
                .volumes(10)
                .status(Status.COMPLETED)
                .synopsis("test synopsis")
                .authors(Set.of(new Author(1L, "Test Author", "http://www.example.com/authors/1")))
                .genres(Set.of(Genre.ROMANCE))
                .url("http://www.example.com/manga/1")
                .imageUrl("http://www.example.com/manga/1/cover")
                .build();

        MangaDto mangaDto = new MangaDto(
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

        CollectionEntry collectionEntry = CollectionEntry.builder()
                .id(1L)
                .user(testUser)
                .manga(manga)
                .type(CollectionType.PHYSICAL)
                .volumeNumber(1)
                .edition("Standard")
                .notes("test notes")
                .build();

        collectionEntryDto = new CollectionEntryDto(
                collectionEntry.getId(),
                mangaDto,
                collectionEntry.getType(),
                collectionEntry.getVolumeNumber(),
                collectionEntry.getEdition(),
                collectionEntry.getNotes(),
                collectionEntry.getDatePurchased()
        );

        CollectionEntryShortFormDto shortFormDto = new CollectionEntryShortFormDto(
                collectionEntry.getId(),
                collectionEntry.getType(),
                collectionEntry.getVolumeNumber(),
                collectionEntry.getEdition(),
                collectionEntry.getNotes(),
                collectionEntry.getDatePurchased()
        );

        mangaCollectionDto = new MangaCollectionDto(
                mangaDto,
                Set.of(shortFormDto)
        );

        createRequest = new CollectionEntryRequest(
                1L,
                CollectionType.PHYSICAL,
                1,
                "Standard",
                "test notes",
                null
        );

        updateRequest = new CollectionEntryRequest(
                null,
                null,
                0,
                "Standard",
                "destroyed by spilled water",
                null
        );
    }

    @Test
    @WithMockUser
    public void getCollection_shouldReturn200AndResponse() throws Exception {
        // given
        String expectedJson = jsonCollection.write(List.of(collectionEntryDto)).getJson();
        when(collectionEntryService.getCollection(any(Authentication.class)))
                .thenReturn(List.of(collectionEntryDto));

        // when & then
        mockMvc.perform(get("/api/manga/collection")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(collectionEntryService).getCollection(any(Authentication.class));
    }

    @Test
    public void getCollection_givenUnauthorized_shouldReturn401() throws Exception {
        // when & then
        mockMvc.perform(get("/api/manga/collection")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getMangaCollection_shouldReturn200AndResponse() throws Exception {
        // given
        Long malId = 1L;
        String expectedJson = jsonMangaCollection.write(mangaCollectionDto).getJson();
        when(collectionEntryService.getMangaCollection(any(Long.class), any(Authentication.class)))
                .thenReturn(mangaCollectionDto);

        // when & then
        mockMvc.perform(get("/api/manga/{malId}/collection", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(collectionEntryService).getMangaCollection(any(Long.class), any(Authentication.class));
    }

    @Test
    public void getMangaCollection_givenUnauthorized_shouldReturn401() throws Exception {
        // given
        Long malId = 1L;

        // when & then
        mockMvc.perform(get("/api/manga/{malId}/collection", malId)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getCollectionEntry_shouldReturn200AndResponse() throws Exception {
        // given
        Long id = 1L;
        String expectedJson = jsonCollectionEntry.write(collectionEntryDto).getJson();
        when(collectionEntryService.getCollectionEntry(any(Long.class), any(Authentication.class)))
                .thenReturn(collectionEntryDto);

        // when & then
        mockMvc.perform(get("/api/manga/collection/{id}", id)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
        verify(collectionEntryService).getCollectionEntry(any(Long.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void getCollectionEntry_givenCollectionEntryNotFound_shouldReturn404AndErrorResponse() throws Exception {
        // given
        Long id = 1L;
        when(collectionEntryService.getCollectionEntry(any(Long.class), any(Authentication.class)))
                .thenThrow(new CollectionEntryNotFoundException(id));

        // when & then
        mockMvc.perform(get("/api/manga/collection/{id}", id)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("CollectionEntry (ID:" + id + ") not found.")));
        verify(collectionEntryService).getCollectionEntry(any(Long.class), any(Authentication.class));
    }

    @Test
    public void getCollectionEntry_givenUnauthorized_shouldReturn401() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(get("/api/manga/collection/{id}", id)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void createCollectionEntry_shouldReturn201AndResponse() throws Exception {
        // given
        String requestJson = jsonRequest.write(createRequest).getJson();
        String responseJson = jsonCollectionEntry.write(collectionEntryDto).getJson();
        when(collectionEntryService.createCollectionEntry(any(CollectionEntryRequest.class), any(Authentication.class)))
                .thenReturn(collectionEntryDto);

        // when & then
        mockMvc.perform(post("/api/manga/collection")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson));
        verify(collectionEntryService).createCollectionEntry(any(CollectionEntryRequest.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void createCollectionEntry_givenCollectionEntryAlreadyExists_shouldReturn400AndErrorResponse() throws Exception {
        // given
        String requestJson = jsonRequest.write(createRequest).getJson();
        when(collectionEntryService.createCollectionEntry(any(CollectionEntryRequest.class), any(Authentication.class)))
                .thenThrow(new CollectionEntryAlreadyExistsException(
                        createRequest.malId(),
                        createRequest.type(),
                        createRequest.volumeNumber(),
                        createRequest.edition()));

        // when & then
        mockMvc.perform(post("/api/manga/collection")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(String.format(
                        "CollectionEntry for the '%s' edition of volume #%d of Manga (malID:%d), with type:%s already exists.",
                        createRequest.edition(), createRequest.volumeNumber(), createRequest.malId(), createRequest.type()))));
        verify(collectionEntryService).createCollectionEntry(any(CollectionEntryRequest.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void createCollectionEntry_givenMangaNotFound_shouldReturn404AndErrorResponse() throws Exception {
        // given
        String requestJson = jsonRequest.write(createRequest).getJson();
        when(collectionEntryService.createCollectionEntry(any(CollectionEntryRequest.class), any(Authentication.class)))
                .thenThrow(new JikanApiMangaNotFoundException(createRequest.malId()));

        // when & then
        mockMvc.perform(post("/api/manga/collection")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Manga not found with malID: " + createRequest.malId())));
        verify(collectionEntryService).createCollectionEntry(any(CollectionEntryRequest.class), any(Authentication.class));
    }

    @Test
    public void createCollectionEntry_givenUnauthorized_shouldReturn401() throws Exception {
        // given
        String requestJson = jsonRequest.write(createRequest).getJson();

        // when & then
        mockMvc.perform(post("/api/manga/collection")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void updateCollectionEntry_shouldReturn204() throws Exception {
        // given
        Long id = 1L;
        String requestJson = jsonRequest.write(updateRequest).getJson();

        // when & then
        mockMvc.perform(put("/api/manga/collection/{id}", id)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(collectionEntryService)
                .updateCollectionEntry(any(Long.class), any(CollectionEntryRequest.class), any(Authentication.class));
    }

    @Test
    @WithMockUser
    public void updateCollectionEntry_givenCollectionEntryNotFound_shouldReturn404AndErrorResponse() throws Exception {
        // given
        Long id = 1L;
        String requestJson = jsonRequest.write(updateRequest).getJson();
        doThrow(new CollectionEntryNotFoundException(id)).when(collectionEntryService).
                updateCollectionEntry(any(Long.class), any(CollectionEntryRequest.class), any(Authentication.class));

        // when & then
        mockMvc.perform(put("/api/manga/collection/{id}", id)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("CollectionEntry (ID:" + id + ") not found.")));
        verify(collectionEntryService)
                .updateCollectionEntry(any(Long.class), any(CollectionEntryRequest.class), any(Authentication.class));
    }

    @Test
    public void updateCollectionEntry_givenUnauthorized_shouldReturn401() throws Exception {
        // given
        Long id = 1L;
        String requestJson = jsonRequest.write(updateRequest).getJson();

        // when & then
        mockMvc.perform(put("/api/manga/collection/{id}", id)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void deleteCollectionEntry_shouldReturn204() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(delete("/api/manga/collection/{id}", id)
                    .with(csrf()))
                .andExpect(status().isNoContent());
        verify(collectionEntryService).deleteCollectionEntry(any(Long.class), any(Authentication.class));
    }

    @Test
    public void deleteCollectionEntry_givenUnauthorized_shouldReturn401() throws Exception {
        // given
        Long id = 1L;

        // when & then
        mockMvc.perform(delete("/api/manga/collection/{id}", id)
                    .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
