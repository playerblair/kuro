package dev.playerblair.kuro.collection;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.collection.model.CollectionEntry;
import dev.playerblair.kuro.collection.model.CollectionType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CollectionEntryRepository extends ListCrudRepository<CollectionEntry, Long> {

    @Query("SELECT c FROM CollectionEntry c WHERE c.user = :user")
    List<CollectionEntry> findAllByUser(@Param("user") User user);

    @Query("SELECT c FROM CollectionEntry c WHERE c.user = :user AND c.manga.malId = :malId")
    List<CollectionEntry> findAllByUserAndMalId(@Param("user") User user, @Param("malId") Long malId);

    @Query("SELECT c FROM CollectionEntry c WHERE c.id = :id AND c.user = :user")
    Optional<CollectionEntry> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT COUNT(c) > 0 FROM CollectionEntry c WHERE c.user = :user AND " +
            "c.manga.malId = :malId AND c.type = :type AND c.volumeNumber = :volumeNumber" +
            " AND c.edition = :edition")
    boolean existsByUserAndMalIdAndTypeAndVolumeNumberAndEdition(
                @Param("user") User user,
                @Param("malId") Long malId,
                @Param("type") CollectionType type,
                @Param("volumeNumber") int volumeNumber,
                @Param("edition") String edition
            );

    @Modifying
    @Query("DELETE FROM CollectionEntry c WHERE c.id = :id AND c.user = :user")
    int deleteByIdAndUser(@Param("id") Long id, @Param("user") User user);


}
