package dev.subtypezero.betternotes;

import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import dev.subtypezero.betternotes.util.DataManager;
import dev.subtypezero.betternotes.util.ImportExportUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;

import static dev.subtypezero.betternotes.util.ImageUtils.loadImage;

@Slf4j
@PluginDescriptor(name = "Better Notes")
public class BetterNotesPlugin extends Plugin {
    public static final int MAX_NAME_LENGTH = 50;

    @Inject
    @Getter
    private Client client;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    @Getter
    private ClientThread clientThread;

    @Inject
    @Getter
    private ColorPickerManager colorPickerManager;

    @Inject
    @Getter
    private ItemManager itemManager;

    @Inject
    private ConfigManager configManager;

    @Getter
    private DataManager dataManager;

    @Getter
    private ImportExportUtils importExportUtils;

    @Getter
    private BetterNotesPluginPanel panel;

    private NavigationButton navBtn;

    @Override
    protected void startUp() throws Exception {
        dataManager = DataManager.getInstance(configManager);
        importExportUtils = new ImportExportUtils(this);
        panel = new BetterNotesPluginPanel(this);

        BufferedImage icon = loadImage("/betternotes.png");

        navBtn = NavigationButton.builder()
                .tooltip("Better Notes")
                .icon(icon)
                .priority(7)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navBtn);
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navBtn);
    }

    public void addNote() {
        String name = JOptionPane.showInputDialog(panel,
                "Enter the name of the note (max " + MAX_NAME_LENGTH + " chars).",
                "Add New Note",
                JOptionPane.PLAIN_MESSAGE);

        if (name == null || name.isEmpty()) {
            return;
        }

        if (name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH);
        }

        String finalName = name;
        boolean allowAction = dataManager.getNotes().stream()
                .noneMatch(n -> n.getName().equals(finalName));

        if (allowAction) {
            Note note = new Note(finalName);
            dataManager.addNote(note);
            panel.updateOverview();
        } else {
            JOptionPane.showMessageDialog(panel,
                    "A note with the name '" + name + "' already exists",
                    "Note Already Exists",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeNote(Note note) {
        dataManager.removeNote(note);
        updateOverview();
    }

    public void addSection() {
        String name = JOptionPane.showInputDialog(panel,
                "Enter the name of the section (max " + MAX_NAME_LENGTH + " chars).",
                "Add New Section",
                JOptionPane.PLAIN_MESSAGE);

        if (name == null || name.isEmpty()) {
            return;
        }

        if (name.equals("Unassigned")) {
            JOptionPane.showMessageDialog(panel,
                    "The section name 'Unassigned' is reserved for Notes without a section",
                    "Section Name Reserved",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (name.length() > MAX_NAME_LENGTH) {
            name = name.substring(0, MAX_NAME_LENGTH);
        }

        String finalName = name;
        boolean allowAction = dataManager.getSections().stream()
                .noneMatch(s -> s.getName().equals(finalName));

        if (allowAction) {
            Section section = new Section(finalName);
            dataManager.addSection(section);
            panel.updateOverview();
        } else {
            JOptionPane.showMessageDialog(panel,
                    "A section with the name '" + name + "' already exists",
                    "Section Already Exists",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeSection(Section section) {
        dataManager.removeSection(section);
        updateOverview();
    }

    public void showNote(Note note) {
        panel.showNote(note);
    }

    public void updateOverview() {
        panel.updateOverview();
    }

    public int showPositionDialog(String type, int currentIndex, int size) {
        String positionString = JOptionPane.showInputDialog(panel,
                "Enter a position between 1 and " + size +
                        ". Current " + type + " is in position " + (currentIndex + 1) + ".",
                "Move Item",
                JOptionPane.PLAIN_MESSAGE);

        if (positionString == null) {
            return -1;
        }

        try {
            return Integer.parseInt(positionString) - 1;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panel,
                    "Invalid input.",
                    "Move " + type + " Failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        return -1;
    }
}
