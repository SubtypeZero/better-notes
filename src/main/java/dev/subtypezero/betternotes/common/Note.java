package dev.subtypezero.betternotes.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.api.ItemID;

import java.awt.*;

@Data
@NoArgsConstructor
public class Note implements DisplayAttributes {
    private String name;
    private Color color;
    private int iconItemId;
    private String notes;

    public Note(String name) {
        this.name = name;
        this.color = null;
        this.iconItemId = ItemID.CAKE_OF_GUIDANCE;
        this.notes = null;
    }
}
