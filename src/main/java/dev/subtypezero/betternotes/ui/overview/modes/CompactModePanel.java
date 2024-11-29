package dev.subtypezero.betternotes.ui.overview.modes;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.Note;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;

public class CompactModePanel extends JPanel {

    public CompactModePanel(BetterNotesPlugin plugin, Note note) {
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setPreferredSize(new Dimension(0, 24));

        NameActionsPanel<Note> nameActions = new NameActionsPanel<>(plugin, note, false, null, null);

        this.add(nameActions, BorderLayout.NORTH);
    }
}
