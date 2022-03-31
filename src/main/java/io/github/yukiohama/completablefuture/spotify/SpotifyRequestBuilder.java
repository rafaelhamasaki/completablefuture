package io.github.yukiohama.completablefuture.spotify;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.yukiohama.completablefuture.infrastructure.JsonNodeBodyHandler;

@Component
public class SpotifyRequestBuilder {

    private static final String ROOT_URL = "https://api.spotify.com/v1";

    private final String clientId;
    private final String clientSecret;

    private String accessToken;

    SpotifyRequestBuilder(@Value("${clientId}") String clientId, @Value("${clientSecret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @PostConstruct
    void init() {
        this.accessToken = getAccessToken();
    }

    public HttpRequest searchArtist(String artistName) {
        return buildRequest("/search?type=artist&q=" + URLEncoder.encode(artistName, UTF_8));
    }

    public HttpRequest fetchRelatedArtists(String artistId) {
        return buildRequest("/artists/" + artistId + "/related-artists");
    }

    public HttpRequest fetchTopTracks(String artistId) {
        return buildRequest("/artists/" + artistId + "/top-tracks?country=BR");
    }

    private HttpRequest buildRequest(String path) {
        //@formatter:off
        return HttpRequest.newBuilder(URI.create(ROOT_URL + path))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .build();
        //@formatter:on
    }

    private String getAccessToken() {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://accounts.spotify.com/api/token"))
                    .header("Authorization", "Basic " + Base64.getEncoder()
                            .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials")).build();

            JsonNode responseBody = HttpClient.newHttpClient().send(request, JsonNodeBodyHandler.getInstance()).body();
            return responseBody.path("access_token").asText();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
