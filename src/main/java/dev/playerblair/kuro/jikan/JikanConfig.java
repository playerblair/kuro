package dev.playerblair.kuro.jikan;

import dev.playerblair.kuro.exception.JikanApiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.net.http.HttpRequest;

@Configuration
public class JikanConfig {

    @Bean
    public JikanClient jikanClient(RestClient.Builder builder) {
        RestClient client = builder
                .baseUrl("https://api.jikan.moe")
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            int statusCode = response.getStatusCode().value();
                            String message = switch (statusCode) {
                                case 404 -> "Manga not found";
                                case 429 -> "Jikan API rate limit exceeded";
                                case 500 -> "Jikan API encountered an internal error";
                                case 503 -> "Jikan API service is temporarily unavailable";
                                default -> "Unexpected error occurred while accessing Jikan API";
                            };
                            throw new JikanApiException(statusCode, message);
                        }
                )
                .build();
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(JikanClient.class);
    }
}
