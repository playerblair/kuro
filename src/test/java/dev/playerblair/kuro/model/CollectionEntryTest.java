package dev.playerblair.kuro.model;

import dev.playerblair.kuro.dto.CollectionEntryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionEntryTest {

    private CollectionEntry testEntry;

    @BeforeEach
    public void setup() {
        Manga manga = new Manga();
        manga.setMalId(1L);
        testEntry = new CollectionEntry(
                1L,
                new User(),
                manga,
                "standard",
                1,
                "notes",
                LocalDate.now()
        );
    }

    @Test
    public void testToString() {
        String expectedString = "CollectionEntry (ID:1,Edition:standard,Volume:1) for Manga (malID:1)";
        assertEquals(expectedString, testEntry.toString());
    }

    @Test
    public void testCreate() {
        CollectionEntry newEntry = CollectionEntry.create(
                testEntry.getUser(),
                testEntry.getManga(),
                testEntry.getEdition(),
                testEntry.getVolumeNumber(),
                testEntry.getNotes(),
                testEntry.getPurchaseDate()
        );
        assertEquals(testEntry.getUser(), newEntry.getUser());
        assertEquals(testEntry.getManga(), newEntry.getManga());
        assertEquals(testEntry.getEdition(), newEntry.getEdition());
        assertEquals(testEntry.getVolumeNumber(), newEntry.getVolumeNumber());
        assertEquals(testEntry.getNotes(), newEntry.getNotes());
        assertEquals(testEntry.getPurchaseDate(), newEntry.getPurchaseDate());
    }

    @Test
    public void testCreateException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CollectionEntry.create(
                        testEntry.getUser(),
                        testEntry.getManga(),
                        null,
                        testEntry.getVolumeNumber(),
                        testEntry.getNotes(),
                        testEntry.getPurchaseDate()
                )
        );
    }

    @Test
    public void testUpdate() {
        testEntry.update("test two", null);
        assertEquals("test two", testEntry.getNotes());
        assertNotNull(testEntry.getPurchaseDate());
    }

    @Test
    public void testToDto() {
        CollectionEntryDto expectedDto = new CollectionEntryDto(
                testEntry.getId(),
                testEntry.getManga(),
                testEntry.getEdition(),
                testEntry.getVolumeNumber(),
                testEntry.getNotes(),
                testEntry.getPurchaseDate()
        );
        assertEquals(expectedDto, testEntry.toDto());
    }
}
