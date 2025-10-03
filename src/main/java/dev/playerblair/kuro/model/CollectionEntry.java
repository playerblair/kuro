package dev.playerblair.kuro.model;

import dev.playerblair.kuro.dto.CollectionEntryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "collection_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_entry_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mal_id", nullable = false)
    private Manga manga;

    @Column(name = "edition", nullable = false)
    private String edition;

    @Column(name = "volume_number")
    private int volumeNumber;

    private String notes;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    public static CollectionEntry create
            (User user, Manga manga, String edition, Integer volumeNumber, String notes, LocalDate purchaseDate) {
        CollectionEntry entry = new CollectionEntry();

        if (user == null) throw new IllegalArgumentException("User cannot be null");
        entry.user = user;

        if (manga == null) throw new IllegalArgumentException("Manga cannot be null");
        entry.manga = manga;

        if (edition == null) throw new IllegalArgumentException("Edition cannot be null");
        entry.edition = edition;

        if (volumeNumber == null) throw new IllegalArgumentException("Volume number cannot be null");
        entry.volumeNumber = volumeNumber;

        if (notes == null) notes = "";
        entry.notes = notes;

        entry.purchaseDate = purchaseDate;

        return entry;
    }

    public void update(String notes, LocalDate purchaseDate) {
        if (notes != null) this.notes = notes;
        if (purchaseDate != null) this.purchaseDate = purchaseDate;
    }

    public CollectionEntryDto toDto() {
        return new CollectionEntryDto(
                id,
                manga,
                edition,
                volumeNumber,
                notes,
                purchaseDate
        );
    }

    @Override
    public String toString() {
        return "CollectionEntry (ID:" + id + ","
                + "Edition:" + edition + ","
                + "Volume:" + volumeNumber + ")"
                + " for " + this.manga;
    }
}
