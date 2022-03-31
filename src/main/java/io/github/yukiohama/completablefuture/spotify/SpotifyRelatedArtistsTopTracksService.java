package io.github.yukiohama.completablefuture.spotify;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class SpotifyRelatedArtistsTopTracksService {

    private final SpotifyClient client;

    SpotifyRelatedArtistsTopTracksService(SpotifyClient client) {
        this.client = client;
    }

    public List<Artist> findRelatedArtistsTopTracks(String artistName) {
        // 1. Find artist by name
        Artist artist = client.findArtist(artistName);

        // 2. Search for related artists
        List<Artist> relatedArtists = client.fetchRelatedArtists(artist);

        // 3. Fetch related artists' top tracks
        for (Artist relatedArtist : relatedArtists) {
            List<String> tracks = client.fetchArtistTopTracks(relatedArtist);
            relatedArtist.setTracks(tracks);
        }

        return relatedArtists;
    }
}
