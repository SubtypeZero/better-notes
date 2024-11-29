package dev.subtypezero.betternotes.ui.notes;

import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;

public class EmptyNotesPanel extends JPanel {

    public EmptyNotesPanel() {
        PluginErrorPanel errorPanel = new PluginErrorPanel();
        errorPanel.setContent("Better Notes", "Add note to get started.");

        this.add(errorPanel);
    }
}
