package dev.playerblair.kuro.list;

import dev.playerblair.kuro.auth.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MangaListRepository extends ListCrudRepository<MangaList, Long> {

    @Query("SELECT m FROM MangaList m WHERE m.user = :user")
    List<MangaList> findAllByUser(@Param("user") User user);

    @Query("SELECT m FROM MangaList m WHERE m.id = :id AND m.user = :user")
    Optional<MangaList> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Modifying
    @Query("DELETE FROM MangaList m WHERE m.id = :id AND m.user = :user")
    int deleteByIdAndUser(@Param("id") Long id, @Param("user") User user);
}
