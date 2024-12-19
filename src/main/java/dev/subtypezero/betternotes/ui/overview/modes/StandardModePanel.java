package dev.subtypezero.betternotes.ui.overview.modes;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.Note;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.subtypezero.betternotes.util.ImageUtils.*;
import static dev.subtypezero.betternotes.util.PanelUtils.*;

public class StandardModePanel extends JPanel {
    private final BetterNotesPlugin plugin;
    private final Note note;

    public StandardModePanel(BetterNotesPlugin plugin, Note note) {
        this.plugin = plugin;
        this.note = note;

        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        NameActionsPanel<Note> nameActions = new NameActionsPanel<>(plugin, note, true, null, null);
        JPanel noteActions = buildNoteActions();

        this.add(nameActions, BorderLayout.NORTH);
        this.add(noteActions, BorderLayout.CENTER);
    }

    private JPanel buildNoteActions() {
        JPanel noteActions = new JPanel(new BorderLayout());
        noteActions.setBorder(new EmptyBorder(8, 4, 8, 4));
        noteActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Left actions
        JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        //leftActions.add(favoriteButton); // TODO Add favorite button

        // Right actions
        // TODO Replace "visible" icon with one that makes more sense
        BufferedImage viewIcon = loadImage("/visible.png");
        BufferedImage viewHoverIcon = luminanceOffset(viewIcon, -150);
        ImageIcon VIEW_NOTE_ICON = new ImageIcon(viewIcon);
        ImageIcon VIEW_NOTE_HOVER_ICON = new ImageIcon(viewHoverIcon);

        JLabel viewButton = new JLabel();
        viewButton.setToolTipText("View Note");
        viewButton.setIcon(VIEW_NOTE_ICON);
        viewButton.addMouseListener(buildMouseAdapter(() -> plugin.showNote(note),
                viewButton, VIEW_NOTE_ICON, VIEW_NOTE_HOVER_ICON));

        BufferedImage exportIcon = loadImage("/export.png");
        ImageIcon EXPORT_ICON = new ImageIcon(exportIcon);
        ImageIcon EXPORT_HOVER_ICON = alphaOffset(exportIcon, 0.53f);

        JLabel exportButton = new JLabel(EXPORT_ICON);
        exportButton.setToolTipText("Export Note");
        exportButton.addMouseListener(buildMouseAdapter(() -> plugin.getImportExportUtils().exportNote(note),
                exportButton, EXPORT_ICON, EXPORT_HOVER_ICON));

        BufferedImage deleteIcon = loadImage("/delete.png");
        ImageIcon DELETE_ICON = new ImageIcon(deleteIcon);
        ImageIcon DELETE_HOVER_ICON = alphaOffset(deleteIcon, 0.53f);

        JLabel deleteButton = new JLabel(DELETE_ICON);
        deleteButton.setToolTipText("Delete Note");
        deleteButton.addMouseListener(buildMouseAdapter(() -> plugin.removeNote(note),
                deleteButton, DELETE_ICON, DELETE_HOVER_ICON));

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        rightActions.add(viewButton);
        rightActions.add(exportButton);
        rightActions.add(deleteButton);

        noteActions.add(leftActions, BorderLayout.WEST);
        noteActions.add(rightActions, BorderLayout.EAST);

        return noteActions;
    }
}
