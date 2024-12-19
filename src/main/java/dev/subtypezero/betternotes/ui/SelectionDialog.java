package dev.subtypezero.betternotes.ui;

import lombok.Setter;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SelectionDialog {
    private final JList<String> list;
    private final JDialog dialog;

    @Setter
    private ActionListener okListener, closeListener;

    public SelectionDialog(JPanel parent, String title, String message, String[] options) {
        this.list = new JList<>(options);
        this.list.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);

        // Selection panel
        JPanel selectionPanel = new JPanel(new BorderLayout(5, 5));

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel tipLabel = new JLabel("Ctrl + Click to select multiple");
        tipLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(messageLabel, BorderLayout.NORTH);
        topPanel.add(tipLabel, BorderLayout.CENTER);

        // Center the list elements
        ((DefaultListCellRenderer) list.getCellRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);

        selectionPanel.add(topPanel, BorderLayout.NORTH);
        selectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Calculate panel width and height
        FontMetrics fontMetrics = new FontMetrics(list.getFont()) {};
        Optional<String> widthOptional = Arrays.stream(options).max(Comparator.comparingInt(
                s -> (int) Math.ceil(fontMetrics.getStringBounds(s, null).getWidth()))
        );

        if (widthOptional.isEmpty()) {
            throw new RuntimeException("Unable to calculate selection panel width");
        }

        Rectangle2D bounds = fontMetrics.getStringBounds(widthOptional.get(), null);
        list.setFixedCellHeight((int) Math.ceil((bounds.getHeight() + 1)));

        int widthPixels = (int) Math.ceil(bounds.getWidth()) + 25;
        int heightPixels = (fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent()) * options.length + 50;

        selectionPanel.setPreferredSize(new Dimension(Math.min(widthPixels, 500), Math.min(heightPixels, 400)));

        // Option pane
        JOptionPane optionPane = new JOptionPane(selectionPanel);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(this::onOk);

        JButton closeButton = new JButton("Cancel");
        closeButton.addActionListener(this::onClose);

        optionPane.setOptions(new Object[] { okButton, closeButton });

        // Dialog
        dialog = optionPane.createDialog(parent, "Select Options");
        //dialog.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
        dialog.setTitle(title);
    }

    private void onOk(ActionEvent e) {
        if (okListener != null) {
            okListener.actionPerformed(e);
        }

        hide();
    }

    private void onClose(ActionEvent e) {
        if (closeListener != null) {
            closeListener.actionPerformed(e);
        }

        hide();
    }

    private void hide() {
        dialog.setVisible(false);
    }

    public void show() {
        dialog.setVisible(true);
    }

    public List<String> getSelectedItems() {
        return list.getSelectedValuesList();
    }
}
