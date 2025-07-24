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
import dev.playerblair.kuro.manga.model.Manga;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class LibraryService {

    private final MangaEntryRepository mangaEntryRepository;
    private final MangaService mangaService;
    private final TokenService tokenService;

    public LibraryService(MangaEntryRepository mangaEntryRepository, MangaService mangaService, TokenService tokenService) {
        this.mangaEntryRepository = mangaEntryRepository;
        this.mangaService = mangaService;
        this.tokenService = tokenService;
    }

    public List<MangaEntryDto> getLibrary(Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is fetching their manga library.", user.getId());

        List<MangaEntry> library = mangaEntryRepository.findAllByUser(user);
        if (library.isEmpty()) {
            log.debug("No manga entries found for User (ID:{}).", user.getId());
            return List.of();
        }

        log.debug("Found {} manga entries for User (ID:{}).", library.size(), user.getId());
        return library.stream()
                .map(this::toMangaEntryDto)
                .toList();
    }

    public MangaEntryDto getMangaEntry(Long malId, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is fetching MangaEntry for Manga (malID:{}).", user.getId(), malId);

        MangaEntry mangaEntry = mangaEntryRepository.findAllByUserAndMalId(user, malId)
                .orElseThrow(() -> new MangaEntryNotFoundException(malId));

        log.debug("Fetched MangaEntry for Manga (malID:{}) for User (ID:{}).", malId, user.getId());
        return toMangaEntryDto(mangaEntry);
    }

    @Transactional
    public MangaEntryDto createMangaEntry(MangaEntryRequest request, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is creating MangaEntry for Manga (malID:{}).", user.getId(), request.malId());

        if (mangaEntryRepository.existsByUserAndMalId(user, request.malId())) {
            throw new MangaEntryAlreadyExistsException(request.malId());
        }

        Manga manga = mangaService.saveManga(request.malId());

        MangaEntry mangaEntry = MangaEntry.builder()
                .user(user)
                .manga(manga)
                .progress(Progress.PLANNING)
                .build();

        editMangaEntry(mangaEntry, request);

        MangaEntry saved = mangaEntryRepository.save(mangaEntry);

        log.debug("MangaEntry created for User (ID:{}) and Manga (malID:{}).", user.getId(), request.malId());
        return toMangaEntryDto(saved);
    }

    @Transactional
    public void updateMangaEntry(Long malId, MangaEntryRequest request, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is updating MangaEntry for Manga (malID:{}).", user.getId(), malId);

        MangaEntry mangaEntry = mangaEntryRepository.findByUserAndMalId(user, malId)
                .orElseThrow(() -> new MangaEntryNotFoundException(malId));

        editMangaEntry(mangaEntry, request);

        mangaEntryRepository.save(mangaEntry);

        log.debug("MangaEntry updated for User (ID:{}) and Manga (malID:{}).", user.getId(), request.malId());
    }

    @Transactional
    public void deleteMangaEntry(Long malId, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is deleting MangaEntry for Manga (malID:{}).", user, malId);

        int deleted = mangaEntryRepository.deleteByUserAndMalId(user, malId);

        if (deleted == 0) {
            log.debug("No MangaEntry for Manga (malID:{}) found to delete for User (ID:{}).", malId, user);
        } else {
            log.debug("Deleted MangaEntry for Manga (malID:{}) for User (ID:{}).", malId, user);
        }
    }

    public MangaEntryDto toMangaEntryDto(MangaEntry mangaEntry) {
        return new MangaEntryDto(
                mangaService.toMangaDto(mangaEntry.getManga()),
                mangaEntry.getProgress(),
                mangaEntry.getVolumesRead(),
                mangaEntry.getChaptersRead(),
                mangaEntry.getRating(),
                mangaEntry.getNotes()
        );
    }

    private void editMangaEntry(MangaEntry mangaEntry, MangaEntryRequest request) {
        if (request.progress() != null) {
            mangaEntry.setProgress(request.progress());
        }

        if (request.chaptersRead() != null) {
            mangaEntry.setChaptersRead(request.chaptersRead());
        }

        if (request.volumesRead() != null) {
            mangaEntry.setVolumesRead(request.volumesRead());
        }

        if (request.rating() != null) {
            mangaEntry.setRating(request.rating());
        }

        if (request.notes() != null) {
            mangaEntry.setNotes(request.notes());
        }
    }
}
