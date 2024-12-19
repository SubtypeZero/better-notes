package dev.subtypezero.betternotes.ui;

import dev.subtypezero.betternotes.ui.notes.NotePanel;
import dev.subtypezero.betternotes.ui.overview.OverviewPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import java.awt.*;

public class MainPanelSouth extends JPanel {
    private JPanel contentWrapper;

    private OverviewPanel overviewPanel;
    private NotePanel notesPanel;
    private JPanel emptyNotesPanel;

    public MainPanelSouth(OverviewPanel overviewPanel, NotePanel notesPanel) {
        this.setLayout(new BorderLayout());

        this.overviewPanel = overviewPanel;
        this.notesPanel = notesPanel;

        setupContentWrapper();

        JScrollPane contentWrapperPane = new JScrollPane(contentWrapper);
        contentWrapperPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(contentWrapperPane, BorderLayout.CENTER);
    }

    private void setupContentWrapper() {
        contentWrapper = new JPanel(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Empty notes panel
        emptyNotesPanel = new JPanel();
        PluginErrorPanel errorPanel = new PluginErrorPanel();
        errorPanel.setContent("Better Notes", "Add a note."); // TODO Replace title with constant
        emptyNotesPanel.add(errorPanel);
        emptyNotesPanel.setVisible(false);

        contentPanel.add(overviewPanel);
        contentPanel.add(notesPanel);
        //contentPanel.add(emptyNotesPanel);

        contentWrapper.add(contentPanel);
    }
}
