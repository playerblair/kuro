package dev.playerblair.kuro.library;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.auth.service.TokenService;
import dev.playerblair.kuro.library.dto.MangaEntryDto;
import dev.playerblair.kuro.library.dto.MangaEntryRequest;
import dev.playerblair.kuro.library.exception.MangaEntryAlreadyExistsException;
import dev.playerblair.kuro.library.exception.MangaEntryNotFoundException;
import dev.playerblair.kuro.library.model.MangaEntry;
import dev.playerblair.kuro.library.model.Progress;
import dev.playerblair.kuro.manga.MangaService;
import dev.playerblair.kuro.manga.dto.MangaDto;
import dev.playerblair.kuro.manga.exception.MangaNotFoundException;
import dev.playerblair.kuro.manga.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryServiceTest {

    @Mock
    private MangaEntryRepository mangaEntryRepository;

    @Mock
    private MangaService mangaService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LibraryService libraryService;

    @Captor
    private ArgumentCaptor<MangaEntry> mangaEntryCaptor;

    Authentication authentication;
    User testUser;
    Manga manga;
    MangaDto mangaDto;
    MangaEntry mangaEntry;
    MangaEntryDto mangaEntryDto;
    MangaEntryRequest createRequest;
    MangaEntryRequest updateRequest;

    @BeforeEach
    public void setUp() {
        authentication = mock(Authentication.class);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role("USER")
                .build();

        manga = Manga.builder()
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

        mangaDto = new MangaDto(
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

        mangaEntry = MangaEntry.builder()
                .user(testUser)
                .manga(manga)
                .progress(Progress.PLANNING)
                .chaptersRead(100)
                .volumesRead(1)
                .rating(5)
                .notes("test notes")
                .build();

        mangaEntryDto = new MangaEntryDto(
                mangaDto,
                mangaEntry.getProgress(),
                mangaEntry.getChaptersRead(),
                mangaEntry.getVolumesRead(),
                mangaEntry.getRating(),
                mangaEntry.getNotes()
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
    public void getLibrary_shouldReturnLibrary() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.findAllByUser(testUser)).thenReturn(List.of(mangaEntry));
        when(mangaService.toMangaDto(mangaEntry.getManga())).thenReturn(mangaDto);

        // when
        List<MangaEntryDto> library = libraryService.getLibrary(authentication);

        // then
        assertThat(library).containsExactly(mangaEntryDto);
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).findAllByUser(testUser);
        verify(mangaService).toMangaDto(mangaEntry.getManga());
    }

    @Test
    public void getMangaEntry_shouldReturnMangaEntry() {
        // given
        Long malId = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.findByUserAndMalId(testUser, malId)).thenReturn(Optional.of(mangaEntry));
        when(mangaService.toMangaDto(mangaEntry.getManga())).thenReturn(mangaDto);

        // when
        MangaEntryDto response = libraryService.getMangaEntry(malId, authentication);

        // then
        assertThat(response).isEqualTo(mangaEntryDto);
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).findByUserAndMalId(testUser, malId);
        verify(mangaService).toMangaDto(mangaEntry.getManga());
    }

    @Test
    public void getMangaEntry_givenMangaNotFound_shouldThrowException() {
        // given
        Long malId = 2L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.findByUserAndMalId(testUser, malId)).thenReturn(Optional.empty());

        // when & then
        MangaEntryNotFoundException exception = assertThrows(
                MangaEntryNotFoundException.class,
                () -> libraryService.getMangaEntry(malId, authentication)
        );
        assertThat(exception.getMessage()).isEqualTo("MangaEntry for Manga (malID:" + malId + ") not found.");
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).findByUserAndMalId(testUser, malId);
    }

    @Test
    public void createMangaEntry_shouldReturnMangaEntry() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.existsByUserAndMalId(testUser, createRequest.malId())).thenReturn(false);
        when(mangaService.saveManga(createRequest.malId())).thenReturn(manga);
        when(mangaEntryRepository.save(any(MangaEntry.class))).thenReturn(mangaEntry);
        when(mangaService.toMangaDto(mangaEntry.getManga())).thenReturn(mangaDto);

        // when
        MangaEntryDto response = libraryService.createMangaEntry(createRequest, authentication);

        // then
        assertThat(response).isEqualTo(mangaEntryDto);
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).existsByUserAndMalId(testUser, createRequest.malId());
        verify(mangaService).saveManga(createRequest.malId());
        verify(mangaEntryRepository).save(any(MangaEntry.class));
        verify(mangaService).toMangaDto(mangaEntry.getManga());
    }

    @Test
    public void createMangaEntry_givenMangaEntryAlreadyExists_shouldThrowException() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.existsByUserAndMalId(testUser, createRequest.malId())).thenReturn(true);

        // when & then
        MangaEntryAlreadyExistsException exception = assertThrows(
                MangaEntryAlreadyExistsException.class,
                () -> libraryService.createMangaEntry(createRequest, authentication)
        );
        assertThat(exception.getMessage())
                .isEqualTo("MangaEntry for Manga (malID:" + createRequest.malId() + ") already exists.");
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).existsByUserAndMalId(testUser, createRequest.malId());
    }

    @Test
    public void createMangaEntry_givenMangaNotFound_shouldThrowException() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.existsByUserAndMalId(testUser, createRequest.malId())).thenReturn(false);
        when(mangaService.saveManga(createRequest.malId())).thenThrow(new MangaNotFoundException(createRequest.malId()));

        // when & then
        MangaNotFoundException exception = assertThrows(
                MangaNotFoundException.class,
                () -> libraryService.createMangaEntry(createRequest, authentication)
        );
        assertThat(exception.getMessage())
                .isEqualTo("Manga not found with malID: " + createRequest.malId() + ".");
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).existsByUserAndMalId(testUser, createRequest.malId());
        verify(mangaService).saveManga(createRequest.malId());
    }

    @Test
    public void updateMangaEntry_shouldUpdateEntry() {
        // given
        Long malId = 1L;
        MangaEntry updated = MangaEntry.builder()
                .user(testUser)
                .manga(manga)
                .progress(Progress.FINISHED)
                .chaptersRead(100)
                .volumesRead(1)
                .rating(5)
                .notes("test notes")
                .build();
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.findByUserAndMalId(testUser, malId)).thenReturn(Optional.of(mangaEntry));

        // when
        libraryService.updateMangaEntry(malId, updateRequest, authentication);

        // then
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).findByUserAndMalId(testUser, malId);
        verify(mangaEntryRepository).save(mangaEntryCaptor.capture());

        MangaEntry captured = mangaEntryCaptor.getValue();
        assertThat(captured).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    public void updateMangaEntry_givenMangaEntryNotFound_shouldThrowException() {
        // given
        Long malId = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.findByUserAndMalId(testUser, malId)).thenReturn(Optional.empty());

        // when & then
        MangaEntryNotFoundException exception = assertThrows(
                MangaEntryNotFoundException.class,
                () -> libraryService.updateMangaEntry(malId, updateRequest, authentication)
        );
        assertThat(exception.getMessage()).isEqualTo("MangaEntry for Manga (malID:" + malId + ") not found.");
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).findByUserAndMalId(testUser, malId);
    }

    @Test
    public void deleteMangaEntry_shouldReturn() {
        // given
        Long malId = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(mangaEntryRepository.deleteByUserAndMalId(testUser, malId)).thenReturn(1);

        // when
        libraryService.deleteMangaEntry(malId, authentication);

        // given
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(mangaEntryRepository).deleteByUserAndMalId(testUser, malId);
    }
}
