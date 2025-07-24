package dev.playerblair.kuro.collection.model;

import dev.playerblair.kuro.auth.model.User;
import dev.playerblair.kuro.manga.model.Manga;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "collection_entry",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "mal_id", "type", "volumeNumber, edition"}
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionEntry {

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
    @Column(name = "collection_type")
    private CollectionType type;

    @Column(name = "volume_number", nullable = false)
    private int volumeNumber;

    private String edition;

    @Column(name = "date_purchased")
    private LocalDate datePurchased;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
