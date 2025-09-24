package dev.playerblair.kuro.model;

import dev.playerblair.kuro.dto.LibraryEntryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "library_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "library_entry_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "mal_id", nullable = false)
    private Manga manga;

    @Enumerated(EnumType.STRING)
    @Column(name = "progress", nullable = false)
    private Progress progress;

    @Column(name = "chapters_read")
    private int chaptersRead;

    @Column(name = "volumes_read")
    private int volumesRead;

    private int rating;

    private String notes;

    public static LibraryEntry toLibraryEntry
            (User user, Manga manga, Progress progress, Integer chaptersRead, Integer volumesRead, Integer rating, String notes) {
        LibraryEntry entry = new LibraryEntry();

        if (user == null) throw new IllegalArgumentException("User cannot be null");
        entry.user = user;

        if (manga == null) throw new IllegalArgumentException("Manga cannot be null");
        entry.manga = manga;

        if (progress == null) progress = Progress.PLANNING;
        entry.progress = progress;

        if (chaptersRead == null) chaptersRead = 0;
        entry.chaptersRead = chaptersRead;

        if (volumesRead == null) volumesRead = 0;
        entry.volumesRead = volumesRead;

        if (rating == null) rating = 5;
        entry.rating = rating;

        if (notes == null) notes = "";
        entry.notes = notes;

        return entry;
    }

    public void update(Progress progress, Integer chaptersRead, Integer volumesRead, Integer rating, String notes) {
        if (progress != null) this.progress = progress;
        if (chaptersRead != null) this.chaptersRead = chaptersRead;
        if (volumesRead != null) this.volumesRead = volumesRead;
        if (rating != null) this.rating = rating;
        if (notes != null) this.notes = notes;
    }

    public LibraryEntryDto toDto() {
        return new LibraryEntryDto(
                id,
                manga,
                progress,
                chaptersRead,
                volumesRead,
                rating,
                notes
        );
    }

    @Override
    public String toString() {
        return "LibraryEntry (ID:" + id + ") for " + this.manga;
    }
}
