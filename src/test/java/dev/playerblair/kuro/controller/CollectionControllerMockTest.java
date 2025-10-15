package dev.playerblair.kuro.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import dev.playerblair.kuro.exception.CollectionEntryNotFoundException;
import dev.playerblair.kuro.exception.JikanApiException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import dev.playerblair.kuro.config.TestSecurityConfig;
import dev.playerblair.kuro.dto.CollectionEntryDto;
import dev.playerblair.kuro.model.CollectionEntry;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.request.CollectionEntryRequest;
import dev.playerblair.kuro.service.CollectionService;

@AutoConfigureJsonTesters
@WebMvcTest(CollectionController.class)
@Import(TestSecurityConfig.class)
public class CollectionControllerMockTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
	private CollectionService collectionService;
	
	@Autowired
	private JacksonTester<List<CollectionEntryDto>> jsonCollection;
	
	@Autowired
	private JacksonTester<CollectionEntryDto> jsonCollectionEntry;
	
	@Autowired
	private JacksonTester<CollectionEntryRequest> jsonCollectionEntryRequest;
	
	private CollectionEntry collectionEntry;
	private CollectionEntryRequest collectionEntryRequest;
	
	@BeforeEach
	public void setup() {
		collectionEntry = new CollectionEntry(
				1L,
				new User(),
				new Manga(),
				"standard",
				1,
				"",
				LocalDate.now()
		);
		collectionEntryRequest = new CollectionEntryRequest(
				1L,
				"standard",
				1,
				"",
				collectionEntry.getPurchaseDate()
		);
	}
	
	@Test
	@WithMockUser
	public void whenGetCollectionIsCalled_shouldReturn200() throws Exception {
		// given
		when(collectionService.getCollection(any(Authentication.class)))
				.thenReturn(List.of(collectionEntry));
		
		// when & then
		mockMvc.perform(get("/api/collection")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json(jsonCollection.write(List.of(collectionEntry.toDto())).getJson()));
	}
	
	@Test
	public void whenGetCollectionIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
		// when & then
		mockMvc.perform(get("/api/collection")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser
	public void whenGetCollectionIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
		// given
		when(collectionService.getCollection(any(Authentication.class)))
				.thenThrow(new UsernameNotFoundException("User not found with username: testuser"));
		
		// when & then
		mockMvc.perform(get("/api/collection")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(containsString("User not found with username: testuser")));
	}
	
	@Test
	@WithMockUser
	public void whenGetCollectionEntryIsCalled_shouldReturn200() throws Exception {
		// given
		when(collectionService.getCollectionEntry(eq(1L), any(Authentication.class)))
				.thenReturn(collectionEntry);
		
		// when & then
		mockMvc.perform(get("/api/collection/{id}", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json(jsonCollectionEntry.write(collectionEntry.toDto()).getJson()));
	}
	
	@Test
	public void whenGetCollectionEntryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
		// when & then
		mockMvc.perform(get("/api/collection/{id}", 1L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser
	public void whenGetCollectionEntryIsCalled_givenInvalidRequest_shouldReturn400() throws Exception {
		// when & then
		mockMvc.perform(get("/api/collection/{id}", 0L)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().string(containsString("ID cannot be less than 1")));
	}

    @Test
    @WithMockUser
    public void whenGetCollectionEntryIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
        // given
        when(collectionService.getCollectionEntry(eq(1L), any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("User not found with username: testuser"));

        // when & then
        mockMvc.perform(get("/api/collection/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenGetCollectionEntryIsCalled_givenCollectionEntryNotFound_shouldReturn404() throws Exception {
        // given
        when(collectionService.getCollectionEntry(eq(1L), any(Authentication.class)))
                .thenThrow(new CollectionEntryNotFoundException(1L));

        // when & then
        mockMvc.perform(get("/api/collection/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("CollectionEntry not found with ID: 1")));
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_shouldReturn201() throws Exception {
        // given
        when(collectionService.createCollectionEntry(eq(collectionEntryRequest), any(Authentication.class)))
                .thenReturn(collectionEntry);

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonCollectionEntry.write(collectionEntry.toDto()).getJson()));
    }

    @Test
    public void whenCreateCollectionEntryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(post("/api/collection"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_givenInvalidRequest_shouldReturn400() throws Exception {
        // given
        collectionEntryRequest = new CollectionEntryRequest(
                0L,
                null,
                -1,
                null,
                null
        );

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MalID cannot be less than 1")))
                .andExpect(content().string(containsString("Edition cannot be blank")))
                .andExpect(content().string(containsString("Volume no. cannot be less than 0")));
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_givenInvalidRequestNull_shouldReturn400() throws Exception {
        // given
        collectionEntryRequest = new CollectionEntryRequest(
                null,
                "standard",
                null,
                null,
                null
        );

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MalID cannot be null")))
                .andExpect(content().string(containsString("Volume no. cannot be null")));
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
        // given
        when(collectionService.createCollectionEntry(eq(collectionEntryRequest), any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("User not found with username: testuser"));

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_givenMangaNotFound_shouldReturn404() throws Exception {
        // given
        when(collectionService.createCollectionEntry(eq(collectionEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(404, "Manga not found"));

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Manga not found")));
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_givenJikanApiException_shouldReturn429() throws Exception {
        // given
        when(collectionService.createCollectionEntry(eq(collectionEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(429, "Jikan API rate limit exceeded"));

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.containsString("Jikan API rate limit exceeded")));
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_givenJikanApiException_shouldReturn500() throws Exception {
        // given
        when(collectionService.createCollectionEntry(eq(collectionEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(500, "Jikan API encountered an internal error"));

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.containsString("Jikan API encountered an internal error")));
    }

    @Test
    @WithMockUser
    public void whenCreateCollectionEntryIsCalled_givenJikanApiException_shouldReturn503() throws Exception {
        // given
        when(collectionService.createCollectionEntry(eq(collectionEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(503, "Jikan API service is temporarily unavailable"));

        // when & then
        mockMvc.perform(post("/api/collection")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(Matchers.containsString("Jikan API service is temporarily unavailable")));
    }

    @Test
    @WithMockUser
    public void whenUpdateCollectionEntryIsCalled_shouldReturn200() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/collection/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("CollectionEntry updated successfully")));
    }

    @Test
    public void whenUpdateCollectionEntryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/collection/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenUpdateCollectionEntryIsCalled_givenNoRequestBody_shouldReturn400() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/collection/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The request body could not be parsed. Please check your JSON syntax and field types.")));
    }

    @Test
    @WithMockUser
    public void whenUpdateCollectionEntryIsCalled_givenUsernameNotFound_shouldReturn400() throws Exception {
        // given
        doThrow(new UsernameNotFoundException("User not found with username: testuser")).when(collectionService)
                .updateCollectionEntry(eq(1L), eq(collectionEntryRequest), any(Authentication.class));

        // when & then
        mockMvc.perform(patch("/api/collection/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenUpdateCollectionEntryIsCalled_givenCollectionEntryNotFound_shouldReturn404() throws Exception {
        // given
        doThrow(new CollectionEntryNotFoundException(1L)).when(collectionService)
                .updateCollectionEntry(eq(1L), eq(collectionEntryRequest), any(Authentication.class));

        // when & then
        mockMvc.perform(patch("/api/collection/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCollectionEntryRequest.write(collectionEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("CollectionEntry not found with ID: 1")));
    }

    @Test
    @WithMockUser
    public void whenDeleteCollectionEntryIsCalled_shouldReturn200() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/collection/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("CollectionEntry deleted successfully")));
    }

    @Test
    public void whenDeleteCollectionEntryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/collection/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenDeleteCollectionEntryIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
        // given
        doThrow(new UsernameNotFoundException("User not found with username: testuser")).when(collectionService)
                .deleteCollectionEntry(eq(1L), any(Authentication.class));

        // when & then
        mockMvc.perform(delete("/api/collection/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenDeleteCollectionEntryIsCalled_givenCollectionEntryNotFound_shouldReturn404() throws Exception {
        // given
        doThrow(new CollectionEntryNotFoundException(1L)).when(collectionService)
                .deleteCollectionEntry(eq(1L), any(Authentication.class));

        // when & then
        mockMvc.perform(delete("/api/collection/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("CollectionEntry not found with ID: 1")));
    }
}
