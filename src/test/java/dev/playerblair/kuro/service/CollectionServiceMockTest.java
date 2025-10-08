package dev.playerblair.kuro.service;

import dev.playerblair.kuro.exception.CollectionEntryNotFoundException;
import dev.playerblair.kuro.exception.JikanApiException;
import dev.playerblair.kuro.model.CollectionEntry;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.CollectionEntryRepository;
import dev.playerblair.kuro.request.CollectionEntryRequest;
import dev.playerblair.kuro.util.AuthenticationHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CollectionServiceMockTest {

    @Mock
    private CollectionEntryRepository collectionEntryRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private MangaService mangaService;

    @InjectMocks
    private CollectionService collectionService;

    private CollectionEntry collectionEntry;
    private CollectionEntryRequest collectionEntryRequest;
    private Authentication mockAuthentication;
    private User mockUser;

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
        mockAuthentication = mock(Authentication.class);
        mockUser = mock(User.class);
    }

    @Test
    public void whenGetCollectionIsCalled_shouldReturnCollection() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(collectionEntryRepository.findAllByUser(any(User.class))).thenReturn(List.of(collectionEntry));

        // when
        List<CollectionEntry> collection = collectionService.getCollection(mockAuthentication);

        // then
        assertEquals(1, collection.size());
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository).findAllByUser(any(User.class));
    }

    @Test
    public void whenGetCollectionIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> collectionService.getCollection(mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository, never()).findAllByUser(mockUser);
    }
    
    @Test
    public void whenGetCollectionEntryIsCalled_shouldReturnCollectionEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(collectionEntryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(collectionEntry));

        // when
        CollectionEntry entry = collectionService.getCollectionEntry(1L, mockAuthentication);

        // then
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository).findByIdAndUser(1L, mockUser);
    }

    @Test
    public void whenGetCollectionEntryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> collectionService.getCollectionEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository, never()).findByIdAndUser(1L, mockUser);
    }

    @Test
    public void whenGetCollectionEntryIsCalled_givenCollectionEntryNotFound_shouldReturnCollectionEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(collectionEntryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        // when & then
        assertThrows(
                CollectionEntryNotFoundException.class,
                () -> collectionService.getCollectionEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository).findByIdAndUser(1L, mockUser);
    }

    @Test
    public void whenCreateCollectionEntryIsCalled_shouldCreateEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(mangaService.getManga(collectionEntryRequest.malId())).thenReturn(collectionEntry.getManga());
        when(collectionEntryRepository.save(any(CollectionEntry.class))).thenReturn(collectionEntry);

        // when
        CollectionEntry entry = collectionService.createCollectionEntry(collectionEntryRequest, mockAuthentication);

        // then
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(mangaService).getManga(collectionEntryRequest.malId());
        verify(collectionEntryRepository).save(any(CollectionEntry.class));
    }

    @Test
    public void whenCreateCollectionEntryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> collectionService.createCollectionEntry(collectionEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(mangaService, never()).getManga(collectionEntryRequest.malId());
        verify(collectionEntryRepository, never()).save(any(CollectionEntry.class));
    }

    @Test
    public void whenCreateCollectionEntryIsCalled_givenMangaNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(mangaService.getManga(collectionEntryRequest.malId())).thenThrow(JikanApiException.class);

        // when & then
        assertThrows(
                JikanApiException.class,
                () -> collectionService.createCollectionEntry(collectionEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(mangaService).getManga(collectionEntryRequest.malId());
        verify(collectionEntryRepository, never()).save(any(CollectionEntry.class));
    }

    @Test
    public void whenUpdateCollectionEntryIsCalled_shouldUpdateEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(collectionEntryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(collectionEntry));

        // when
        collectionService.updateCollectionEntry(1L, collectionEntryRequest, mockAuthentication);

        // then
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository).findByIdAndUser(1L, mockUser);
        verify(collectionEntryRepository).save(any(CollectionEntry.class));
    }

    @Test
    public void whenUpdateCollectionEntryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> collectionService.updateCollectionEntry(1L, collectionEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository, never()).findByIdAndUser(1L, mockUser);
        verify(collectionEntryRepository, never()).save(any(CollectionEntry.class));
    }

    @Test
    public void whenUpdateCollectionEntryIsCalled_givenCollectionEntryNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(collectionEntryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        // when & then
        assertThrows(
                CollectionEntryNotFoundException.class,
                () -> collectionService.updateCollectionEntry(1L, collectionEntryRequest, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository).findByIdAndUser(1L, mockUser);
        verify(collectionEntryRepository, never()).save(any(CollectionEntry.class));
    }

    @Test
    public void whenDeleteCollectionEntryIsCalled_shouldDeleteEntry() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(collectionEntryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(collectionEntry));

        // when
        collectionService.deleteCollectionEntry(1L, mockAuthentication);

        // then
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository).findByIdAndUser(1L, mockUser);
        verify(collectionEntryRepository).delete(any(CollectionEntry.class));
    }

    @Test
    public void whenDeleteCollectionEntryIsCalled_givenUsernameNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenThrow(UsernameNotFoundException.class);

        // when & then
        assertThrows(
                UsernameNotFoundException.class,
                () -> collectionService.deleteCollectionEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository, never()).findByIdAndUser(1L, mockUser);
        verify(collectionEntryRepository, never()).delete(any(CollectionEntry.class));
    }

    @Test
    public void whenDeleteCollectionEntryIsCalled_givenCollectionEntryNotFound_shouldThrowException() {
        // given
        when(authenticationHelper.getCurrentUser(mockAuthentication)).thenReturn(mockUser);
        when(collectionEntryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        // when & then
        assertThrows(
                CollectionEntryNotFoundException.class,
                () -> collectionService.deleteCollectionEntry(1L, mockAuthentication)
        );
        verify(authenticationHelper).getCurrentUser(mockAuthentication);
        verify(collectionEntryRepository).findByIdAndUser(1L, mockUser);
        verify(collectionEntryRepository, never()).delete(any(CollectionEntry.class));
    }
}
