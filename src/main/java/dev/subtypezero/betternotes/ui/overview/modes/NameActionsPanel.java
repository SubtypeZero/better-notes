package dev.subtypezero.betternotes.ui.overview.modes;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.DisplayAttributes;
import dev.subtypezero.betternotes.common.Note;
import dev.subtypezero.betternotes.common.Section;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static dev.subtypezero.betternotes.util.ImageUtils.*;

@Slf4j
public class NameActionsPanel<T extends DisplayAttributes> extends JPanel {
    private final T displayItem;

    private JLabel colorPicker;
    private Color selectedColor;

    private final FlatTextField nameInput;

    private JLabel editButton;
    private JLabel cancelButton;
    private JLabel saveButton;


    public NameActionsPanel(BetterNotesPlugin plugin, T displayItem, boolean allowEdits,
                            JPopupMenu popupMenu, MouseAdapter nameInputFieldListener) {
        this.displayItem = displayItem;

        this.setLayout(new BorderLayout());

        Color panelColor;
        if (displayItem instanceof Note) {
            panelColor = ColorScheme.DARKER_GRAY_COLOR;
        } else {
            panelColor = new Color(20, 20, 20);
        }

        this.setBackground(panelColor);

        if (allowEdits || displayItem instanceof Section) {
            if (displayItem.getColor() == null) {
                this.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
                        BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)));
            } else {
                this.setBorder(new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, displayItem.getColor()),
                        BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR)));
            }
        }

        if (allowEdits) {
            BufferedImage colorPickerIcon = loadImage("/color_picker.png");
            ImageIcon COLOR_PICKER_ICON = new ImageIcon(colorPickerIcon);
            ImageIcon COLOR_PICKER_HOVER_ICON = new ImageIcon(luminanceOffset(colorPickerIcon, -150));

            JPopupMenu removeColorMenu = new JPopupMenu();
            JMenuItem removeColorButton = new JMenuItem("Remove the color");
            removeColorButton.addActionListener(e -> updateColorPicker(null));
            removeColorMenu.add(removeColorButton);

            colorPicker = new JLabel();
            colorPicker.setToolTipText("Edit the color");
            colorPicker.setIcon(COLOR_PICKER_ICON);
            colorPicker.setBackground(panelColor);
            colorPicker.setVisible(false);
            colorPicker.setComponentPopupMenu(removeColorMenu);
            colorPicker.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
                                SwingUtilities.windowForComponent(plugin.getPanel()),
                                selectedColor == null ? ColorScheme.PROGRESS_ERROR_COLOR : selectedColor,
                                "Choose a color",
                                false);
                        colorPicker.setLocation(plugin.getPanel().getLocationOnScreen());
                        colorPicker.setOnColorChange(c -> updateColorPicker(
                                new Color(c.getRed(), c.getGreen(), c.getBlue())));
                        colorPicker.setVisible(true);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    colorPicker.setIcon(COLOR_PICKER_HOVER_ICON);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    colorPicker.setIcon(COLOR_PICKER_ICON);
                }
            });
            selectedColor = displayItem.getColor();
        }

        nameInput = new FlatTextField();
        nameInput.setText(displayItem.getName());
        if (allowEdits || displayItem instanceof Section) {
            nameInput.setBorder(null);
        } else {
            nameInput.setBorder(new MatteBorder(0, 2, 0, 0, displayItem.getColor()));
        }
        nameInput.setEditable(false);
        nameInput.setBackground(panelColor);
        nameInput.setPreferredSize(new Dimension(0, 24));

        nameInput.getTextField().setForeground(Color.WHITE);
        nameInput.getTextField().setBackground(panelColor);
        nameInput.getTextField().setBorder(new EmptyBorder(0, 6, 0, 0));

        if (allowEdits) {
            nameInput.getTextField().setComponentPopupMenu(popupMenu);
        }

        nameInput.getTextField().setCaretPosition(0);
        nameInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSaveButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSaveButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSaveButton();
            }
        });
        // TODO Limit character input

        nameInput.getTextField().addMouseListener(nameInputFieldListener);

        JPanel nameActions = new JPanel(new BorderLayout(3, 0));
        nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
        nameActions.setBackground(panelColor);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(panelColor);

        if (allowEdits) {
            editButton = new JLabel("Edit");
            editButton.setFont(FontManager.getRunescapeSmallFont());
            editButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            editButton.setBackground(panelColor);
            editButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        nameInput.getTextField().removeMouseListener(nameInputFieldListener);
                        nameInput.setEditable(true);
                        updateActions(true);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    editButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    editButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
                }
            });

            cancelButton = new JLabel("Cancel");
            cancelButton.setVisible(false);
            cancelButton.setFont(FontManager.getRunescapeSmallFont());
            cancelButton.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
            cancelButton.setBackground(panelColor);
            cancelButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        nameInput.getTextField().addMouseListener(nameInputFieldListener);
                        nameInput.setEditable(false);
                        nameInput.setText(displayItem.getName());
                        nameInput.getTextField().setCaretPosition(0);
                        updateActions(false);
                        requestFocusInWindow();
                        updateColorPicker(displayItem.getColor());
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    cancelButton.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    cancelButton.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
                }
            });

            saveButton = new JLabel("Save");
            saveButton.setEnabled(false);
            saveButton.setVisible(false);
            saveButton.setFont(FontManager.getRunescapeSmallFont());
            saveButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            saveButton.setBackground(panelColor);
            saveButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && saveButton.isEnabled()) {
                        nameInput.getTextField().addMouseListener(nameInputFieldListener);

                        displayItem.setName(nameInput.getText());
                        displayItem.setColor(selectedColor);

                        // TODO Update the configuration when saving changes

                        nameInput.setEditable(false);
                        updateActions(false);
                        requestFocusInWindow();

                        plugin.getPanel().updateOverview();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    updateSaveButton(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    updateSaveButton();
                }
            });

            nameActions.add(editButton, BorderLayout.CENTER);
            nameActions.add(cancelButton, BorderLayout.WEST);
            nameActions.add(saveButton, BorderLayout.EAST);

            wrapper.add(colorPicker, BorderLayout.WEST);
            wrapper.add(nameActions, BorderLayout.EAST);
        } else if (displayItem instanceof Note) {
            nameInput.getTextField().addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        plugin.showNote((Note) displayItem);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    nameInput.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                }
            });
        }

        wrapper.add(nameInput, BorderLayout.CENTER);

        this.add(wrapper, BorderLayout.CENTER);
    }

    private void updateActions(boolean showSaveAndCancel) {
        editButton.setVisible(!showSaveAndCancel);
        colorPicker.setVisible(showSaveAndCancel);

        if (showSaveAndCancel) {
            nameInput.getTextField().requestFocusInWindow();
            nameInput.getTextField().selectAll();
        }

        cancelButton.setVisible(showSaveAndCancel);
        saveButton.setVisible(showSaveAndCancel);
    }

    private void updateColorPicker(Color color) {
        colorPicker.setBorder(new CompoundBorder(
                new EmptyBorder(0, 4, 0, 0),
                new MatteBorder(0, 0, 3, 0, color)
        ));
        selectedColor = color;
        updateSaveButton();
    }

    private void updateSaveButton() {
        updateSaveButton(false);
    }

    private void updateSaveButton(boolean isHovering) {
        if (validateChanges()) {
            if (isHovering) {
                saveButton.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
            } else {
                saveButton.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
            }
            saveButton.setEnabled(true);
        } else {
            saveButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
            saveButton.setEnabled(false);
        }
    }

    private boolean validateChanges() {
        String textInput = nameInput.getText();

        if (StringUtils.isNotEmpty(textInput) && !textInput.equals(displayItem.getName())) {
            return true;
        }

        Color itemColor = displayItem.getColor();

        if (selectedColor == null) {
            return itemColor != null;
        } else {
            return !selectedColor.equals(itemColor);
        }
    }
}
