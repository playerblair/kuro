package dev.playerblair.kuro.model;

import dev.playerblair.kuro.dto.LibraryEntryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LibraryEntryTest {

    private LibraryEntry testEntry;

    @BeforeEach
    public void setup() {
        Manga manga = new Manga();
        manga.setMalId(1L);
        testEntry = new LibraryEntry(
                1L,
                new User(),
                manga,
                Progress.PLANNING,
                0,
                0,
                5,
                "test"
        );
    }

    @Test
    public void testToString() {
        String expectedString = "LibraryEntry (ID:1) for Manga (malID:1)";
        assertEquals(expectedString, testEntry.toString());
    }

    @Test
    public void testCreate() {
        LibraryEntry newEntry = LibraryEntry.create(
                testEntry.getUser(),
                testEntry.getManga(),
                testEntry.getProgress(),
                testEntry.getChaptersRead(),
                testEntry.getVolumesRead(),
                testEntry.getRating(),
                testEntry.getNotes()
        );
        assertEquals(testEntry.getUser(), newEntry.getUser());
        assertEquals(testEntry.getManga(), newEntry.getManga());
        assertEquals(testEntry.getProgress(), newEntry.getProgress());
        assertEquals(testEntry.getChaptersRead(), newEntry.getChaptersRead());
        assertEquals(testEntry.getVolumesRead(), newEntry.getVolumesRead());
        assertEquals(testEntry.getRating(), newEntry.getRating());
        assertEquals(testEntry.getNotes(), newEntry.getNotes());
    }

    @Test
    public void testCreateException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> LibraryEntry.create(
                        testEntry.getUser(),
                        null,
                        testEntry.getProgress(),
                        testEntry.getChaptersRead(),
                        testEntry.getVolumesRead(),
                        testEntry.getRating(),
                        testEntry.getNotes()
                )
        );
    }

    @Test
    public void testUpdate() {
        testEntry.update(Progress.READING, 10, 1, 6, null);
        assertEquals(Progress.READING, testEntry.getProgress());
        assertEquals(10, testEntry.getChaptersRead());
        assertEquals(1, testEntry.getVolumesRead());
        assertEquals(6, testEntry.getRating());
        assertEquals("test", testEntry.getNotes());
    }

    @Test
    public void testToDto() {
        LibraryEntryDto expectedDto = new LibraryEntryDto(
                testEntry.getId(),
                testEntry.getManga(),
                testEntry.getProgress(),
                testEntry.getChaptersRead(),
                testEntry.getVolumesRead(),
                testEntry.getRating(),
                testEntry.getNotes()
        );
        assertEquals(expectedDto, testEntry.toDto());
    }
}
