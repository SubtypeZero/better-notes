package dev.subtypezero.betternotes.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;

import java.io.IOException;

public class SectionSerializer extends JsonSerializer<Section> {

    @Override
    public void serialize(Section section, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeStringField("name", section.getName());
        generator.writeObjectField("color", section.getColor());
        generator.writeBooleanField("isOpen", section.isOpen());
        generator.writeArrayFieldStart("noteNames");
        for (Note note : section.getNotes()) {
            generator.writeString(note.getName());
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }
}
