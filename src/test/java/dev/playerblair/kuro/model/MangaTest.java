package dev.playerblair.kuro.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MangaTest {

    @Test
    public void testToString() {
        Manga testManga = new Manga();
        testManga.setMalId(1L);
        assertEquals("Manga (malID:1)", testManga.toString());
    }
}
