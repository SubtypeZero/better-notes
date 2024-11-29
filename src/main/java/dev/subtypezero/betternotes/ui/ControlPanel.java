package dev.subtypezero.betternotes.ui;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.ui.notes.NoteControls;
import dev.subtypezero.betternotes.ui.overview.OverviewControls;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

@Getter
public class ControlPanel extends JPanel {
    private final OverviewControls overviewControls;
    private final NoteControls noteControls;

    public ControlPanel(BetterNotesPlugin plugin, Supplier<Note> noteSupplier, Runnable backRunnable) {
        this.setLayout(new BorderLayout());

        overviewControls = new OverviewControls(plugin);
        noteControls = new NoteControls(plugin.getImportExportUtils(), noteSupplier, backRunnable);

        this.add(overviewControls, BorderLayout.NORTH);
        this.add(noteControls, BorderLayout.SOUTH);
    }

    public void showOverview() {
        noteControls.setVisible(false);
        noteControls.setTitle(null);
        overviewControls.setVisible(true);
    }

    public void showNote(Note note) {
        overviewControls.setVisible(false);
        noteControls.setTitle(note.getName());
        noteControls.setVisible(true);
    }
}
