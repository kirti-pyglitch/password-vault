package com.securevault.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Responsive scrollable panel that tracks viewport width for desktop window resizing.
 */
public class ScrollablePanel extends JPanel implements Scrollable {
    public ScrollablePanel(LayoutManager layout) {
        super(layout);
    }

    public ScrollablePanel() {
        super();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 64;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true; // Responsively fill the available width
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}

