package dev.subtypezero.betternotes.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.awt.*;
import java.io.IOException;

public class ColorDeserializer extends JsonDeserializer<Color> {

    @Override
    public Color deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        int r = node.has("r") ? node.get("r").asInt() : 0;
        int g = node.has("g") ? node.get("g").asInt() : 0;
        int b = node.has("b") ? node.get("b").asInt() : 0;
        int a = node.has("a") ? node.get("a").asInt() : 255;
        return new Color(r, g, b, a);
    }
}
