package dev.playerblair.kuro.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.playerblair.kuro.jikan.MangaDeserializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = MangaDeserializer.class)
public class Manga {

    @Id
    @Column(name = "mal_id")
    private Long malId;

    private String title;

    @Column(name = "title_english")
    private String titleEnglish;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer chapters;

    private Integer volumes;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String synopsis;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "manga_authors",
            joinColumns = @JoinColumn(name = "mal_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable(name = "manga_genres", joinColumns = @JoinColumn(name = "mal_id"))
    private Set<Genre> genres;

    @Column(name = "image_url")
    private String imageUrl;

    private String url;

    @Override
    public String toString() {
        return "Manga (malID:" + malId + ")";
    }
}
