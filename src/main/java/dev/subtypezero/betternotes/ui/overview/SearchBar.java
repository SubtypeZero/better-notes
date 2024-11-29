package dev.subtypezero.betternotes.ui.overview;

import dev.subtypezero.betternotes.util.ImageUtils;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

import javax.swing.*;
import java.awt.*;

import static dev.subtypezero.betternotes.util.PanelUtils.*;

public class SearchBar extends IconTextField {

    public SearchBar() {
        this.setIcon(new ImageIcon(ImageUtils.loadImage("/search.png")));
        this.setMinimumSize(new Dimension(0, 30));
        this.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        this.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        this.setHoverBackgroundColor(ColorScheme.DARKER_GRAY_HOVER_COLOR);
    }

    public void setListener(Runnable r) {
        this.addKeyListener(buildKeyListener(r));
        this.addClearListener(r);
    }
}
