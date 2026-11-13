package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Editorial toggle switch for configuration and generator settings.
 */
public class CustomToggle extends JPanel {
    private boolean selected = false;
    private final String label;
    private final List<Runnable> changeListeners = new ArrayList<>();

    public CustomToggle(String label, boolean initial) {
        this.label = label;
        this.selected = initial;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
            }
        });
    }

    public void toggle() {
        setSelected(!selected);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean sel) {
        if (this.selected != sel) {
            this.selected = sel;
            repaint();
            for (Runnable listener : changeListeners) {
                listener.run();
            }
        }
    }

    public void addChangeListener(Runnable r) {
        changeListeners.add(r);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(Typography.BODY_MEDIUM);
        int textW = fm.stringWidth(label);
        return new Dimension(42 + 10 + textW, 26);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ThemeManager.applyQualityRendering(g2);

        int switchW = 38;
        int switchH = 22;
        int y = (getHeight() - switchH) / 2;

        // Track
        Color trackColor = selected ? AppColors.CRIMSON_RED : new Color(0xDD, 0xDA, 0xD2);
        g2.setColor(trackColor);
        g2.fillRoundRect(0, y, switchW, switchH, switchH, switchH);

        // Knob
        int knobSize = 16;
        int knobY = y + 3;
        int knobX = selected ? (switchW - knobSize - 3) : 3;

        g2.setColor(AppColors.PURE_WHITE);
        g2.fillOval(knobX, knobY, knobSize, knobSize);

        // Label
        g2.setColor(AppColors.TEXT_PRIMARY);
        g2.setFont(Typography.BODY_MEDIUM);
        FontMetrics fm = g2.getFontMetrics();
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(label, switchW + 10, textY);

        g2.dispose();
    }
}

