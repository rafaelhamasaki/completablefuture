package io.github.yukiohama.completablefuture.spotify;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SpotifyRelatedArtistsTopTracksController {

    private final SpotifyRelatedArtistsTopTracksService service;

    SpotifyRelatedArtistsTopTracksController(SpotifyRelatedArtistsTopTracksService service) {
        this.service = service;
    }

    @GetMapping
    public Map<String, Collection<Artist>> listRelatedArtistTracks(@RequestParam(name = "artist") List<String> artistNames) {
        long start = System.nanoTime();

        log.info("Finding top tracks of artists related to \"{}\"...", artistNames);

        Map<String, Collection<Artist>> results = new HashMap<>();
        Map<String, CompletableFuture<Collection<Artist>>> futures = new HashMap<>();

        for (String artistName : artistNames) {
            CompletableFuture<Collection<Artist>> relatedArtistsTopTracks = service.findRelatedArtistsTopTracks(artistName);
            futures.put(artistName, relatedArtistsTopTracks);
        }

        for (Map.Entry<String, CompletableFuture<Collection<Artist>>> future : futures.entrySet()) {
            String artistName = future.getKey();
            Collection<Artist> relatedArtistsAndTracks = future.getValue().join();
            results.put(artistName, relatedArtistsAndTracks);
        }

        log.info("Query took {} milliseconds.", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));

        return results;
    }
}
