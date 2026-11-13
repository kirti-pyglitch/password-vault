package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

/**
 * Editorial circular security score gauge.
 * Uses clean geometry, restrained line widths, and explicit color hierarchy.
 */
public class CircularScoreIndicator extends JComponent {
    private int score = 85;
    private final int diameter;

    public CircularScoreIndicator(int diameter) {
        this.diameter = diameter;
        setPreferredSize(new Dimension(diameter, diameter));
        setOpaque(false);
    }

    public CircularScoreIndicator() {
        this(120);
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

        int strokeWidth = 8;
        int pad = strokeWidth + 2;
        int d = Math.min(getWidth(), getHeight()) - 2 * pad;
        int x = (getWidth() - d) / 2;
        int y = (getHeight() - d) / 2;

        // Background track (Light grey)
        g2.setColor(AppColors.LIGHT_GREY);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new Ellipse2D.Float(x, y, d, d));

        // Score arc
        Color scoreColor;
        if (score >= 80) {
            scoreColor = AppColors.MUTED_TEAL;
        } else if (score >= 50) {
            scoreColor = AppColors.SOFT_AMBER;
        } else {
            scoreColor = AppColors.CRIMSON_RED;
        }

        g2.setColor(scoreColor);
        // Start from top (90 degrees) and sweep clockwise (negative degrees in Swing arc)
        double extent = -(score / 100.0) * 360.0;
        g2.draw(new Arc2D.Double(x, y, d, d, 90, extent, Arc2D.OPEN));

        // Center typography: Big score number + "/100" or "%"
        g2.setColor(AppColors.TEXT_PRIMARY);
        g2.setFont(new Font(Typography.getFamily(), Font.BOLD, Math.round(diameter * 0.28f)));
        String scoreStr = String.valueOf(score);
        FontMetrics fmScore = g2.getFontMetrics();
        int scoreW = fmScore.stringWidth(scoreStr);
        int scoreY = (getHeight() / 2) + (fmScore.getAscent() / 3);
        g2.drawString(scoreStr, (getWidth() - scoreW) / 2, scoreY);

        // Small label below
        g2.setColor(AppColors.TEXT_MUTED);
        g2.setFont(Typography.TINY);
        FontMetrics fmLabel = g2.getFontMetrics();
        String label = "SECURITY";
        int labelW = fmLabel.stringWidth(label);
        g2.drawString(label, (getWidth() - labelW) / 2, scoreY + fmLabel.getHeight() + 2);

        g2.dispose();
    }
}

