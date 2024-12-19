package dev.subtypezero.betternotes.ui.overview;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.DisplayAttributes;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import dev.subtypezero.betternotes.ui.states.SortState;
import dev.subtypezero.betternotes.util.DataManager;

import javax.swing.*;
import java.util.List;

public class MoveMenu<T extends DisplayAttributes> extends JPopupMenu {
    private final BetterNotesPlugin plugin;
    private final DataManager dataManager;

    public MoveMenu(BetterNotesPlugin plugin, T displayItem) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();

        JMenuItem moveToTop = new JMenuItem("Move to Top");
        JMenuItem moveUp = new JMenuItem("Move Up");
        JMenuItem moveToPosition = new JMenuItem("Move to Position");
        JMenuItem moveDown = new JMenuItem("Move Down");
        JMenuItem moveToBottom = new JMenuItem("Move to Bottom");

        moveToTop.addActionListener(e -> {
            if (isMovable()) {
                dataManager.moveToTop(displayItem);
            }
        });
        moveUp.addActionListener(e -> {
            if (isMovable()) {
                dataManager.moveUp(displayItem);
            }
        });
        moveToPosition.addActionListener(e -> {
            if (isMovable()) {
                int targetPosition = showPositionDialog(displayItem);

                if (targetPosition >= 0) {
                    dataManager.moveToPosition(displayItem, targetPosition);
                }
            }
        });
        moveDown.addActionListener(e -> {
            if (isMovable()) {
                dataManager.moveDown(displayItem);
            }
        });
        moveToBottom.addActionListener(e -> {
            if (isMovable()) {
                dataManager.moveToBottom(displayItem);
            }
        });

        this.add(moveToTop);
        this.add(moveUp);
        this.add(moveToPosition);
        this.add(moveDown);
        this.add(moveToBottom);
    }

    private boolean isMovable() {
        SortState currentSortState = plugin.getPanel().getControlPanel().getOverviewControls().getSortState();

        if (currentSortState != SortState.WEIGHTED) {
            JOptionPane.showMessageDialog(plugin.getPanel(),
                    "You cannot move this unless weighted sorting is enabled.",
                    "Move Failed",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private int showPositionDialog(T displayItem) {
        String type;
        int currentIndex;
        int currentSize;

        if (displayItem instanceof Note) {
            type = "Note";
            List<Note> notes = dataManager.getNotes();
            currentIndex = notes.indexOf(displayItem);
            currentSize = notes.size();
        } else if (displayItem instanceof Section) {
            type = "Section";
            List<Section> sections = dataManager.getSections();
            currentIndex = sections.indexOf(displayItem);
            currentSize = sections.size();
        } else {
            throw new UnsupportedOperationException("This type is not supported");
        }

        return plugin.showPositionDialog(type, currentIndex, currentSize);
    }
}
