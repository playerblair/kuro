package dev.playerblair.kuro.repository;

import dev.playerblair.kuro.model.CollectionEntry;
import dev.playerblair.kuro.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionEntryRepository extends ListCrudRepository<CollectionEntry, Long> {

    @Query("SELECT c FROM CollectionEntry c WHERE c.user = :user")
    List<CollectionEntry> findAllByUser(User user);

    @Query("SELECT c FROM CollectionEntry c WHERE id = :id AND c.user = :user")
    Optional<CollectionEntry> findByIdAndUser(Long id, User user);
}
