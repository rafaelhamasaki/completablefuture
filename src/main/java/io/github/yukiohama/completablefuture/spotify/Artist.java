package io.github.yukiohama.completablefuture.spotify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Artist {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String id;

    private String name;

    @Setter
    private List<String> tracks;
}
