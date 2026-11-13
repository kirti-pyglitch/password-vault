package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;

import javax.swing.*;
import java.awt.*;

/**
 * Editorial horizontal password strength / security metric bar.
 */
public class HorizontalScoreIndicator extends JComponent {
    private int score = 75; // 0 - 100
    private String label = "Strength";
    private boolean showValue = true;

    public HorizontalScoreIndicator() {
        setPreferredSize(new Dimension(200, 26));
        setOpaque(false);
    }

    public void setScore(int score, String label) {
        this.score = Math.max(0, Math.min(100, score));
        this.label = label;
        repaint();
    }

    public void setScore(int score) {
        this.score = Math.max(0, Math.min(100, score));
        repaint();
    }

    public int getScore() {
        return score;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ThemeManager.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        int barH = 6;
        int barY = h - barH - 2;

        // Label row
        g2.setFont(Typography.METADATA);
        g2.setColor(AppColors.TEXT_MUTED);
        g2.drawString(label, 0, barY - 4);

        String statusStr;
        Color scoreColor;
        if (score >= 80) {
            statusStr = "Excellent";
            scoreColor = AppColors.MUTED_TEAL;
        } else if (score >= 60) {
            statusStr = "Strong";
            scoreColor = new Color(0x3B, 0x8C, 0x80);
        } else if (score >= 40) {
            statusStr = "Moderate";
            scoreColor = AppColors.SOFT_AMBER;
        } else {
            statusStr = "Critical";
            scoreColor = AppColors.CRIMSON_RED;
        }

        if (showValue) {
            g2.setFont(Typography.LABEL_BOLD);
            g2.setColor(scoreColor);
            FontMetrics fm = g2.getFontMetrics();
            int sw = fm.stringWidth(statusStr + " (" + score + "%)");
            g2.drawString(statusStr + " (" + score + "%)", w - sw, barY - 4);
        }

        // Track
        g2.setColor(AppColors.LIGHT_GREY);
        g2.fillRoundRect(0, barY, w, barH, 2, 2);

        // Active bar
        int fillW = Math.max(4, Math.round((w * (score / 100.0f))));
        g2.setColor(scoreColor);
        g2.fillRoundRect(0, barY, fillW, barH, 2, 2);

        g2.dispose();
    }
}

