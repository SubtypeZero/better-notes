package dev.subtypezero.betternotes.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.subtypezero.betternotes.serialization.SectionDeserializer;
import dev.subtypezero.betternotes.serialization.SectionSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonSerialize(using = SectionSerializer.class)
@JsonDeserialize(using = SectionDeserializer.class)
public class Section implements DisplayAttributes {
    private String name;
    private Color color;
    private boolean isOpen;
    private List<Note> notes;

    public Section(String name) {
        this.name = name;
        this.color = null;
        this.isOpen = false;
        this.notes = new ArrayList<>();
    }
}
