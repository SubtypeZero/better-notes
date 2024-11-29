package dev.subtypezero.betternotes.ui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CycleButton<T> extends JLabel {
    private final List<T> states;
    private final List<ImageIcon> icons;
    private final List<ImageIcon> hoverIcons;
    private final List<String> tooltips;

    private int currentIndex;

    private MouseAdapter mouseAdapter;

    public CycleButton(List<T> states, T initialState, List<ImageIcon> icons, List<ImageIcon> hoverIcons,
                       List<String> tooltips) {
        super();

        this.states = states;
        this.icons = icons;
        this.hoverIcons = hoverIcons;
        this.tooltips = tooltips;
        this.currentIndex = 0;

        assertSizes();
        setCurrentState(initialState);
    }

    private void assertSizes() {
        assert states.size() == icons.size();
        assert icons.size() == hoverIcons.size();
        assert hoverIcons.size() == tooltips.size();
    }

    public void setRunnable(Runnable runnable) {
        removeMouseListener(mouseAdapter);

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    currentIndex = (currentIndex + 1) % states.size();
                    setToolTipText(tooltips.get(currentIndex));
                    setIcon(icons.get(currentIndex));
                    runnable.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(hoverIcons.get(currentIndex));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(icons.get(currentIndex));
            }
        };

        addMouseListener(mouseAdapter);
    }

    public T getCurrentState() {
        return states.get(currentIndex);
    }

    public void setCurrentState(final T state) {
        for (int i = 0; i < this.states.size(); i++) {
            if (this.states.get(i) == state) {
                this.currentIndex = i;
                break;
            }
        }
        setIcon(icons.get(currentIndex));
        setToolTipText(tooltips.get(currentIndex));
    }
}
