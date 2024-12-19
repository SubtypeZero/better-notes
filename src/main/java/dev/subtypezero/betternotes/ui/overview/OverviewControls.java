package dev.subtypezero.betternotes.ui.overview;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.ui.CycleButton;
import dev.subtypezero.betternotes.ui.states.ModeState;
import dev.subtypezero.betternotes.ui.states.SortState;
import dev.subtypezero.betternotes.ui.states.ViewState;
import dev.subtypezero.betternotes.util.ImportExportUtils;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import static dev.subtypezero.betternotes.util.ImageUtils.*;
import static dev.subtypezero.betternotes.util.PanelUtils.*;

public class OverviewControls extends JPanel {
    private static final String TITLE = "Better Notes";
    private static final String HELP_LINK = "https://github.com/SubtypeZero/better-notes";

    private final BetterNotesPlugin plugin;
    private final ImportExportUtils importExportUtils;

    private JPanel overviewTitleAndHelpButton;
    private JLabel helpButton;

    private JPanel overviewButtons;

    private CycleButton<ViewState> viewButton;
    private CycleButton<SortState> sortButton;
    private CycleButton<ModeState> modeButton;

    public OverviewControls(BetterNotesPlugin plugin) {
        this.plugin = plugin;
        this.importExportUtils = plugin.getImportExportUtils();

        this.setLayout(new BorderLayout());

        // TODO Get config from plugin
        // setupOverviewTitleAndHelpButtons(plugin.getConfig().showHelpButton());
        setupOverviewTitleAndHelpButtons(true);
        setupOverviewButtons();

        this.add(overviewTitleAndHelpButton, BorderLayout.NORTH);
        this.add(Box.createRigidArea(new Dimension(0, 3)), BorderLayout.CENTER);
        this.add(overviewButtons, BorderLayout.SOUTH);
    }

    private void setupOverviewTitleAndHelpButtons(boolean showHelpButton) {
        overviewTitleAndHelpButton = new JPanel(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel(TITLE);
        titleLabel.setForeground(Color.WHITE);

        // Help button
        BufferedImage helpIcon = loadImage("/help.png");
        ImageIcon HELP_ICON = new ImageIcon(helpIcon);
        ImageIcon HELP_HOVER_ICON = alphaOffset(helpIcon, 0.53f);

        helpButton = new JLabel(HELP_ICON);
        helpButton.setToolTipText("Click for help");
        helpButton.addMouseListener(buildLinkAdapter(HELP_LINK, helpButton, HELP_ICON, HELP_HOVER_ICON));
        helpButton.setVisible(showHelpButton);

        overviewTitleAndHelpButton.add(titleLabel, BorderLayout.WEST);
        overviewTitleAndHelpButton.add(helpButton, BorderLayout.EAST);
    }

    private void setupOverviewButtons() {
        overviewButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        // View button
        // TODO Replace "standard" view icon with "list" view icon
        BufferedImage sectionViewIcon = loadImage("/view.png");
        ImageIcon SECTION_VIEW_ICON = new ImageIcon(sectionViewIcon);
        ImageIcon SECTION_VIEW_HOVER_ICON = alphaOffset(sectionViewIcon, 0.53f);

        BufferedImage listViewIcon = luminanceOffset(sectionViewIcon, -150);
        ImageIcon LIST_VIEW_ICON = new ImageIcon(listViewIcon);
        ImageIcon LIST_VIEW_HOVER_ICON = alphaOffset(listViewIcon, -100);

        java.util.List<ViewState> viewStates = Arrays.asList(ViewState.values());
        ViewState initViewState = ViewState.LIST; // TODO PULL FROM CONFIG
        java.util.List<ImageIcon> viewIcons = Arrays.asList(LIST_VIEW_ICON, SECTION_VIEW_ICON);
        java.util.List<ImageIcon> viewHoverIcons = Arrays.asList(LIST_VIEW_HOVER_ICON, SECTION_VIEW_HOVER_ICON);
        List<String> viewTooltips = Arrays.asList("Switch to section view", "Switch to list view");
        viewButton = new CycleButton<>(viewStates, initViewState, viewIcons, viewHoverIcons, viewTooltips);
        viewButton.setRunnable(() -> {
            plugin.updateOverview();
            // TODO Save view to config
        });
        //viewButton.setRunnable(() -> plugin.setConfigValue(CONFIG_KEY_PANEL_VIEW, viewButton.getCurrentState().getName()));

        // Sort button
        // TODO Add weighted sort icon
        BufferedImage alphabeticalSortIcon = loadImage("/alphabetical_sort.png");
        ImageIcon ALPHABETICAL_SORT_ICON = new ImageIcon(alphabeticalSortIcon);
        ImageIcon ALPHABETICAL_SORT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(alphabeticalSortIcon, 0.53f));

        BufferedImage weightedSortIcon = ImageUtil.luminanceOffset(alphabeticalSortIcon, -150);
        ImageIcon WEIGHTED_SORT_ICON = new ImageIcon(weightedSortIcon);
        ImageIcon WEIGHTED_SORT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(weightedSortIcon, -100));

        List<SortState> sortStates = Arrays.asList(SortState.values());
        SortState initSortState = SortState.WEIGHTED; // TODO PULL FROM CONFIG
        List<ImageIcon> sortIcons = Arrays.asList(ALPHABETICAL_SORT_ICON, WEIGHTED_SORT_ICON);
        List<ImageIcon> sortHoverIcons = Arrays.asList(ALPHABETICAL_SORT_HOVER_ICON, WEIGHTED_SORT_HOVER_ICON);
        List<String> sortTooltips = Arrays.asList("Switch to weighted sort", "Switch to alphabetical sort");
        sortButton = new CycleButton<>(sortStates, initSortState, sortIcons, sortHoverIcons, sortTooltips);
        sortButton.setRunnable(() -> {
            plugin.updateOverview();
            // TODO Save sort to config
        });
        //sortButton.setRunnable(() -> plugin.setConfigValue(CONFIG_KEY_PANEL_SORT, sortButton.getCurrentState().getName()));

        // Mode button
        BufferedImage standardModeIcon = loadImage("/standard_mode.png");
        ImageIcon STANDARD_MODE_ICON = new ImageIcon(standardModeIcon);
        ImageIcon STANDARD_MODE_HOVER_ICON = alphaOffset(standardModeIcon, -100);

        BufferedImage compactModeIcon = loadImage("/compact_mode.png");
        ImageIcon COMPACT_MODE_ICON = new ImageIcon(compactModeIcon);
        ImageIcon COMPACT_MODE_HOVER_ICON = alphaOffset(compactModeIcon, 0.53f);

        BufferedImage iconModeIcon = loadImage("/icon_mode.png");
        ImageIcon ICON_MODE_ICON = new ImageIcon(iconModeIcon);
        ImageIcon ICON_MODE_HOVER_ICON = alphaOffset(iconModeIcon, 0.53f);

        List<ModeState> modeStates = Arrays.asList(ModeState.values());
        ModeState initModeState = ModeState.STANDARD; // TODO PULL FROM CONFIG
        List<ImageIcon> modeIcons = Arrays.asList(STANDARD_MODE_ICON, COMPACT_MODE_ICON, ICON_MODE_ICON);
        List<ImageIcon> modeHoverIcons = Arrays.asList(STANDARD_MODE_HOVER_ICON, COMPACT_MODE_HOVER_ICON, ICON_MODE_HOVER_ICON);
        List<String> modeTooltips = Arrays.asList("Switch to compact mode", "Switch to icon mode", "Switch to standard mode");
        modeButton = new CycleButton<>(modeStates, initModeState, modeIcons, modeHoverIcons, modeTooltips);
        modeButton.setRunnable(() -> {
            plugin.updateOverview();
            // TODO Save mode to config
        });
        //modeButton.setRunnable(() -> plugin.setConfigValue(CONFIG_KEY_PANEL_MODE, modeButton.getCurrentState().getName()));

        // Import button
        BufferedImage importIcon = loadImage("/import.png");
        ImageIcon IMPORT_ICON = new ImageIcon(importIcon);
        ImageIcon IMPORT_HOVER_ICON = alphaOffset(importIcon, 0.53f);

        // Mass imports and exports
        JPopupMenu massImportExportMenu = new JPopupMenu();

        JMenuItem massImportNotes = new JMenuItem("Mass Import Notes");
        massImportNotes.addActionListener(e -> importExportUtils.massImportNotes());
        massImportExportMenu.add(massImportNotes);

        JMenuItem massExportNotes = new JMenuItem("Mass Export Notes");
        massExportNotes.addActionListener(e -> importExportUtils.massExportNotes());
        massImportExportMenu.add(massExportNotes);

        JMenuItem massImportSections = new JMenuItem("Mass Import Sections");
        massImportSections.addActionListener(e -> importExportUtils.massImportSections());
        massImportExportMenu.add(massImportSections);

        JMenuItem massExportSections = new JMenuItem("Mass Export Sections");
        massExportSections.addActionListener(e -> importExportUtils.massExportSections());
        massImportExportMenu.add(massExportSections);

        // Single imports and exports
        JPopupMenu importExportMenu = new JPopupMenu();

        JMenuItem importNote = new JMenuItem("Import Note");
        importNote.addActionListener(e -> importExportUtils.importNote());
        importExportMenu.add(importNote);

        JMenuItem importSection = new JMenuItem("Import Section");
        importSection.addActionListener(e -> importExportUtils.importSection());
        importExportMenu.add(importSection);

        JLabel importButton = new JLabel(IMPORT_ICON);
        importButton.setToolTipText("Import a note or section");
        importButton.setComponentPopupMenu(massImportExportMenu);
        importButton.addMouseListener(buildMenuAdapter(importExportMenu, importButton, IMPORT_ICON, IMPORT_HOVER_ICON));

        // Add button
        BufferedImage addIcon = loadImage("/add.png");
        ImageIcon ADD_ICON = new ImageIcon(addIcon);
        ImageIcon ADD_HOVER_ICON = alphaOffset(addIcon, 0.53f);

        JPopupMenu addMenu = new JPopupMenu();

        JMenuItem addNote = new JMenuItem("Add Note");
        addNote.addActionListener(e -> plugin.addNote());
        addMenu.add(addNote);

        JMenuItem addSection = new JMenuItem("Add Section");
        addSection.addActionListener(e -> plugin.addSection());
        addMenu.add(addSection);

        JLabel addButton = new JLabel(ADD_ICON);
        addButton.setToolTipText("Add a note or section");
        addButton.addMouseListener(buildMenuAdapter(addMenu, addButton, ADD_ICON, ADD_HOVER_ICON));

        EmptyBorder borderLeft = new EmptyBorder(0, 8, 0, 0);
        sortButton.setBorder(borderLeft);
        modeButton.setBorder(borderLeft);
        importButton.setBorder(borderLeft);
        addButton.setBorder(borderLeft);

        overviewButtons.add(viewButton);
        overviewButtons.add(sortButton);
        overviewButtons.add(modeButton);
        overviewButtons.add(importButton);
        overviewButtons.add(addButton);
    }

    public void showHelpButton(boolean showHelpButton) {
        helpButton.setVisible(showHelpButton);
    }

    public ViewState getViewState() {
        return viewButton.getCurrentState();
    }

    public SortState getSortState() {
        return sortButton.getCurrentState();
    }

    public ModeState getModeState() {
        return modeButton.getCurrentState();
    }
}
