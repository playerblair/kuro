package dev.playerblair.kuro.repository;

import dev.playerblair.kuro.model.LibraryEntry;
import dev.playerblair.kuro.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryEntryRepository extends ListCrudRepository<LibraryEntry, Long> {

    @Query("SELECT l FROM LibraryEntry l WHERE l.user = :user")
    List<LibraryEntry> findAllByUser(User user);

    @Query("SELECT l FROM LibraryEntry l WHERE l.user = :user AND l.manga.malId = :malId")
    Optional<LibraryEntry> findByUserAndMalId(User user, Long malId);

    @Query("SELECT COUNT(l) > 0 FROM LibraryEntry l WHERE l.user = :user AND l.manga.malId = :malId")
    boolean existsByUserAndMalId(User user, Long malId);
}
