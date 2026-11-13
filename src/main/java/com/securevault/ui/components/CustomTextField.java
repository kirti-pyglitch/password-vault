package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Editorial text field with placeholder and minimalist focus ring.
 */
public class CustomTextField extends JTextField {
    private String placeholder = "";
    private boolean isFocused = false;
    private int cornerRadius = 4;

    public CustomTextField(String placeholder) {
        this.placeholder = placeholder;
        init();
    }

    public CustomTextField() {
        this("");
    }

    private void init() {
        setFont(Typography.BODY_MEDIUM);
        setForeground(AppColors.TEXT_PRIMARY);
        setCaretColor(AppColors.CRIMSON_RED);
        setOpaque(false);
        setBorder(new EmptyBorder(10, 14, 10, 14));

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, Math.max(d.height, 42));
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 42);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ThemeManager.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(isEnabled() ? AppColors.PURE_WHITE : AppColors.LIGHT_GREY);
        g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);

        // Border
        if (isFocused) {
            g2.setColor(AppColors.CRIMSON_RED);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        } else {
            g2.setColor(AppColors.BORDER_SUBTLE);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        super.paintComponent(g2);

        // Paint placeholder if text is empty
        if (getText().isEmpty() && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(AppColors.TEXT_MUTED);
            g2.setFont(Typography.BODY_MEDIUM);
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
        }

        g2.dispose();
    }
}
