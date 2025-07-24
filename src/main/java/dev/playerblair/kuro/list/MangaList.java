package dev.playerblair.kuro.list;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.manga.model.Manga;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "manga_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MangaList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "list_status")
    private ListStatus listStatus;

    @ElementCollection
    @Column(name = "list_manga_ids")
    private List<Long> malIds;

    @ManyToMany
    @JoinTable(
            name = "list_manga",
            joinColumns = @JoinColumn(name = "list_id"),
            inverseJoinColumns = @JoinColumn(name = "manga_id")
    )
    private List<Manga> manga;
}
