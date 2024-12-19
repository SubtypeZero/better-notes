package dev.subtypezero.betternotes.ui.overview;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import dev.subtypezero.betternotes.ui.ControlPanel;
import dev.subtypezero.betternotes.ui.notes.EmptyNotesPanel;
import dev.subtypezero.betternotes.ui.overview.modes.CompactModePanel;
import dev.subtypezero.betternotes.ui.overview.modes.IconModePanel;
import dev.subtypezero.betternotes.ui.overview.modes.StandardModePanel;
import dev.subtypezero.betternotes.ui.overview.views.SectionViewPanel;
import dev.subtypezero.betternotes.ui.states.ModeState;
import dev.subtypezero.betternotes.ui.states.SortState;
import dev.subtypezero.betternotes.ui.states.ViewState;
import dev.subtypezero.betternotes.util.DataManager;
import net.runelite.client.util.SwingUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OverviewPanel extends JPanel {
    private final BetterNotesPlugin plugin;
    private final DataManager dataManager;
    private final OverviewControls overviewControls;
    private final SearchBar searchBar;

    private final JPanel grid;
    private final GridBagConstraints constraints;

    private ViewState viewState;
    private SortState sortState;
    private ModeState modeState;

    public OverviewPanel(BetterNotesPlugin plugin, ControlPanel controlPanel, SearchBar searchBar) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.overviewControls = controlPanel.getOverviewControls();
        this.searchBar = searchBar;

        this.setLayout(new BorderLayout());

        grid = new JPanel(new GridBagLayout());
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        redraw();

        this.add(grid, BorderLayout.NORTH);
    }

    public void redraw() {
        fastRemoveAll(grid);
        updateStates();

        List<Note> notes = dataManager.getNotes();

        if (notes.isEmpty() && dataManager.getSections().isEmpty()) {
            grid.add(new EmptyNotesPanel(), constraints);
            return;
        }

        boolean forceOpen = false;
        String searchText = searchBar.getText();

        if (StringUtils.isNotEmpty(searchText)) {
            notes = dataManager.filterNotes(searchText);
            forceOpen = true;
        }

        if (viewState == ViewState.LIST) {
            grid.add(buildNoteContainer(notes, null), constraints);
        } else if (viewState == ViewState.SECTION) {
            boolean allowEdits = modeState == ModeState.STANDARD;

            for (Section section : dataManager.getSections()) {
                if (sectionShouldBeHidden(section, notes)) {
                    continue;
                }

                SectionViewPanel sectionPanel = new SectionViewPanel(plugin, section, allowEdits, forceOpen);
                populateSectionView(sectionPanel, section, notes, forceOpen);
            }

            Section unassignedSection = dataManager.getUnassignedSection(notes);

            if (!unassignedSection.getNotes().isEmpty()) {
                if (sectionShouldBeHidden(unassignedSection, notes)) {
                    return;
                }

                SectionViewPanel unassignedSectionPanel = new SectionViewPanel(plugin, unassignedSection, false, forceOpen);
                populateSectionView(unassignedSectionPanel, unassignedSection, notes, forceOpen);
            }
        }
    }

    private boolean sectionShouldBeHidden(Section section, List<Note> filteredNotes) {
        if (StringUtils.isNotEmpty(searchBar.getText())) {
            return dataManager.intersectNotes(section.getNotes(), filteredNotes).isEmpty();
        }

        return false;
    }

    private void populateSectionView(SectionViewPanel sectionPanel, Section section,
                                     List<Note> filteredNotes, boolean forceOpen) {
        if (section.isOpen() || forceOpen) {
            sectionPanel.add(buildNoteContainer(section.getNotes(), filteredNotes), BorderLayout.SOUTH);
        }

        grid.add(sectionPanel, constraints);
        constraints.gridy++;
        grid.add(Box.createRigidArea(new Dimension(0, 5)), constraints);
        constraints.gridy++;
    }

    private JPanel buildNoteContainer(List<Note> notes, List<Note> filter) {
        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        ViewState viewState = overviewControls.getViewState();
        SortState sortState = overviewControls.getSortState();
        ModeState modeState = overviewControls.getModeState();

        boolean sectionMode = viewState == ViewState.SECTION;

        List<Note> sortedNotes;

        // Filter
        if (filter == null) {
            sortedNotes = new ArrayList<>(notes);
        } else {
            sortedNotes = new ArrayList<>(dataManager.intersectNotes(notes, filter));
        }

        // Sort
        if (sortState == SortState.ALPHABETICAL) {
            sortedNotes.sort(Comparator.comparing(Note::getName));
        }

        if (modeState == ModeState.ICON) {
            final int MAX_COL_SIZE = 4;

            JPanel stopExpansionWrapper = new JPanel(new FlowLayout());
            JPanel iconGrid = new JPanel(new GridLayout(0, MAX_COL_SIZE, 5, 5));

            for (Note note : sortedNotes) {
                iconGrid.add(new IconModePanel(plugin, note));
            }

            stopExpansionWrapper.add(iconGrid);
            grid.add(stopExpansionWrapper, constraints);
            constraints.gridy++;
        } else {
            if (sectionMode) {
                grid.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
                constraints.gridy++;
            }

            for (Note note : sortedNotes) {
                JPanel notePanel;

                if (modeState == ModeState.COMPACT) {
                    notePanel = new CompactModePanel(plugin, note);
                } else {
                    notePanel = new StandardModePanel(plugin, note);
                }

                if (sectionMode) {
                    JPanel indentWrapper = new JPanel(new BorderLayout());
                    indentWrapper.add(Box.createRigidArea(new Dimension(12, 0)), BorderLayout.WEST);
                    indentWrapper.add(notePanel, BorderLayout.CENTER);
                    grid.add(indentWrapper, constraints);
                } else {
                    grid.add(notePanel, constraints);
                }

                constraints.gridy++;
                grid.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
                constraints.gridy++;
            }
        }

        return grid;
    }

    private void updateStates() {
        viewState = overviewControls.getViewState();
        sortState = overviewControls.getSortState();
        modeState = overviewControls.getModeState();
    }

    private void fastRemoveAll(Container c) {
        fastRemoveAll(c, true);
        c.revalidate();
        c.repaint();
    }

    private void fastRemoveAll(Container c, boolean isParent) {
        // If we are not on the EDT this will deadlock, in addition to being totally unsafe
        assert SwingUtilities.isEventDispatchThread();

        // Valid components are resized when removed, so we invalidate before removing
        c.invalidate();
        for (int i = 0; i < c.getComponentCount(); i++) {
            Component ic = c.getComponent(i);

            // removeAll and removeNotify are both recursive, so we have to recurse before them
            if (ic instanceof Container) {
                fastRemoveAll((Container) ic, false);
            }

            // each removeNotify needs to remove anything from the event queue that is for that widget
            // this however requires taking a lock and is moderately slow, so we just execute all of
            // those events with a secondary event loop
            SwingUtil.pumpPendingEvents();

            // call removeNotify early; this is most of the work in removeAll and generates events that
            // the next secondary event loop will pick up
            ic.removeNotify();
        }

        if (isParent) {
            c.removeAll();
        }
    }
}
