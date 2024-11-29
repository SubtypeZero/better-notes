package dev.subtypezero.betternotes.ui;

import dev.subtypezero.betternotes.ui.overview.SearchBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainPanelNorth extends JPanel {

    public MainPanelNorth(ControlPanel controlPanel, SearchBar searchBar) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(0, 0, 10, 0));
        this.add(controlPanel);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(searchBar);
    }
}
