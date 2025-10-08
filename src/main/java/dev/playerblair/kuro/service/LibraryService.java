package dev.playerblair.kuro.service;

import dev.playerblair.kuro.exception.LibraryEntryAlreadyExistsException;
import dev.playerblair.kuro.exception.LibraryEntryNotFoundException;
import dev.playerblair.kuro.model.LibraryEntry;
import dev.playerblair.kuro.model.Manga;
import dev.playerblair.kuro.model.User;
import dev.playerblair.kuro.repository.LibraryEntryRepository;
import dev.playerblair.kuro.request.LibraryEntryRequest;
import dev.playerblair.kuro.util.AuthenticationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LibraryService {

    private final LibraryEntryRepository libraryEntryRepository;
    private final AuthenticationHelper helper;
    private final MangaService mangaService;

    public LibraryService(LibraryEntryRepository libraryEntryRepository, AuthenticationHelper helper, MangaService mangaService) {
        this.libraryEntryRepository = libraryEntryRepository;
        this.helper = helper;
        this.mangaService = mangaService;
    }

    public List<LibraryEntry> getLibrary(Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is fetching their entire library", user);
        List<LibraryEntry> results = libraryEntryRepository.findAllByUser(user);
        log.info("Found {} library entries", results.size());
        return results;
    }

    public LibraryEntry getLibraryEntry(Long malId, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is fetching their LibraryEntry for Manga with malID: {}", user, malId);
        LibraryEntry entry = libraryEntryRepository.findByUserAndMalId(user, malId)
                .orElseThrow(() -> new LibraryEntryNotFoundException(malId));
        log.info("Found {}", entry);
        return entry;
    }

    public LibraryEntry createLibraryEntry(LibraryEntryRequest request, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is creating a LibraryEntry for Manga with malID: {}", user, request.malId());
        if (libraryEntryRepository.existsByUserAndMalId(user, request.malId())) {
            throw new LibraryEntryAlreadyExistsException(request.malId());
        }
        Manga manga = mangaService.getManga(request.malId());
        LibraryEntry entry = libraryEntryRepository.save(
                LibraryEntry.create(
                        user,
                        manga,
                        request.progress(),
                        request.chaptersRead(),
                        request.volumesRead(),
                        request.rating(),
                        request.notes()
                )
        );
        log.info("Created and persisted {}", entry);
        return entry;
    }

    public void updateLibraryEntry(Long malId, LibraryEntryRequest request, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        LibraryEntry entry = libraryEntryRepository.findByUserAndMalId(user, malId)
                .orElseThrow(() -> new LibraryEntryNotFoundException(malId));
        log.debug("{} is updating {}", user, entry);
        entry.update(request.progress(), request.chaptersRead(), request.volumesRead(), request.rating(), request.notes());
        libraryEntryRepository.save(entry);
        log.info("Updated {}", entry);
    }

    public void deleteLibraryEntry(Long malId, Authentication authentication) {
        User user = helper.getCurrentUser(authentication);
        log.debug("{} is deleting LibraryEntry for Manga with malID: {}", user, malId);
        LibraryEntry entry = libraryEntryRepository.findByUserAndMalId(user, malId)
                .orElseThrow(() -> new LibraryEntryNotFoundException(malId));
        libraryEntryRepository.delete(entry);
        log.info("Deleted {}", entry);
    }
}
