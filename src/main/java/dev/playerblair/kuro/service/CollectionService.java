package dev.playerblair.kuro.service;

import dev.playerblair.kuro.exception.CollectionEntryNotFoundException;
import dev.playerblair.kuro.model.CollectionEntry;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.CollectionEntryRepository;
import dev.playerblair.kuro.request.CollectionEntryRequest;
import dev.playerblair.kuro.util.AuthenticationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CollectionService {

    private final CollectionEntryRepository collectionEntryRepository;
    private final AuthenticationHelper helper;
    private final MangaService mangaService;

    public CollectionService(CollectionEntryRepository collectionEntryRepository, AuthenticationHelper helper, MangaService mangaService) {
        this.collectionEntryRepository = collectionEntryRepository;
        this.helper = helper;
        this.mangaService = mangaService;
    }

    public List<CollectionEntry> getCollection(Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is fetching their entire collection", user);
        List<CollectionEntry> results = collectionEntryRepository.findAllByUser(user);
        log.info("Found {} collection entries", results.size());
        return results;
    }

    public List<CollectionEntry> getMangaCollection(Long malId, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is fetching their collection for Manga with malID: {}", user, malId);
        List<CollectionEntry> results = collectionEntryRepository.findAllByUserAndMalId(user, malId);
        log.info("Found {} collection entries for Manga with malID: {}", results.size(), malId);
        return results;
    }

    public CollectionEntry getCollectionEntry(Long id, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is fetching CollectionEntry with ID: {}", user, id);
        CollectionEntry entry = collectionEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CollectionEntryNotFoundException(id));
        log.info("Fetched {}", entry);
        return entry;
    }

    public CollectionEntry createCollectionEntry(CollectionEntryRequest request, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is creating a CollectionEntry for Manga with ID: {}", user, request.malId());
        Manga manga = mangaService.getManga(request.malId());
        CollectionEntry entry = collectionEntryRepository.save(
                CollectionEntry.create(
                        user,
                        manga,
                        request.edition(),
                        request.volumeNumber(),
                        request.notes(),
                        request.purchaseDate()
                )
        );
        log.debug("Created {}", entry);
        return entry;
    }

    public void updateCollectionEntry(Long id, CollectionEntryRequest request, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        CollectionEntry entry = collectionEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CollectionEntryNotFoundException(id));
        log.debug("{} is updating {}", user, entry);
        entry.update(request.notes(), request.purchaseDate());
        collectionEntryRepository.save(entry);
        log.debug("Updated {}", entry);
    }

    public void deleteCollectionEntry(Long id, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is deleting CollectionEntry with ID: {}", user, id);
        CollectionEntry entry = collectionEntryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new CollectionEntryNotFoundException(id));
        collectionEntryRepository.delete(entry);
        log.debug("Delete {}", entry);
    }
}
