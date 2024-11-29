package dev.subtypezero.betternotes.ui.notes;

import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.util.ImportExportUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import static dev.subtypezero.betternotes.util.ImageUtils.*;
import static dev.subtypezero.betternotes.util.PanelUtils.*;

public class NoteControls extends JPanel {
    private final ImportExportUtils importExportUtils;
    private final Supplier<Note> noteSupplier;
    private final Runnable backRunnable;

    private JPanel noteTitleAndButtons;
    private JLabel noteTitle;

    public NoteControls(ImportExportUtils importExportUtils, Supplier<Note> noteSupplier, Runnable backRunnable) {
        this.importExportUtils = importExportUtils;
        this.noteSupplier = noteSupplier;
        this.backRunnable = backRunnable;

        this.setLayout(new BorderLayout());

        setupNoteTitleAndButtons();

        this.add(noteTitleAndButtons, BorderLayout.CENTER);
        this.setVisible(false);
    }

    private void setupNoteTitleAndButtons() {
        noteTitleAndButtons = new JPanel(new BorderLayout());

        // Note title
        noteTitle = new JLabel();
        noteTitle.setForeground(Color.WHITE);

        // Note buttons
        JPanel noteButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        BufferedImage exportIcon = loadImage("/export.png");
        ImageIcon EXPORT_ICON = new ImageIcon(exportIcon);
        ImageIcon EXPORT_HOVER_ICON = alphaOffset(exportIcon, 0.53f);

        JLabel exportButton = new JLabel(EXPORT_ICON);
        exportButton.setToolTipText("Export Note");
        exportButton.addMouseListener(buildMouseAdapter(() -> importExportUtils.exportNote(noteSupplier.get()),
                exportButton, EXPORT_ICON, EXPORT_HOVER_ICON));

        BufferedImage backIcon = loadImage("/back_arrow.png");
        ImageIcon BACK_ICON = new ImageIcon(backIcon);
        ImageIcon BACK_HOVER_ICON = alphaOffset(backIcon, 0.53f);

        JLabel backButton = new JLabel(BACK_ICON);
        backButton.setToolTipText("Return to Overview");
        backButton.addMouseListener(buildMouseAdapter(backRunnable, backButton, BACK_ICON, BACK_HOVER_ICON));

        EmptyBorder borderLeft = new EmptyBorder(0, 8, 0, 0);
        backButton.setBorder(borderLeft);

        noteButtons.add(exportButton);
        noteButtons.add(backButton);

        noteTitleAndButtons.add(noteTitle, BorderLayout.WEST);
        noteTitleAndButtons.add(noteButtons, BorderLayout.EAST);
    }

    public void setTitle(String title) {
        noteTitle.setText(title);
    }
}
