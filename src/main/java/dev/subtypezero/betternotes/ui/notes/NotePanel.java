package dev.subtypezero.betternotes.ui.notes;

import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.util.DataManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@Slf4j
public class NotePanel extends JPanel {
    private static final String UNDO_ACTION = "Undo";
    private static final String REDO_ACTION = "Redo";

    private final JTextArea notesEditor = new JTextArea();
    private final UndoManager undoManager = new UndoManager();

    private final DataManager dataManager;

    private Note note;

    public NotePanel(DataManager dataManager) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(ColorScheme.DARK_GRAY_COLOR);

        this.dataManager = dataManager;

        setupNotesEditor();

        JPanel notesContainer = new JPanel(new BorderLayout());
        notesContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        notesContainer.add(notesEditor);
        notesContainer.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(notesContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scrollPane);
    }

    private void setupNotesEditor() {
        notesEditor.setTabSize(2);
        notesEditor.setLineWrap(true);
        notesEditor.setWrapStyleWord(true);
        notesEditor.setOpaque(false);

        notesEditor.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
        notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), UNDO_ACTION);
        notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control shift Z"), REDO_ACTION);

        notesEditor.getActionMap().put(UNDO_ACTION, new AbstractAction(UNDO_ACTION) {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) {
                        undoManager.undo();
                    }
                } catch (CannotUndoException ex) {
                    log.warn("Unable to undo", ex);
                }
            }
        });

        notesEditor.getActionMap().put(REDO_ACTION, new AbstractAction(REDO_ACTION) {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) {
                        undoManager.redo();
                    }
                } catch (CannotRedoException ex) {
                    log.warn("Unable to redo", ex);
                }
            }
        });

        notesEditor.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                updateSavedNotes();
            }
        });

        // Increase undo limit (default is 100)
        undoManager.setLimit(500);
    }

    public void showNote(Note note) {
        undoManager.discardAllEdits(); // TODO Look into having undo/redo per note
        notesEditor.setText(note.getNotes());
        this.note = note;
        this.setVisible(true);
    }

    public void hideNote() {
        this.setVisible(false);
        updateSavedNotes();
        undoManager.discardAllEdits();
        notesEditor.setText(null);
        this.note = null;
    }

    private void updateSavedNotes() {
        if (note != null) {
            try {
                Document document = notesEditor.getDocument();
                note.setNotes(document.getText(0, document.getLength()));
                dataManager.saveNoteToConfig(note);
            } catch (BadLocationException ex) {
                log.warn("Unable to save notes", ex);
            }
        }
    }
}
