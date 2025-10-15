package dev.playerblair.kuro.controller;

import dev.playerblair.kuro.config.TestSecurityConfig;
import dev.playerblair.kuro.dto.LibraryEntryDto;
import dev.playerblair.kuro.exception.JikanApiException;
import dev.playerblair.kuro.exception.LibraryEntryAlreadyExistsException;
import dev.playerblair.kuro.exception.LibraryEntryNotFoundException;
import dev.playerblair.kuro.model.LibraryEntry;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.model.Progress;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.request.LibraryEntryRequest;
import dev.playerblair.kuro.service.LibraryService;
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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureJsonTesters
@WebMvcTest(LibraryController.class)
@Import(TestSecurityConfig.class)
public class LibraryControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibraryService libraryService;

    @Autowired
    private JacksonTester<List<LibraryEntryDto>> jsonLibrary;

    @Autowired
    private JacksonTester<LibraryEntryDto> jsonLibraryEntryDto;

    @Autowired
    private JacksonTester<LibraryEntryRequest> jsonLibraryEntryRequest;

    private LibraryEntry libraryEntry;
    private LibraryEntryRequest libraryEntryRequest;

    @BeforeEach
    public void setup() {
        libraryEntry = new LibraryEntry(
                1L,
                new User(),
                new Manga(),
                Progress.PLANNING,
                0,
                0,
                5,
                ""
        );
        libraryEntryRequest = new LibraryEntryRequest(
                1L,
                Progress.PLANNING,
                0,
                0,
                5,
                ""
        );
    }

    @Test
    @WithMockUser
    public void whenGetLibraryIsCalled_shouldReturn200() throws Exception {
        // given
        when(libraryService.getLibrary(any(Authentication.class))).thenReturn(List.of(libraryEntry));

        // when & then
        mockMvc.perform(get("/api/library")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonLibrary.write(List.of(libraryEntry.toDto())).getJson()));

    }

    @Test
    public void whenGetLibraryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(get("/api/library"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenGetLibraryIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
        // given
        when(libraryService.getLibrary(any(Authentication.class)))
        .thenThrow(new UsernameNotFoundException("User not found with username: testuser"));

        // when & then
        mockMvc.perform(get("/api/library")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenGetLibraryEntryIsCalled_shouldReturn200() throws Exception {
        // given
        when(libraryService.getLibraryEntry(eq(1L), any(Authentication.class))).thenReturn(libraryEntry);

        // when & then
        mockMvc.perform(get("/api/library/{malId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonLibraryEntryDto.write(libraryEntry.toDto()).getJson()));
    }

    @Test
    public void whenGetLibraryEntryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(get("/api/library/{malId}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenGetLibraryEntryIsCalled_givenInvalidParameters_shouldReturn400() throws Exception {
        // when & then
        mockMvc.perform(get("/api/library/{malId}", 0L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MalID cannot be less than 1")));
    }

    @Test
    @WithMockUser
    public void whenGetLibraryEntryIsCalled_givenUsernameNotFount_shouldReturn404() throws Exception {
        // given
        when(libraryService.getLibraryEntry(eq(1L), any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("User not found with username: testuser"));

        // when & then
        mockMvc.perform(get("/api/library/{malId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenGetLibraryEntryIsCalled_givenLibraryEntryNotFound_shouldReturn404() throws Exception {
        // given
        when(libraryService.getLibraryEntry(eq(1L), any(Authentication.class)))
                .thenThrow(new LibraryEntryNotFoundException(1L));

        // when & then
        mockMvc.perform(get("/api/library/{malId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("LibraryEntry not found for Manga with malID: 1")));
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_shouldReturn201() throws Exception {
        // given
        when(libraryService.createLibraryEntry(eq(libraryEntryRequest), any(Authentication.class)))
                .thenReturn(libraryEntry);

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonLibraryEntryDto.write(libraryEntry.toDto()).getJson()));

    }

    @Test
    public void whenCreateLibraryEntryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_givenInvalidRequest_shouldReturn400() throws Exception {
        // given
        libraryEntryRequest = new LibraryEntryRequest(
                null,
                Progress.PLANNING,
                -1,
                -1,
                5,
                ""
        );

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MalID cannot be null")))
                .andExpect(content().string(containsString("Chapters read cannot be less than 0")))
                .andExpect(content().string(containsString("Volumes read cannot be less than 0")));
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
        // given
        when(libraryService.createLibraryEntry(eq(libraryEntryRequest), any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException("User not found with username: testuser"));

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_givenLibraryEntryAlreadyExists_shouldReturn400() throws Exception {
        // given
        when(libraryService.createLibraryEntry(eq(libraryEntryRequest), any(Authentication.class)))
                .thenThrow(new LibraryEntryAlreadyExistsException(1L));

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("LibraryEntry already exists for Manga with malID: 1")));
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_givenMangaNotFound_shouldReturn404() throws Exception {
        // given
        when(libraryService.createLibraryEntry(eq(libraryEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(404, "Manga not found"));

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Manga not found")));
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_givenJikanApiException_shouldReturn429() throws Exception {
        // given
        when(libraryService.createLibraryEntry(eq(libraryEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(429, "Jikan API rate limit exceeded"));

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Jikan API rate limit exceeded")));
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_givenJikanApiException_shouldReturn500() throws Exception {
        // given
        when(libraryService.createLibraryEntry(eq(libraryEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(500, "Jikan API encountered an internal error"));

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Jikan API encountered an internal error")));
    }

    @Test
    @WithMockUser
    public void whenCreateLibraryEntryIsCalled_givenJikanApiException_shouldReturn503() throws Exception {
        // given
        when(libraryService.createLibraryEntry(eq(libraryEntryRequest), any(Authentication.class)))
                .thenThrow(new JikanApiException(503, "Jikan API service is temporarily unavailable"));

        // when & then
        mockMvc.perform(post("/api/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Jikan API service is temporarily unavailable")));
    }

    @Test
    @WithMockUser
    public void whenUpdateLibraryEntryIsCalled_shouldReturn200() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/library/{malId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("LibraryEntry updated successfully")));
    }

    @Test
    public void whenUpdateLibraryEntryIsCalled_givenUnauthorisedUser_shouldReturn403() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/library/{malId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenUpdateLibraryEntryIsCalled_givenInvalidRequest_shouldReturn400() throws Exception {
        // given
        libraryEntryRequest = new LibraryEntryRequest(
                null,
                Progress.PLANNING,
                -1,
                -1,
                12,
                ""
        );

        // when & then
        mockMvc.perform(patch("/api/library/{malId}", -1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Chapters read cannot be less than 0")))
                .andExpect(content().string(containsString("Volumes read cannot be less than 0")))
                .andExpect(content().string(containsString("Rating cannot be greater than 10")));
    }

    @Test
    @WithMockUser
    public void whenUpdateLibraryEntryIsCalled_givenInvalidPathVariable_shouldReturn400() throws Exception {
        // when & then
        mockMvc.perform(patch("/api/library/{malId}", 0L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MalID cannot be less than 1")));
    }

    @Test
    @WithMockUser
    public void whenUpdateLibraryEntryIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
        // given
        doThrow(new UsernameNotFoundException("User not found with username: testuser"))
                .when(libraryService)
                .updateLibraryEntry(eq(1L), eq(libraryEntryRequest), any(Authentication.class));

        // when & then
        mockMvc.perform(patch("/api/library/{malId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User not found with username: testuser")));
    }

    @Test
    @WithMockUser
    public void whenUpdateLibraryEntryIsCalled_givenLibraryEntryNotFound_shouldReturn404() throws Exception {
        // given
        doThrow(new LibraryEntryNotFoundException(1L))
                .when(libraryService)
                .updateLibraryEntry(eq(1L), eq(libraryEntryRequest), any(Authentication.class));

        // when & then
        mockMvc.perform(patch("/api/library/{malId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLibraryEntryRequest.write(libraryEntryRequest).getJson())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("LibraryEntry not found for Manga with malID: 1")));
    }
    
    @Test
    @WithMockUser
    public void whenDeleteLibraryEntryIsCalled_shouldReturn200() throws Exception {
    	// when & then
    	mockMvc.perform(delete("/api/library/{malId}", 1L)
    				.accept(MediaType.APPLICATION_JSON))
    			.andExpect(status().isOk())
    			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
    			.andExpect(content().string(containsString("LibraryEntry deleted successfully")));
    }
    
    @Test
    public void whenDeleteLibraryEntryIsCalled_givenUnauthorisdeUser_shouldReturn403() throws Exception {
    	// when & then
    	mockMvc.perform(delete("/api/library/{malId}", 1L)
    				.accept(MediaType.APPLICATION_JSON))
    			.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void whenDeleteLibraryEntryIsCalled_givenInvalidRequest_shouldReturn400() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/library/{malId}", 0L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("MalID cannot be less than 1")));
    }
    
    @Test
    @WithMockUser
    public void whenDeleteLibraryEntryIsCalled_givenUsernameNotFound_shouldReturn404() throws Exception {
    	// given
    	doThrow(new UsernameNotFoundException("User not found with username: testuser"))
    			.when(libraryService)
    			.deleteLibraryEntry(eq(1L), any(Authentication.class));
    	
    	// when & then
    	mockMvc.perform(delete("/api/library/{malId}", 1L)
    				.accept(MediaType.APPLICATION_JSON))
    			.andExpect(status().isNotFound())
    			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
    			.andExpect(content().string(containsString("User not found with username: testuser")));
    }
    
    @Test
    @WithMockUser
    public void whenDeleteLibraryEntryIsCalled_givenLibraryEntryNotFound_shouldReturn404() throws Exception {
    	// given
    	doThrow(new LibraryEntryNotFoundException(1L))
    			.when(libraryService)
    			.deleteLibraryEntry(eq(1L), any(Authentication.class));
    	
    	// when & then
    	mockMvc.perform(delete("/api/library/{malId}", 1L)
    				.accept(MediaType.APPLICATION_JSON))
    			.andExpect(status().isNotFound())
    			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
    			.andExpect(content().string(containsString("LibraryEntry not found for Manga with malID: 1")));
    	
    }

}
