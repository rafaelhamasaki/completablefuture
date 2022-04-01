package io.github.yukiohama.completablefuture.spotify;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class SpotifyRelatedArtistsTopTracksService {

    private final SpotifyClient client;

    SpotifyRelatedArtistsTopTracksService(SpotifyClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public CompletableFuture<Collection<Artist>> findRelatedArtistsTopTracks(String artistName) {

        //@formatter:off
        return client.findArtist(artistName) // 1. Find artist by name
                .thenComposeAsync(client::fetchRelatedArtists) // 2. Search for related artists
                .thenApplyAsync(Collection::stream)
                .thenApplyAsync(stream -> stream.collect(Collectors.toMap(Function.identity(), client::fetchArtistTopTracks))) // 3. Fetch related artists' top tracks
                .whenComplete((artistsMap, error) -> {
                    CompletableFuture<List<String>>[] futures = artistsMap.values().toArray(CompletableFuture[]::new);
                    CompletableFuture.allOf(futures).join();
                    artistsMap.forEach((artist, tracks) -> artist.setTracks(tracks.join()));
                }).thenApplyAsync(Map::keySet);
        //@formatter:on
    }
}



