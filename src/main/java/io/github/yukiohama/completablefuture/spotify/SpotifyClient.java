package io.github.yukiohama.completablefuture.spotify;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

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

    public Artist findArtist(String artistName) {
        log.info("Searching for artist \"{}\"...", artistName);

        HttpRequest request = requestBuilder.searchArtist(artistName);
        HttpResponse<JsonNode> response = send(request);

        JsonNode artistNode = response.body().at("/artists/items/0");

        return Mapper.fromJson(artistNode, Artist.class);
    }

    public List<Artist> fetchRelatedArtists(Artist artist) {
        log.info("Fetching related artists...");

        HttpRequest request = requestBuilder.fetchRelatedArtists(artist.getId());
        HttpResponse<JsonNode> response = send(request);

        JsonNode relatedArtistsNode = response.body().get("artists");

        List<Artist> relatedArtists = new ArrayList<>();

        for (JsonNode relatedArtistNode : relatedArtistsNode) {
            Artist relatedArtist = Mapper.fromJson(relatedArtistNode, Artist.class);
            relatedArtists.add(relatedArtist);
        }

        return relatedArtists;
    }

    public List<String> fetchArtistTopTracks(Artist artist) {
        log.info("Fetching top tracks of artist \"{}\"...", artist.getName());

        HttpRequest request = requestBuilder.fetchTopTracks(artist.getId());
        HttpResponse<JsonNode> response = send(request);

        JsonNode tracksNode = response.body().get("tracks");

        List<String> topTracks = new ArrayList<>();

        for (JsonNode trackNode : tracksNode) {
            String track = trackNode.get("name").asText();
            topTracks.add(track);
        }

        return topTracks;
    }

    private HttpResponse<JsonNode> send(HttpRequest request) {
        try {
            return httpClient.send(request, JsonNodeBodyHandler.getInstance());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
