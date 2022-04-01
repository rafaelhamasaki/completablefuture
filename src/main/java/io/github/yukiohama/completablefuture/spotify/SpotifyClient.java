package io.github.yukiohama.completablefuture.spotify;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.stereotype.Component;

import io.github.yukiohama.completablefuture.infrastructure.JsonNodeBodyHandler;
import io.github.yukiohama.completablefuture.infrastructure.Mapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SpotifyClient {

    private final SpotifyRequestBuilder requestBuilder;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    SpotifyClient(SpotifyRequestBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }

    public CompletableFuture<Artist> findArtist(String artistName) {
        log.info("Searching for artist \"{}\"...", artistName);

        HttpRequest request = requestBuilder.searchArtist(artistName);
        CompletableFuture<HttpResponse<JsonNode>> response = send(request);

        //@formatter:off
        return response
                .thenApplyAsync(HttpResponse::body)
                .thenApplyAsync(body -> body.at("/artists/items/0"))
                .thenApplyAsync(jsonNode -> Mapper.fromJson(jsonNode, Artist.class));
        //@formatter:on
    }

    public CompletableFuture<List<Artist>> fetchRelatedArtists(Artist artist) {
        log.info("Fetching related artists...");

        HttpRequest request = requestBuilder.fetchRelatedArtists(artist.getId());
        CompletableFuture<HttpResponse<JsonNode>> response = send(request);

        //@formatter:off
        return response
                .thenApplyAsync(HttpResponse::body)
                .thenApplyAsync(body -> body.get("artists"))
                .thenApplyAsync(relatedArtistsNode -> {
                    List<Artist> relatedArtists = new ArrayList<>();
                    for(JsonNode relatedArtistNode : relatedArtistsNode) {
                        Artist relatedArtist = Mapper.fromJson(relatedArtistNode, Artist.class);
                        relatedArtists.add(relatedArtist);
                    }
                    return relatedArtists;
                });
        //@formatter:on
    }

    public CompletableFuture<List<String>> fetchArtistTopTracks(Artist artist) {
        log.info("Fetching top tracks of artist \"{}\"...", artist.getName());

        HttpRequest request = requestBuilder.fetchTopTracks(artist.getId());
        CompletableFuture<HttpResponse<JsonNode>> response;

        // Failing a CompletableFuture in order to demonstrate exception handling.
        // This artist is related to Marina Sena, if you wish to replicate the failure.
        if ("Urias".equals(artist.getName())) {
            response = CompletableFuture.failedFuture(new RuntimeException());
        } else {
            response = send(request);
        }

        //@formatter:off
        return response
                .thenApplyAsync(HttpResponse::body)
                .thenApplyAsync(body -> body.get("tracks"))
                .thenApplyAsync(tracksNode -> {
                    List<String> topTracks = new ArrayList<>();
                    for(JsonNode trackNode : tracksNode) {
                        String track = trackNode.get("name").asText();
                        topTracks.add(track);
                    }
                    return topTracks;
                })
                .whenComplete((result, error) -> {
                    if(error != null) {
                        log.error("Failed fetching top tracks of artist \"{}\"", artist.getName());
                    } else {
                        log.info("Fetching top tracks of artist \"{}\" succeeded", artist.getName());
                    }
                }).exceptionally(error -> List.of("Failed fetching top tracks"));
        //@formatter:on
    }

    private CompletableFuture<HttpResponse<JsonNode>> send(HttpRequest request) {
        return httpClient.sendAsync(request, JsonNodeBodyHandler.getInstance());
    }
}
