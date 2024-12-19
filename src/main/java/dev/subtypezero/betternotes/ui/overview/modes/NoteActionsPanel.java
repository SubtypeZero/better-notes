package dev.subtypezero.betternotes.ui.overview.modes;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.ui.SelectionDialog;
import dev.subtypezero.betternotes.ui.overview.MoveMenu;
import dev.subtypezero.betternotes.util.DataManager;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NoteActionsPanel extends JPanel {
    private final BetterNotesPlugin plugin;
    private final DataManager dataManager;

    public NoteActionsPanel(BetterNotesPlugin plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    /*
    public NoteActionsPanel(BetterNotesPlugin plugin, boolean allowEdits) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();

        // TODO Create the move/action menu for Note panels
        // TODO Have all mode panels extend this class

        // TODO Add Note to Sections


        JPopupMenu popupMenu;

        if (allowEdits) {
            popupMenu = new MoveMenu<>(plugin, note);
        } else {
            popupMenu = new JPopupMenu();
        }

        JMenuItem addToSection = new JMenuItem("Add Note to Section");

        addToSection.addActionListener(e -> {
            if (dataManager.getSections().isEmpty()) {
                JOptionPane.showMessageDialog(plugin.getPanel(),
                        "You must create a Section first",
                        "No Sections Exist",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Section names
            // Sorted

            SelectionDialog selectionDialog = new SelectionDialog(plugin.getPanel(), title, message, sectionNames);
            selectionDialog.setOkListener(e1 -> {
                List<String> selectedSections = selectionDialog.getSelectedItems();

            });
        });

        popupMenu.add(addToSection);

        // TODO Allow notes to be removed from sections


    }

    private JPopupMenu buildPopupMenu() {
        JPopupMenu popupMenu = new MoveMenu<>(plugin, section);



        popupMenu.add(addNotesButton);
        popupMenu.add(exportSectionButton);
        popupMenu.add(deleteSectionButton);

        return popupMenu;
    }*/
}
