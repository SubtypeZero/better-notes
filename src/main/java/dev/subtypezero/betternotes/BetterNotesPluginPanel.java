package dev.subtypezero.betternotes;

import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import dev.subtypezero.betternotes.ui.ControlPanel;
import dev.subtypezero.betternotes.ui.MainPanelNorth;
import dev.subtypezero.betternotes.ui.MainPanelSouth;
import dev.subtypezero.betternotes.ui.notes.NotePanel;
import dev.subtypezero.betternotes.ui.overview.OverviewPanel;
import dev.subtypezero.betternotes.ui.overview.SearchBar;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

@Slf4j
public class BetterNotesPluginPanel extends PluginPanel {
    private final BetterNotesPlugin plugin;

    @Getter
    private final ControlPanel controlPanel;
    @Getter
    private final SearchBar searchBar;
    private final OverviewPanel overviewPanel;
    private final NotePanel notePanel;

    private Note currentNote;

    public BetterNotesPluginPanel(BetterNotesPlugin plugin) {
        super(false);

        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        this.plugin = plugin;

        controlPanel = new ControlPanel(plugin, this::getCurrentNote, this::showOverview);
        searchBar = new SearchBar();
        overviewPanel = new OverviewPanel(plugin, controlPanel, searchBar);
        searchBar.setListener(overviewPanel::redraw);

        notePanel = new NotePanel(plugin.getDataManager());

        MainPanelNorth northPanel = new MainPanelNorth(controlPanel, searchBar);
        MainPanelSouth southPanel = new MainPanelSouth(overviewPanel, notePanel);

        this.add(northPanel, BorderLayout.NORTH);
        this.add(southPanel, BorderLayout.CENTER);

        initNoteExample();
    }

    private Note getCurrentNote() {
        return currentNote;
    }

    private void showOverview() {
        notePanel.hideNote();
        controlPanel.showOverview();
        searchBar.setVisible(true);
        overviewPanel.redraw();
        overviewPanel.setVisible(true);
        currentNote = null;
    }

    public void showNote(Note note) {
        currentNote = note;
        overviewPanel.setVisible(false);
        searchBar.setVisible(false);
        controlPanel.showNote(note);
        notePanel.showNote(note);
    }

    public void updateOverview() {
        overviewPanel.redraw();
    }

    private void initNoteExample() {
        // TODO Remove temporary code
        String markdownText = "# Better Notes\n" +
                "A RuneLite plugin designed to simplify the process of creating and organizing notes.\n" +
                "\n" +
                "## Heading 2\n" +
                "Here we have some text and some list items:\n" +
                "1. Item 1\n" +
                "2. Item 2\n" +
                "3. Item 3\n" +
                "\n" +
                "Another example but with bullet points:\n" +
                "- Point 1\n" +
                "- Point 2\n" +
                "- Point 3\n" +
                "\n" +
                "## Rendering code/commands\n" +
                "An example command: `/test`\n";

        Note firstNote = new Note("Test");
        firstNote.setNotes(markdownText);
        plugin.getDataManager().addNote(firstNote);

        Note secondNote = new Note("Example");
        secondNote.setNotes(markdownText);
        plugin.getDataManager().addNote(secondNote);

        Note thirdNote = new Note("Third");
        thirdNote.setNotes(markdownText);
        plugin.getDataManager().addNote(thirdNote);

        Note fourthNote = new Note("Fourth");
        fourthNote.setNotes(markdownText);
        plugin.getDataManager().addNote(fourthNote);

        Note fifthNote = new Note("Fifth");
        fifthNote.setNotes(markdownText);
        plugin.getDataManager().addNote(fifthNote);

        Note sixthNote = new Note("Sixth");
        sixthNote.setNotes(markdownText);
        plugin.getDataManager().addNote(sixthNote);

        Section firstSection = new Section("Section 1");
        firstSection.setNotes(List.of(firstNote, secondNote, thirdNote, fourthNote, fifthNote));
        plugin.getDataManager().addSection(firstSection);

        Section secondSection = new Section("Section 2");
        secondSection.setNotes(List.of(firstNote, secondNote, thirdNote, fourthNote));
        plugin.getDataManager().addSection(secondSection);

        Section thirdSection = new Section("Section 3");
        thirdSection.setNotes(List.of(firstNote, secondNote, thirdNote));
        plugin.getDataManager().addSection(thirdSection);

        Section fourthSection = new Section("Section 4");
        plugin.getDataManager().addSection(fourthSection);

        //showNote(firstNote);
        showOverview();
    }
}
