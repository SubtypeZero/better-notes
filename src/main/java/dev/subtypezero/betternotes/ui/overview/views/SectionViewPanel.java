package dev.subtypezero.betternotes.ui.overview.views;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import dev.subtypezero.betternotes.ui.SelectionDialog;
import dev.subtypezero.betternotes.ui.overview.MoveMenu;
import dev.subtypezero.betternotes.ui.overview.modes.NameActionsPanel;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dev.subtypezero.betternotes.util.ImageUtils.loadImage;
import static dev.subtypezero.betternotes.util.ImageUtils.luminanceOffset;

public class SectionViewPanel extends JPanel {
    private final BetterNotesPlugin plugin;
    private final Section section;
    private final boolean allowEdits;
    private final boolean forceOpen;

    public SectionViewPanel(BetterNotesPlugin plugin, Section section,
                            boolean allowEdits, boolean forceOpen) {
        this.plugin = plugin;
        this.section = section;
        this.allowEdits = allowEdits;
        this.forceOpen = forceOpen;

        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        BufferedImage openSectionImg = loadImage("/right_arrow.png");
        BufferedImage openSectionHoverImg = luminanceOffset(openSectionImg, -150);
        ImageIcon OPEN_SECTION_ICON = new ImageIcon(openSectionImg);
        ImageIcon OPEN_SECTION_HOVER_ICON = new ImageIcon(openSectionHoverImg);

        BufferedImage closeSectionImg = loadImage("/down_arrow.png");
        BufferedImage closeSectionHoverImg = luminanceOffset(closeSectionImg, -150);
        ImageIcon CLOSE_SECTION_ICON = new ImageIcon(closeSectionImg);
        ImageIcon CLOSE_SECTION_HOVER_ICON = new ImageIcon(closeSectionHoverImg);

        // Open/Close button
        JLabel openCloseButton = new JLabel();

        if (forceOpen) {
            openCloseButton.setToolTipText("");
            openCloseButton.setIcon(CLOSE_SECTION_ICON);
        } else {
            if (section.isOpen()) {
                openCloseButton.setToolTipText("Close Section");
                openCloseButton.setIcon(CLOSE_SECTION_ICON);
            } else {
                openCloseButton.setToolTipText("Open Section");
                openCloseButton.setIcon(OPEN_SECTION_ICON);
            }
        }

        openCloseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleOpenCloseRequest(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (forceOpen) {
                    return;
                }

                openCloseButton.setIcon(section.isOpen() ? CLOSE_SECTION_HOVER_ICON : OPEN_SECTION_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (forceOpen) {
                    return;
                }

                openCloseButton.setIcon(section.isOpen() ? CLOSE_SECTION_ICON : OPEN_SECTION_ICON);
            }
        });

        JPopupMenu popupMenu = buildPopupMenu();

        // Name actions
        JPanel openCloseActions = new JPanel(new BorderLayout());
        openCloseActions.setBackground(new Color(20, 20, 20)); // TODO Make this a common constant
        openCloseActions.add(Box.createRigidArea(new Dimension(6, 0)), BorderLayout.WEST);
        openCloseActions.add(openCloseButton, BorderLayout.CENTER);

        NameActionsPanel<Section> nameActions = new NameActionsPanel<>(plugin, section, allowEdits, popupMenu,
                new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        handleOpenCloseRequest(e);
                    }
                }
        );
        nameActions.add(openCloseActions, BorderLayout.WEST);

        // Name wrapper
        JPanel nameWrapper = new JPanel();
        nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        nameWrapper.setLayout(new BorderLayout());
        nameWrapper.add(nameActions, BorderLayout.CENTER);

        if (allowEdits) {
            nameWrapper.setComponentPopupMenu(popupMenu);
        }

        this.add(nameWrapper, BorderLayout.NORTH);
    }

    private JPopupMenu buildPopupMenu() {
        JPopupMenu popupMenu = new MoveMenu<>(plugin, section);

        JMenuItem addNotesButton = new JMenuItem("Add Notes to Section");
        addNotesButton.addActionListener(e -> {
            Map<String, Note> noteMap = plugin.getDataManager().getNotes().stream()
                    .collect(Collectors.toMap(Note::getName, n -> n));
            String[] noteNames = noteMap.keySet().toArray(String[]::new);
            Arrays.sort(noteNames, String.CASE_INSENSITIVE_ORDER);

            String title = "Select Notes";
            String message = "Select Notes to add to this Section";
            SelectionDialog selectionDialog = new SelectionDialog(plugin.getPanel(), title, message, noteNames);
            selectionDialog.setOkListener(e1 -> {
                List<String> selectedNames = selectionDialog.getSelectedItems();

                if (!selectedNames.isEmpty()) {
                    List<Note> sectionNotes = section.getNotes();
                    sectionNotes.addAll(selectedNames.stream()
                            .map(noteMap::get)
                            .collect(Collectors.toList()));
                    plugin.updateOverview();
                }
            });
            selectionDialog.show();
        });

        JMenuItem exportSectionButton = new JMenuItem("Export Section");
        exportSectionButton.addActionListener(e -> plugin.getImportExportUtils().exportSection(section));

        JMenuItem deleteSectionButton = new JMenuItem("Delete Section");
        deleteSectionButton.addActionListener(e -> plugin.removeSection(section));

        popupMenu.add(addNotesButton);
        popupMenu.add(exportSectionButton);
        popupMenu.add(deleteSectionButton);

        return popupMenu;
    }

    private void handleOpenCloseRequest(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!forceOpen) {
                section.setOpen(!section.isOpen());
                // TODO Update config
                plugin.updateOverview();
            }
        }
    }
}
