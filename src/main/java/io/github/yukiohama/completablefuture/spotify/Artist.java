package io.github.yukiohama.completablefuture.spotify;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Artist {
    private String id;
    private String name;

    @Setter
    private List<String> tracks;
}
