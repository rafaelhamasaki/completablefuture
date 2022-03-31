package io.github.yukiohama.completablefuture.infrastructure;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;

public final class JsonNodeBodyHandler implements HttpResponse.BodyHandler<JsonNode> {

    private static JsonNodeBodyHandler instance;

    private JsonNodeBodyHandler() {
        // Empty constructor
    }

    public static JsonNodeBodyHandler getInstance() {
        if (instance == null) {
            instance = new JsonNodeBodyHandler();
        }
        return instance;
    }

    @Override
    public HttpResponse.BodySubscriber<JsonNode> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(upstream, Mapper::readTree);
    }
}
