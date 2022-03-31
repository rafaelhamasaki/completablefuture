package io.github.yukiohama.completablefuture.infrastructure;

import java.io.UncheckedIOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

public final class Mapper {

    //@formatter:off
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    //@formatter:on

    private Mapper() {
        throw new UnsupportedOperationException();
    }

    public static <T> T fromJson(JsonNode json, Class<T> clazz) {
        try {
            return JSON_MAPPER.treeToValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static JsonNode readTree(String json) {
        try {
            return JSON_MAPPER.readTree(json);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
