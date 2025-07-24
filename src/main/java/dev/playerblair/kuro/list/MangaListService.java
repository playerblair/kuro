package dev.playerblair.kuro.list;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.auth.service.TokenService;
import dev.playerblair.kuro.list.dto.MangaListDto;
import dev.playerblair.kuro.list.dto.MangaListRequest;
import dev.playerblair.kuro.list.dto.MangaListShortFormDto;
import dev.playerblair.kuro.manga.MangaService;
import dev.playerblair.kuro.manga.model.Manga;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MangaListService {

    private final MangaListRepository mangaListRepository;
    private final MangaService mangaService;
    private final TokenService tokenService;

    public MangaListService(MangaListRepository mangaListRepository, MangaService mangaService, TokenService tokenService) {
        this.mangaListRepository = mangaListRepository;
        this.mangaService = mangaService;
        this.tokenService = tokenService;
    }

    public List<MangaListShortFormDto> getLists(Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is fetching their manga lists.", user.getId());

        List<MangaList> mangaLists = mangaListRepository.findAllByUser(user);
        if (mangaLists.isEmpty()) {
            log.debug("No manga lists found for User (ID:{}).", user.getId());
        }

        log.debug("Fetched {} manga lists for User (ID:{}).", mangaLists.size(), user.getId());
        return mangaLists.stream()
                .map(this::toMangaListShortFormDto)
                .toList();
    }

    public MangaListDto getMangaList(Long id, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is fetching MangaList (ID:{}).", user.getId(), id);

        MangaList mangaList = mangaListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new MangaListNotFoundException(id));

        log.debug("Fetched MangaList (ID:{}) for User (ID:{}).", id, user.getId());
        return toMangaListDto(mangaList);
    }

    @Transactional
    public MangaListDto createMangaList(MangaListRequest request, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is creating a MangaList with name:{}.", user.getId(), request.name());

        MangaList mangaList = MangaList.builder()
                .user(user)
                .build();

        editMangaList(mangaList, request);

        MangaList saved = mangaListRepository.save(mangaList);

        if (saved.getListStatus() == ListStatus.POPULATING) {
            populateList(saved.getId());
        }

        log.debug("Created MangaList (ID:{}) for User (ID:{}).", saved.getId(), user.getId());
        return toMangaListDto(saved);
    }

    @Transactional
    public void updateMangaList(Long id, MangaListRequest request, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is updating MangaList (ID:{}).", user.getId(), id);

        MangaList mangaList = mangaListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new MangaListNotFoundException(id));

        editMangaList(mangaList, request);

        MangaList updated = mangaListRepository.save(mangaList);

        if (updated.getListStatus() == ListStatus.POPULATING) {
            populateList(updated.getId());
        }

        log.debug("Updated MangaList (ID:{}) for User (ID:{}).", id, user.getId());
    }

    @Transactional
    public void deleteMangaList(Long id, Authentication authentication) {
        User user = tokenService.getUserFromAuthentication(authentication);
        log.debug("User (ID:{}) is deleting MangaList (ID:{}).", user.getId(), id);

        int deleted = mangaListRepository.deleteByIdAndUser(id, user);

        if (deleted == 0) {
            log.debug("No MangaList (ID:{}) found to delete for User (ID:{})", id, user.getId());
        } else {
            log.debug("Deleted MangaList (ID:{}) for User (ID:{}).", id, user.getId());
        }
    }

    @Async
    @Transactional
    private void populateList(Long id) {
        log.debug("Populating Manga of MangaList (ID:{}).", id);

        MangaList mangaList = mangaListRepository.findById(id)
                .orElseThrow(() -> new MangaListNotFoundException(id));

        List<Manga> manga = mangaList.getMalIds().parallelStream()
                .map(mangaService::saveManga)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        mangaList.setManga(manga);

        if (mangaList.getMalIds().size() == manga.size()) {
            mangaList.setListStatus(ListStatus.COMPLETE);
            log.debug("Populated MangaList (ID:{}) with {} Manga.", id, manga.size());
        } else {
            log.warn("Partially populating MangaList (ID:{}) with {}/{} Manga.",
                    id, manga.size(), mangaList.getMalIds().size());
        }

        mangaListRepository.save(mangaList);
    }

    private void editMangaList(MangaList mangaList, MangaListRequest request) {
        mangaList.setName(request.name());

        if (request.description() != null) {
            mangaList.setDescription(request.description());
        }

        if (request.manga() != null) {;
            mangaList.setListStatus(ListStatus.POPULATING);
            mangaList.setMalIds(request.manga());
        }
    }

    private MangaListDto toMangaListDto(MangaList mangaList) {
        return new MangaListDto(
                mangaList.getId(),
                mangaList.getName(),
                mangaList.getDescription(),
                mangaList.getListStatus(),
                mangaList.getMalIds(),
                mangaList.getManga().stream()
                        .map(mangaService::toMangaDto)
                        .toList()
        );
    }

    private MangaListShortFormDto toMangaListShortFormDto(MangaList mangaList) {
        return new MangaListShortFormDto(
                mangaList.getId(),
                mangaList.getName(),
                mangaList.getDescription(),
                mangaList.getListStatus(),
                mangaList.getMalIds(),
                mangaList.getManga().size()
        );
    }
}
