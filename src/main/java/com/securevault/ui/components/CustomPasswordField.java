package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Editorial password field with built-in toggle visibility action and clean styling.
 */
public class CustomPasswordField extends JPasswordField {
    private String placeholder = "";
    private boolean isFocused = false;
    private boolean isPasswordVisible = false;
    private char defaultEchoChar;
    private int cornerRadius = 4;
    private final Rectangle toggleBounds = new Rectangle();

    public CustomPasswordField(String placeholder) {
        this.placeholder = placeholder;
        init();
    }

    public CustomPasswordField() {
        this("");
    }

    private void init() {
        this.defaultEchoChar = getEchoChar();
        setFont(Typography.BODY_MEDIUM);
        setForeground(AppColors.TEXT_PRIMARY);
        setCaretColor(AppColors.CRIMSON_RED);
        setOpaque(false);
        // Leave room on right for the eye toggle icon
        setBorder(new EmptyBorder(10, 14, 10, 38));

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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (toggleBounds.contains(e.getPoint())) {
                    toggleVisibility();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (toggleBounds.contains(e.getPoint())) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                }
            }
        });
    }

    public void toggleVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            setEchoChar((char) 0);
        } else {
            setEchoChar(defaultEchoChar);
        }
        repaint();
    }

    public boolean isPasswordVisible() {
        return isPasswordVisible;
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

        // Placeholder
        if (getPassword().length == 0 && !placeholder.isEmpty() && !isFocused) {
            g2.setColor(AppColors.TEXT_MUTED);
            g2.setFont(Typography.BODY_MEDIUM);
            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
        }

        // Eye toggle icon
        int iconSize = 18;
        int iconX = w - iconSize - 12;
        int iconY = (h - iconSize) / 2;
        toggleBounds.setBounds(iconX - 4, iconY - 4, iconSize + 8, iconSize + 8);

        Icon eyeIcon = Icons.getIcon(isPasswordVisible ? "eye-off" : "eye", iconSize, isPasswordVisible ? AppColors.CRIMSON_RED : AppColors.SLATE_GREY);
        eyeIcon.paintIcon(this, g2, iconX, iconY);

        g2.dispose();
    }
}
