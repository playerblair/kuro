package dev.playerblair.kuro.collection;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.auth.service.TokenService;
import dev.playerblair.kuro.collection.dto.CollectionEntryDto;
import dev.playerblair.kuro.collection.dto.CollectionEntryRequest;
import dev.playerblair.kuro.collection.dto.CollectionEntryShortFormDto;
import dev.playerblair.kuro.collection.dto.MangaCollectionDto;
import dev.playerblair.kuro.collection.exception.CollectionEntryAlreadyExistsException;
import dev.playerblair.kuro.collection.exception.CollectionEntryNotFoundException;
import dev.playerblair.kuro.collection.model.CollectionEntry;
import dev.playerblair.kuro.collection.model.CollectionType;
import dev.playerblair.kuro.manga.MangaService;
import dev.playerblair.kuro.manga.dto.MangaDto;
import dev.playerblair.kuro.manga.exception.JikanApiMangaNotFoundException;
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
public class CollectionEntryServiceTest {

    @Mock
    private CollectionEntryRepository collectionEntryRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private MangaService mangaService;

    @InjectMocks
    private CollectionEntryService collectionEntryService;

    @Captor
    private ArgumentCaptor<CollectionEntry> collectionEntryCaptor;

    private Authentication authentication;
    private User testUser;
    private Manga manga;
    private MangaDto mangaDto;
    private CollectionEntry collectionEntry;
    private CollectionEntryDto collectionEntryDto;
    private MangaCollectionDto mangaCollectionDto;
    private CollectionEntryRequest createRequest;
    private CollectionEntryRequest updateRequest;

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

        collectionEntry = CollectionEntry.builder()
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
    public void getCollection_shouldReturnCollection() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.findAllByUser(testUser)).thenReturn(List.of(collectionEntry));
        when(mangaService.toMangaDto(collectionEntry.getManga())).thenReturn(mangaDto);

        // when
        List<CollectionEntryDto> collection = collectionEntryService.getCollection(authentication);

        // then
        assertThat(collection).containsExactly(collectionEntryDto);
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).findAllByUser(testUser);
        verify(mangaService).toMangaDto(collectionEntry.getManga());
    }

    @Test
    public void getMangaCollection_shouldReturnMangaCollection() {
        // given
        Long malId = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.findAllByUserAndMalId(testUser, malId)).thenReturn(List.of(collectionEntry));
        when(mangaService.toMangaDto(collectionEntry.getManga())).thenReturn(mangaDto);

        // when
        MangaCollectionDto mangaCollection = collectionEntryService.getMangaCollection(malId, authentication);

        // then
        assertThat(mangaCollection).isEqualTo(mangaCollectionDto);
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).findAllByUserAndMalId(testUser, malId);
        verify(mangaService).toMangaDto(collectionEntry.getManga());
    }

    @Test
    public void getCollectionEntryById_shouldReturnCollectionEntry() {
        // given
        Long id = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.findByIdAndUser(id, testUser)).thenReturn(Optional.of(collectionEntry));
        when(mangaService.toMangaDto(collectionEntry.getManga())).thenReturn(mangaDto);

        // when
        CollectionEntryDto entry = collectionEntryService.getCollectionEntry(id, authentication);

        // then
        assertThat(entry).isEqualTo(collectionEntryDto);
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).findByIdAndUser(id, testUser);
        verify(mangaService).toMangaDto(collectionEntry.getManga());
    }

    @Test
    public void getCollectionEntryById_givenCollectionEntryNotFound_shouldThrowException() {
        // given
        Long id = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.findByIdAndUser(id, testUser)).thenReturn(Optional.empty());

        // when & then
        CollectionEntryNotFoundException exception = assertThrows(
                CollectionEntryNotFoundException.class,
                () -> collectionEntryService.getCollectionEntry(id, authentication)
        );
        assertThat(exception.getMessage()).isEqualTo("CollectionEntry (ID:" + id + ") not found.");
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).findByIdAndUser(id, testUser);
    }

    @Test
    public void createCollectionEntry_shouldReturnCollectionEntry() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition(
                    testUser,
                    createRequest.malId(),
                    createRequest.type(),
                    createRequest.volumeNumber(),
                    createRequest.edition()))
                .thenReturn(false);
        when(mangaService.saveManga(createRequest.malId())).thenReturn(manga);
        when(collectionEntryRepository.save(any(CollectionEntry.class))).thenReturn(collectionEntry);
        when(mangaService.toMangaDto(collectionEntry.getManga())).thenReturn(mangaDto);

        // when
        CollectionEntryDto response = collectionEntryService.createCollectionEntry(createRequest, authentication);

        // then
        assertThat(response).isEqualTo(collectionEntryDto);
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition(
                testUser,
                createRequest.malId(),
                createRequest.type(),
                createRequest.volumeNumber(),
                createRequest.edition());
        verify(mangaService).saveManga(createRequest.malId());
        verify(collectionEntryRepository).save(any(CollectionEntry.class));
        verify(mangaService).toMangaDto(collectionEntry.getManga());
    }

    @Test
    public void createCollectionEntry_givenCollectionEntryAlreadyExists_shouldThrowException() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition(
                testUser,
                createRequest.malId(),
                createRequest.type(),
                createRequest.volumeNumber(),
                createRequest.edition()))
                .thenReturn(true);

        // when & then
        CollectionEntryAlreadyExistsException exception = assertThrows(
                CollectionEntryAlreadyExistsException.class,
                () -> collectionEntryService.createCollectionEntry(createRequest, authentication)
        );
        assertThat(exception.getMessage()).contains(String.format(
                "CollectionEntry for the '%s' edition of volume #%d of Manga (malID:%d), with type:%s already exists.",
                createRequest.edition(), createRequest.volumeNumber(), createRequest.malId(), createRequest.type()));
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition(
                testUser,
                createRequest.malId(),
                createRequest.type(),
                createRequest.volumeNumber(),
                createRequest.edition());
    }

    @Test
    public void createCollectionEntry_givenMangaNotFound_shouldThrowException() {
        // given
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition(
                testUser,
                createRequest.malId(),
                createRequest.type(),
                createRequest.volumeNumber(),
                createRequest.edition()))
                .thenReturn(false);
        when(mangaService.saveManga(createRequest.malId()))
                .thenThrow(new JikanApiMangaNotFoundException(createRequest.malId()));

        // when & then
        JikanApiMangaNotFoundException exception = assertThrows(
                JikanApiMangaNotFoundException.class,
                () -> collectionEntryService.createCollectionEntry(createRequest, authentication)
        );
        assertThat(exception.getMessage()).contains("Manga not found with malID: " + createRequest.malId());
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition(
                testUser,
                createRequest.malId(),
                createRequest.type(),
                createRequest.volumeNumber(),
                createRequest.edition());
        verify(mangaService).saveManga(createRequest.malId());
    }

    @Test
    public void updateCollectionEntry_shouldUpdateEntry() {
        // given
        Long id = 1L;
        CollectionEntry updated = CollectionEntry.builder()
                .id(1L)
                .user(testUser)
                .manga(manga)
                .type(CollectionType.PHYSICAL)
                .volumeNumber(1)
                .edition("Standard")
                .notes("destroyed by spilled water")
                .build();
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.findByIdAndUser(id, testUser)).thenReturn(Optional.of(collectionEntry));

        // when
        collectionEntryService.updateCollectionEntry(id, updateRequest, authentication);

        // then
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).findByIdAndUser(id, testUser);
        verify(collectionEntryRepository).save(collectionEntryCaptor.capture());

        CollectionEntry captured = collectionEntryCaptor.getValue();
        assertThat(captured).usingRecursiveComparison().isEqualTo(updated);
    }

    @Test
    public void updateCollectionEntry_givenCollectionEntryNotFound_shouldThrowException() {
        // given
        Long id = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.findByIdAndUser(id, testUser)).thenReturn(Optional.empty());

        // when & then
        CollectionEntryNotFoundException exception = assertThrows(
                CollectionEntryNotFoundException.class,
                () -> collectionEntryService.updateCollectionEntry(id, updateRequest, authentication)
        );
        assertThat(exception.getMessage()).contains("CollectionEntry (ID:" + id + ") not found.");
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).findByIdAndUser(id, testUser);
    }

    @Test
    public void deleteCollectionEntry_shouldReturn() {
        // given
        Long id = 1L;
        when(tokenService.getUserFromAuthentication(authentication)).thenReturn(testUser);
        when(collectionEntryRepository.deleteByIdAndUser(id, testUser)).thenReturn(1);

        // when
        collectionEntryService.deleteCollectionEntry(id, authentication);

        // then
        verify(tokenService).getUserFromAuthentication(authentication);
        verify(collectionEntryRepository).deleteByIdAndUser(id, testUser);
    }
}
