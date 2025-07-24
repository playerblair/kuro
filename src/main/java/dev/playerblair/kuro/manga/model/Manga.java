package dev.playerblair.kuro.manga.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manga {

    @Id
    private Long malId;

    private String title;

    @Column(name = "title_english")
    private String titleEnglish;

    @Enumerated(EnumType.STRING)
    private MangaType type;

    private Integer chapters;

    private Integer volumes;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "manga_authors",
            joinColumns = @JoinColumn(name = "manga_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Genre> genres;

    private String url;

    private String imageUrl;

}
