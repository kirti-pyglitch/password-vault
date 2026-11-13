package com.securevault.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Central ThemeManager for SecureVault.
 * Configures system-wide Swing UI defaults and provides standard styling utilities.
 */
public final class ThemeManager {
    private ThemeManager() {}

    public static void initializeTheme() {
        try {
            // Use system look and feel for core window chrome where appropriate
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Global font and anti-aliasing hints
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Tooltip styling
        UIManager.put("ToolTip.background", AppColors.JET_BLACK);
        UIManager.put("ToolTip.foreground", AppColors.PURE_WHITE);
        UIManager.put("ToolTip.font", Typography.METADATA);
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Text selection
        UIManager.put("TextField.selectionBackground", AppColors.RED_TINT);
        UIManager.put("TextField.selectionForeground", AppColors.JET_BLACK);
        UIManager.put("PasswordField.selectionBackground", AppColors.RED_TINT);
        UIManager.put("PasswordField.selectionForeground", AppColors.JET_BLACK);

        // Panel defaults
        UIManager.put("Panel.background", AppColors.WARM_OFF_WHITE);
    }

    public static void applyQualityRendering(Graphics g) {
        if (g instanceof Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        }
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
            new LineBorder(AppColors.BORDER_SUBTLE, 1, false),
            new EmptyBorder(20, 24, 20, 24)
        );
    }

    public static Border createPanelBorder() {
        return new LineBorder(AppColors.BORDER_SUBTLE, 1, false);
    }

    public static Border createEmptyPadding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                applyQualityRendering(g);
                g.setColor(AppColors.SURFACE_CARD);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(createCardBorder());
        panel.setBackground(AppColors.SURFACE_CARD);
        return panel;
    }

    public static void styleScrollBar(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(AppColors.WARM_OFF_WHITE);

        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        vBar.setUI(new MinimalScrollBarUI());
        vBar.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        vBar.setUnitIncrement(16);

        JScrollBar hBar = scrollPane.getHorizontalScrollBar();
        hBar.setUI(new MinimalScrollBarUI());
        hBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 8));
        hBar.setUnitIncrement(16);
    }

    private static class MinimalScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(0xBB, 0xB8, 0xB0);
            this.trackColor = AppColors.WARM_OFF_WHITE;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            applyQualityRendering(g2);
            g2.setColor(isThumbRollover() ? AppColors.SLATE_GREY : thumbColor);
            int arc = Math.min(thumbBounds.width, 6);
            g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1, thumbBounds.width - 2, thumbBounds.height - 2, arc, arc);
            g2.dispose();
        }
    }
}

