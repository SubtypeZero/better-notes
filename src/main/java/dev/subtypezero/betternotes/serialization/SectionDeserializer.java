package dev.subtypezero.betternotes.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import dev.subtypezero.betternotes.util.DataManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class SectionDeserializer extends JsonDeserializer<Section> {

    @Override
    public Section deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        Section section = new Section();
        section.setName(node.get("name").asText());
        section.setColor(parser.getCodec().treeToValue(node.get("color"), Color.class));
        section.setOpen(node.get("isOpen").asBoolean());

        List<String> noteNames = new ArrayList<>();
        node.get("noteNames").forEach(name -> noteNames.add(name.asText()));

        Map<String, Note> currentNotes = DataManager.getInstance().getNotes().stream()
                .collect(Collectors.toMap(Note::getName, Function.identity()));

        List<Note> notes = new ArrayList<>();

        for (String noteName : noteNames) {
            Note note = currentNotes.get(noteName);

            if (note != null) {
                notes.add(note);
            } else {
                log.error("Note not found: {}", noteName);
                notes.add(new Note()); // Indicate to the validator that a note couldn't be found
            }
        }
        section.setNotes(notes);

        return section;
    }
}
