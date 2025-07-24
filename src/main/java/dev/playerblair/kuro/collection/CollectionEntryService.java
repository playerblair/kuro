package dev.playerblair.kuro.collection;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.auth.service.TokenService;
import dev.playerblair.kuro.collection.dto.CollectionEntryDto;
import dev.playerblair.kuro.collection.dto.CollectionEntryRequest;
import dev.playerblair.kuro.collection.dto.CollectionEntryShortFormDto;
import dev.playerblair.kuro.collection.dto.MangaCollectionDto;
import dev.playerblair.kuro.collection.exception.CollectionEntryNotFoundException;
import dev.playerblair.kuro.collection.exception.CollectionEntryAlreadyExistsException;
import dev.playerblair.kuro.collection.model.CollectionEntry;
import dev.playerblair.kuro.manga.MangaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CollectionEntryService {

    private final CollectionEntryRepository collectionEntryRepository;
    private final MangaService mangaService;
    private final TokenService tokenService;

    public CollectionEntryService(CollectionEntryRepository collectionEntryRepository, MangaService mangaService, TokenService tokenService) {
        this.collectionEntryRepository = collectionEntryRepository;
        this.mangaService = mangaService;
        this.tokenService = tokenService;
    }

    public List<CollectionEntryDto> getCollection(Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is fetching their collection.", user.getId());

        List<CollectionEntry> collectionEntries = collectionEntryRepository.findAllByUser(user);

        log.debug("Fetched {} collection entries for User (ID:{}).", collectionEntries.size(), user.getId());
        return collectionEntries.stream()
                .map(this::toCollectionEntryDto)
                .toList();
    }

    public MangaCollectionDto getMangaCollection(Long malId, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is fetching their collection for Manga (malID:{}).", user.getId(), malId);

        List<CollectionEntry> collectionEntries = collectionEntryRepository.findAllByUserAndMalId(user, malId);

        log.debug("Fetched {} collection entries for Manga (malID:{}), for User (ID:{}).", collectionEntries.size(), malId, user.getId());
        return toMangaCollectionDto(collectionEntries);
    }

    public CollectionEntryDto getCollectionEntryById(Long id, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is fetching CollectionEntry (ID:{}).", user.getId(), id);

        CollectionEntry collectionEntry = collectionEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CollectionEntryNotFoundException(id));

        log.debug("Fetched CollectionEntry (ID:{}) for User (ID:{}).", id, user.getId());
        return toCollectionEntryDto(collectionEntry);
    }

    @Transactional
    public CollectionEntryDto createVolumeEntry(CollectionEntryRequest request, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is creating CollectionEntry for volume #{} of Manga (malID:{}), with type:{}.",
                user.getId(), request.volumeNumber(), request.malId(), request.type());

        if (collectionEntryRepository.existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition
                (user, request.malId(), request.type(), request.volumeNumber(), request.edition())) {
            throw new CollectionEntryAlreadyExistsException(request.malId(), request.type(), request.volumeNumber(), request.edition());
        }

        CollectionEntry collectionEntry = CollectionEntry.builder()
                .user(user)
                .manga(mangaService.saveManga(request.malId()))
                .type(request.type())
                .volumeNumber(request.volumeNumber())
                .edition(request.edition())
                .build();

        editVolumeEntry(collectionEntry, request);

        CollectionEntry saved = collectionEntryRepository.save(collectionEntry);

        log.debug("Created VolumeEntry for volume #{} of Manga (malID:{}), with type:{}, for User (ID:{}).",
                request.volumeNumber(), request.malId(), request.type(), user.getId());
        return toCollectionEntryDto(saved);
    }

    @Transactional
    public void updateCollectionEntry(Long id, CollectionEntryRequest request, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is updating CollectionEntry (ID:{}).", user.getId(), id);

        CollectionEntry collectionEntry = collectionEntryRepository.findByIdAndUser(id, user)
                        .orElseThrow(() -> new CollectionEntryNotFoundException(id));

        editVolumeEntry(collectionEntry, request);

        collectionEntryRepository.save(collectionEntry);

        log.debug("Updated VolumeEntry (ID:{}) for User (ID:{}).", id, user.getId());
    }

    @Transactional
    public void deleteCollectionEntry(Long id, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is deleting CollectionEntry (ID:{}).", user.getId(), id);

        int deleted = collectionEntryRepository.deleteByIdAndUser(id, user);

        if (deleted == 0) {
            log.debug("No CollectionEntry (ID:{}) found to delete for User (ID:{}).", id, user.getId());
        } else {
            log.debug("Deleted CollectionEntry (ID:{}) for User (ID:{}).", id, user.getId());
        }
    }

    private MangaCollectionDto toMangaCollectionDto(List<CollectionEntry> collectionEntries) {
        return new MangaCollectionDto(
                mangaService.toMangaDto(collectionEntries.getFirst().getManga()),
                collectionEntries.stream()
                        .map(this::toCollectionEntryShortFormDto)
                        .collect(Collectors.toSet())
        );
    }

    private void editVolumeEntry(CollectionEntry volumeEntry, CollectionEntryRequest request) {
        if (request.edition() != null) {
            volumeEntry.setEdition(request.edition());
        }

        if (request.notes() != null) {
            volumeEntry.setNotes(request.notes());
        }

        if (request.datePurchased() != null) {
            volumeEntry.setDatePurchased(request.datePurchased());
        }
    }

    private CollectionEntryDto toCollectionEntryDto(CollectionEntry collectionEntry) {
        return new CollectionEntryDto(
                collectionEntry.getId(),
                mangaService.toMangaDto(collectionEntry.getManga()),
                collectionEntry.getType(),
                collectionEntry.getVolumeNumber(),
                collectionEntry.getEdition(),
                collectionEntry.getNotes(),
                collectionEntry.getDatePurchased()
        );
    }

    private CollectionEntryShortFormDto toCollectionEntryShortFormDto(CollectionEntry collectionEntry) {
        return new CollectionEntryShortFormDto(
                collectionEntry.getId(),
                collectionEntry.getType(),
                collectionEntry.getVolumeNumber(),
                collectionEntry.getEdition(),
                collectionEntry.getNotes(),
                collectionEntry.getDatePurchased()
        );
    }
}
