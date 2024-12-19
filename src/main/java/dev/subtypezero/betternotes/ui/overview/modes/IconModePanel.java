package dev.subtypezero.betternotes.ui.overview.modes;

import dev.subtypezero.betternotes.BetterNotesPlugin;
import dev.subtypezero.betternotes.common.Note;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IconModePanel extends JPanel {

    public IconModePanel(BetterNotesPlugin plugin, Note note) {
        this.setLayout(new BorderLayout());
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setPreferredSize(new Dimension(46, 42));
        this.setToolTipText(note.getName());

        JLabel noteIcon = new JLabel();
        noteIcon.setHorizontalAlignment(JLabel.CENTER);
        noteIcon.setVerticalAlignment(JLabel.CENTER);
        noteIcon.setBorder(new EmptyBorder(2, 2, 2, 2));

        AsyncBufferedImage itemImg = plugin.getItemManager().getImage(note.getIconItemId(), 1, false);
        Runnable r = () -> {
            noteIcon.setIcon(new ImageIcon(itemImg));
            this.repaint();
        };
        itemImg.onLoaded(r);
        r.run();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    plugin.showNote(note);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(ColorScheme.DARKER_GRAY_COLOR);
            }
        });

        // TODO Allow users to update Note icons
        /*
        JMenuItem updateIcon = new JMenuItem("Update Icon..");
		updateIcon.addActionListener(e -> plugin.updateInventorySetupIcon(invSetup));
		popupMenu.add(updateIcon);
         */

        this.add(noteIcon, BorderLayout.CENTER);
    }
}
