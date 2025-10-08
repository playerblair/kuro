package dev.playerblair.kuro.service;

import dev.playerblair.kuro.exception.JikanApiException;
import dev.playerblair.kuro.exception.LibraryEntryAlreadyExistsException;
import dev.playerblair.kuro.exception.LibraryEntryNotFoundException;
import dev.playerblair.kuro.model.LibraryEntry;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.model.Progress;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.LibraryEntryRepository;
import dev.playerblair.kuro.request.LibraryEntryRequest;
import dev.playerblair.kuro.util.AuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibraryServiceMockTest {

    @Mock
    private LibraryEntryRepository libraryEntryRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private MangaService mangaService;

    @InjectMocks
    private LibraryService libraryService;

    private LibraryEntry libraryEntry;
    private LibraryEntryRequest libraryEntryRequest;
    private Authentication mockAuthentication;
    private User mockUser;

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
        mockAuthentication = mock(Authentication.class);
        mockUser = mock(User.class);
    }

    @Test
    public void whenGetLibraryIsCalled_returnLibrary() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.findAllByUser(mockUser)).thenReturn(List.of(libraryEntry));

        // when
        List<LibraryEntry> library = libraryService.getLibrary(mockAuthentication);

        // then
        assertEquals(1, library.size());
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).findAllByUser(mockUser);
    }
    @Test
    public void whenGetLibraryEntryIsCalled_shouldReturnLibraryEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.findByUserAndMalId(mockUser, 1L)).thenReturn(Optional.of(libraryEntry));

        // when
        LibraryEntry entry = libraryService.getLibraryEntry(1L, mockAuthentication);

        // then
        assertEquals(libraryEntry, entry);
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).findByUserAndMalId(mockUser, 1L);
    }

    @Test
    public void whenGetLibraryEntryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> libraryService.getLibraryEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository, never()).findByUserAndMalId(mockUser, 1L);
    }

    @Test
    public void whenGetLibraryEntryIsCalled_givenLibraryEntryNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.findByUserAndMalId(mockUser, 1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(
                LibraryEntryNotFoundException.class,
                () -> libraryService.getLibraryEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).findByUserAndMalId(mockUser, 1L);
    }

    @Test
    public void whenCreateLibraryEntryIsCalled_shouldCreateLibraryEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.existsByUserAndMalId(mockUser, 1L)).thenReturn(false);
        when(mangaService.getManga(libraryEntryRequest.malId())).thenReturn(mock(Manga.class));
        when(libraryEntryRepository.save(any(LibraryEntry.class))).thenReturn(libraryEntry);

        // when
        LibraryEntry entry = libraryService.createLibraryEntry(libraryEntryRequest, mockAuthentication);

        // then
        assertEquals(entry, libraryEntry);
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).existsByUserAndMalId(mockUser, 1L);
        verify(mangaService).getManga(libraryEntryRequest.malId());
        verify(libraryEntryRepository).save(any(LibraryEntry.class));
    }

    @Test
    public void whenCreateLibraryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> libraryService.createLibraryEntry(libraryEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository, never()).existsByUserAndMalId(mockUser, 1L);
        verify(mangaService, never()).getManga(libraryEntryRequest.malId());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    public void whenCreateLibraryIsCalled_givenLibraryEntryAlreadyExists_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.existsByUserAndMalId(mockUser, libraryEntryRequest.malId()))
                .thenReturn(true);

        // when & then
        assertThrows(
                LibraryEntryAlreadyExistsException.class,
                () -> libraryService.createLibraryEntry(libraryEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).existsByUserAndMalId(mockUser, libraryEntryRequest.malId());
        verify(mangaService, never()).getManga(libraryEntryRequest.malId());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    public void whenCreateLibraryIsCalled_givenMangaNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.existsByUserAndMalId(mockUser, libraryEntryRequest.malId()))
                .thenReturn(false);
        when(mangaService.getManga(libraryEntryRequest.malId())).thenThrow(JikanApiException.class);

        // when & then
        assertThrows(
                JikanApiException.class,
                () -> libraryService.createLibraryEntry(libraryEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).existsByUserAndMalId(mockUser, libraryEntryRequest.malId());
        verify(mangaService).getManga(libraryEntryRequest.malId());
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    public void whenUpdateLibraryEntryIsCalled_shouldUpdate() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.findByUserAndMalId(mockUser, 1L))
                .thenReturn(Optional.of(libraryEntry));

        // when
        libraryService.updateLibraryEntry(1L, libraryEntryRequest, mockAuthentication);

        // then
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).findByUserAndMalId(mockUser, 1L);
        verify(libraryEntryRepository).save(any(LibraryEntry.class));
    }

    @Test
    public void whenUpdateLibraryEntryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> libraryService.updateLibraryEntry(1L, libraryEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository, never()).findByUserAndMalId(mockUser, 1L);
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    public void whenUpdateLibraryEntryIsCalled_givenLibraryEntryNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.findByUserAndMalId(mockUser, 1L))
                .thenThrow(LibraryEntryNotFoundException.class);

        // when & then
        assertThrows(
                LibraryEntryNotFoundException.class,
                () -> libraryService.updateLibraryEntry(1L, libraryEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).findByUserAndMalId(mockUser, 1L);
        verify(libraryEntryRepository, never()).save(any(LibraryEntry.class));
    }

    @Test
    public void whenDeleteLibraryEntryIsCalled_shouldDeleteEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.findByUserAndMalId(mockUser, 1L))
                .thenReturn(Optional.of(libraryEntry));

        // when
        libraryService.deleteLibraryEntry(1L, mockAuthentication);

        // given
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).findByUserAndMalId(mockUser, 1L);
        verify(libraryEntryRepository).delete(libraryEntry);
    }

    @Test
    public void whenDeleteLibraryEntryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> libraryService.deleteLibraryEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository, never()).findByUserAndMalId(mockUser, 1L);
        verify(libraryEntryRepository, never()).delete(libraryEntry);
    }

    @Test
    public void whenDeleteLibraryEntryIsCalled_givenLibraryEntryNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(libraryEntryRepository.findByUserAndMalId(mockUser, 1L))
                .thenThrow(LibraryEntryNotFoundException.class);

        // when & then
        assertThrows(
                LibraryEntryNotFoundException.class,
                () -> libraryService.deleteLibraryEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(libraryEntryRepository).findByUserAndMalId(mockUser,  1L);
        verify(libraryEntryRepository, never()).delete(libraryEntry);
    }
}
