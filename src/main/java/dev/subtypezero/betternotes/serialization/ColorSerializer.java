package dev.subtypezero.betternotes.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.awt.*;
import java.io.IOException;

public class ColorSerializer extends JsonSerializer<Color> {

    @Override
    public void serialize(Color color, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeNumberField("r", color.getRed());
        generator.writeNumberField("g", color.getGreen());
        generator.writeNumberField("b", color.getBlue());
        generator.writeNumberField("a", color.getAlpha());
        generator.writeEndObject();
    }
}
