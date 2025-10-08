package dev.playerblair.kuro.service;

import dev.playerblair.kuro.exception.JikanApiException;
import dev.playerblair.kuro.jikan.JikanClient;
import dev.playerblair.kuro.jikan.response.JikanGetResponse;
import dev.playerblair.kuro.jikan.response.JikanSearchResponse;
import dev.playerblair.kuro.model.Manga;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MangaServiceMockTest {

    @Mock
    private JikanClient jikanClient;

    @InjectMocks
    private MangaService mangaService;

    @Test
    public void whenSearchMangaIsCalled_returnSearchResponse() {
        // given
        when(jikanClient.searchManga("test", 1)).thenReturn(mock(JikanSearchResponse.class));

        // when
        JikanSearchResponse response = mangaService.searchManga("test", 1);

        // given
        verify(jikanClient).searchManga("test", 1);
    }

    @Test
    public void whenSearchMangaIsCalled_givenJikanApiError_throwException() {
        // given
        when(jikanClient.searchManga("test", 1)).thenThrow(JikanApiException.class);

        // when & then
        assertThrows(
                JikanApiException.class,
                () -> mangaService.searchManga("test", 1)
        );
        verify(jikanClient).searchManga("test", 1);
    }

    @Test
    public void whenGetIsCalled_givenJikanApiError_returnManga() {
        // given
        Manga mockManga = mock(Manga.class);
        when(jikanClient.getManga(1L)).thenReturn(new JikanGetResponse(mockManga));

        // when
        Manga manga = mangaService.getManga(1L);

        // then
        assertEquals(mockManga, manga);
        verify(jikanClient).getManga(1L);

    }

    @Test
    public void whenGetMangaIsCalled_givenJikanApiError_throwException() {
        // given
        when(jikanClient.getManga(1L)).thenThrow(JikanApiException.class);

        // when & then
        assertThrows(
                JikanApiException.class,
                () -> mangaService.getManga(1L)
        );
    }
}
