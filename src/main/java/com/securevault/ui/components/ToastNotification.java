package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;

import javax.swing.*;
import java.awt.*;

/**
 * Lightweight microinteraction toast overlay.
 */
public class ToastNotification extends JPanel {
    private final String message;
    private final boolean isSuccess;

    public static void show(JComponent parent, String message, boolean isSuccess) {
        JRootPane rootPane = SwingUtilities.getRootPane(parent);
        if (rootPane == null) return;

        JLayeredPane layeredPane = rootPane.getLayeredPane();
        ToastNotification toast = new ToastNotification(message, isSuccess);
        toast.setSize(toast.getPreferredSize());

        int x = (layeredPane.getWidth() - toast.getWidth()) / 2;
        int y = layeredPane.getHeight() - toast.getHeight() - 40;
        toast.setLocation(x, y);

        layeredPane.add(toast, JLayeredPane.POPUP_LAYER);
        layeredPane.repaint();

        Timer timer = new Timer(2200, e -> {
            layeredPane.remove(toast);
            layeredPane.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private ToastNotification(String message, boolean isSuccess) {
        this.message = message;
        this.isSuccess = isSuccess;
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(Typography.BODY_BOLD);
        int w = fm.stringWidth(message) + 48;
        return new Dimension(Math.max(w, 200), 40);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ThemeManager.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Dark pill background
        g2.setColor(AppColors.JET_BLACK);
        g2.fillRoundRect(0, 0, w, h, 6, 6);

        // Thin indicator line on left
        g2.setColor(isSuccess ? AppColors.MUTED_TEAL : AppColors.CRIMSON_RED);
        g2.fillRoundRect(0, 0, 4, h, 2, 2);

        // Icon dot
        g2.fillOval(14, (h - 8) / 2, 8, 8);

        // Message text
        g2.setColor(AppColors.PURE_WHITE);
        g2.setFont(Typography.BODY_BOLD);
        FontMetrics fm = g2.getFontMetrics();
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(message, 30, textY);

        g2.dispose();
    }
}

