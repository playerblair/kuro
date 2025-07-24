package dev.playerblair.kuro.library;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.library.model.MangaEntry;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MangaEntryRepository extends ListCrudRepository<MangaEntry, Long> {

    @Query("SELECT m FROM MangaEntry m WHERE m.user = :user")
    List<MangaEntry> findAllByUser(@Param("user") User user);

    @Query("SELECT m FROM MangaEntry m WHERE m.user = :user and m.manga.malId = :malId")
    Optional<MangaEntry> findAllByUserAndMalId(@Param("user") User user, @Param("malId") Long malId);

    @Query("SELECT Count(m) > 0 FROM MangaEntry m WHERE m.user = :user and m.manga.malId = :malId")
    boolean existsByUserAndMalId(@Param("user") User user, @Param("malId") Long malId);

    @Query("SELECT m FROM MangaEntry m WHERE m.user = :user and m.manga.malId = :malId")
    Optional<MangaEntry> findByUserAndMalId(@Param("user") User user, @Param("malId") Long malId);

    @Modifying
    @Query("DELETE FROM MangaEntry m WHERE m.user = :user AND m.manga.malId = :malId")
    int deleteByUserAndMalId(@Param("user") User user, @Param("malId") Long malId);

    @Query("SELECT m FROM MangaEntry m WHERE m.user = :user AND m.manga.malId IN :malIds")
    List<MangaEntry> findAllByUserAndMalIdsIn(@Param("user") User user, @Param("malIds") List<Long> malId);
}
