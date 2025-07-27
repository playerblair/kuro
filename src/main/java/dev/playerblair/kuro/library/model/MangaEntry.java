package dev.playerblair.kuro.library.model;

import dev.playerblair.kuro.manga.model.Manga;
import dev.playerblair.kuro.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "manga_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MangaEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "manga_id", nullable = false)
    private Manga manga;

    @Enumerated(EnumType.STRING)
    private Progress progress = Progress.PLANNING;

    @Column(name = "chapters_read")
    private int chaptersRead = 0;

    @Column(name = "volumes_read")
    private int volumesRead = 0;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String notes;

}
