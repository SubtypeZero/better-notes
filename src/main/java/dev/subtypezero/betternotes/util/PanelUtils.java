package dev.subtypezero.betternotes.util;

import net.runelite.client.util.LinkBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelUtils {

    public static MouseAdapter buildMouseAdapter(Runnable r, JLabel button, ImageIcon icon, ImageIcon hoverIcon) {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    r.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(icon);
            }
        };
    }

    public static MouseAdapter buildLinkAdapter(String url, JLabel button, ImageIcon icon, ImageIcon hoverIcon) {
        return buildMouseAdapter(() -> LinkBrowser.browse(url), button, icon, hoverIcon);
    }

    public static MouseAdapter buildMenuAdapter(JPopupMenu menu, JLabel button, ImageIcon icon, ImageIcon hoverIcon) {
        return buildMouseAdapter(() -> {
            Point location = MouseInfo.getPointerInfo().getLocation();
            SwingUtilities.convertPointFromScreen(location, button);
            menu.show(button, location.x, location.y);
        }, button, icon, hoverIcon);
    }

    public static KeyListener buildKeyListener(Runnable r) {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                r.run();
            }
        };
    }
}
