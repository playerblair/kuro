package dev.playerblair.kuro.repository;

import dev.playerblair.kuro.model.Manga;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MangaRepository extends CrudRepository<Manga, Long> {
}
