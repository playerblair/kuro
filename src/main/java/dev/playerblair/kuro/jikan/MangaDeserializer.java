package dev.playerblair.kuro.jikan;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.playerblair.kuro.model.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MangaDeserializer extends JsonDeserializer<Manga> {
    @Override
    public Manga deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        JsonNode chaptersNode = node.get("chapters");
        Integer chapters = (chaptersNode.isNull()) ? null : chaptersNode.asInt();

        JsonNode volumesNode = node.get("volumes");
        Integer volumes = (volumesNode.isNull()) ? null : volumesNode.asInt();

        Set<Author> authors = new HashSet<>();
        JsonNode authorsNode = node.get("authors");
        if (authorsNode != null && authorsNode.isArray()) {
            for (JsonNode authorNode : authorsNode) {
                Long id = authorNode.get("mal_id").asLong();
                String name = authorNode.get("name").asText();
                String url = authorNode.get("url").asText();
                authors.add(new Author(id, name, url));
            }
        }

        Set<Genre> genres = new HashSet<>();
        JsonNode genresNode = node.get("genres");
        if (genresNode != null && genresNode.isArray()) {
            for (JsonNode genreNode : genresNode) {
                genres.add(Genre.fromString(genreNode.get("name").asText()));
            }
        }

        return new Manga(
                node.get("mal_id").asLong(),
                node.get("title").asText(),
                node.get("title_english").asText(null),
                Type.fromString(node.get("type").asText()),
                chapters,
                volumes,
                Status.fromString(node.get("status").asText()),
                node.get("synopsis").asText(),
                authors,
                genres,
                node.get("url").asText(),
                node.path("images").path("jpg").path("image_url").asText(null)
        );
    }
}
