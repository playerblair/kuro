package dev.playerblair.kuro.manga.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.playerblair.kuro.manga.model.Author;
import dev.playerblair.kuro.manga.model.Genre;
import dev.playerblair.kuro.manga.model.MangaType;
import dev.playerblair.kuro.manga.model.Status;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MangaDtoDeserializer extends JsonDeserializer<MangaDto> {

    @Override
    public MangaDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Set<Author> authors = new HashSet<>();
        JsonNode authorsNode = node.get("authors");
        if (authorsNode != null && authorsNode.isArray()) {
            for (JsonNode authorNode: authorsNode) {
                Author author = Author.builder()
                        .malId(authorNode.get("mal_id").asLong())
                        .name(authorNode.get("name").asText())
                        .url(authorNode.get("url").asText())
                        .build();
                authors.add(author);
            }
        }

        Set<Genre> genres = new HashSet<>();
        JsonNode genresNode = node.get("genres");
        if (genresNode != null && genresNode.isArray()) {
            for (JsonNode genreNode: genresNode) {
                genres.add(Genre.fromString(genreNode.get("name").asText()));
            }
        }

        return new MangaDto(
                node.get("mal_id").asLong(),
                node.get("title").asText(),
                node.get("title_english").asText(null),
                MangaType.fromString(node.get("type").asText()),
                node.get("chapters").asInt(),
                node.get("volumes").asInt(),
                Status.fromString(node.get("status").asText()),
                node.get("synopsis").asText(),
                authors,
                genres,
                node.get("url").asText(),
                node.path("images").path("jpg").path("image_url").asText(null)
        );

    }
}
