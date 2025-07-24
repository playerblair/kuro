package dev.playerblair.kuro.manga;

import dev.playerblair.kuro.manga.model.Manga;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MangaRepository extends ListCrudRepository<Manga, Long> {

    @Query("SELECT m FROM Manga m LEFT JOIN FETCH m.authors WHERE m.malId = :id")
    Optional<Manga> findByIdWithAuthors(@Param("id") Long id);

    @Query("SELECT m FROM Manga m WHERE m.malId IN :malIds")
    List<Manga> findAllByMalIdsIn(@Param("malIds") List<Long> malId);
}
