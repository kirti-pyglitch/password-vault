package com.securevault.ui.components;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Premium minimalist button component adhering to SecureVault's editorial design.
 */
public class CustomButton extends JButton {

    public enum Variant {
        PRIMARY,   // Crimson Red background, pure white text
        SECONDARY, // White background, jet black text, crisp 1px border
        GHOST,     // Transparent background, hover tint
        DANGER,    // Burgundy/Red accent for destructive operations
        BLACK      // Jet black background, white text
    }

    private final Variant variant;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 4; // Minimalist, restrained 4px radius

    public CustomButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        init();
    }

    public CustomButton(String text) {
        this(text, Variant.PRIMARY);
    }

    private void init() {
        setFont(Typography.BODY_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 18, 10, 18));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
                    isPressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    isPressed = false;
                    repaint();
                }
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        // Generous vertical touch area for human-centred desktop UX
        int height = Math.max(d.height, 38);
        int width = Math.max(d.width + 12, 80);
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ThemeManager.applyQualityRendering(g2);

        int w = getWidth();
        int h = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        if (!isEnabled()) {
            bg = AppColors.LIGHT_GREY;
            fg = AppColors.SLATE_GREY;
        } else {
            switch (variant) {
                case PRIMARY:
                    if (isPressed) {
                        bg = AppColors.RED_PRESSED;
                    } else if (isHovered) {
                        bg = AppColors.RED_HOVER;
                    } else {
                        bg = AppColors.CRIMSON_RED;
                    }
                    fg = AppColors.PURE_WHITE;
                    break;

                case SECONDARY:
                    if (isPressed) {
                        bg = AppColors.LIGHT_GREY;
                    } else if (isHovered) {
                        bg = AppColors.WARM_OFF_WHITE;
                    } else {
                        bg = AppColors.PURE_WHITE;
                    }
                    fg = AppColors.JET_BLACK;
                    border = AppColors.BORDER_SUBTLE;
                    break;

                case DANGER:
                    if (isPressed) {
                        bg = AppColors.DEEP_BURGUNDY.darker();
                    } else if (isHovered) {
                        bg = AppColors.DEEP_BURGUNDY;
                    } else {
                        bg = new Color(0x8A, 0x2A, 0x36);
                    }
                    fg = AppColors.PURE_WHITE;
                    break;

                case BLACK:
                    if (isPressed) {
                        bg = new Color(0x33, 0x33, 0x33);
                    } else if (isHovered) {
                        bg = new Color(0x22, 0x22, 0x22);
                    } else {
                        bg = AppColors.JET_BLACK;
                    }
                    fg = AppColors.PURE_WHITE;
                    break;

                case GHOST:
                default:
                    if (isPressed) {
                        bg = AppColors.LIGHT_GREY;
                    } else if (isHovered) {
                        bg = new Color(0xEB, 0xE9, 0xE3);
                    } else {
                        bg = new Color(0, 0, 0, 0);
                    }
                    fg = AppColors.TEXT_PRIMARY;
                    break;
            }
        }

        // Fill background
        if (bg.getAlpha() > 0) {
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, cornerRadius, cornerRadius);
        }

        // Draw crisp border
        if (border != null) {
            g2.setColor(border);
            g2.drawRoundRect(0, 0, w - 1, h - 1, cornerRadius, cornerRadius);
        }

        // Draw focus ring if focused
        if (hasFocus() && isEnabled()) {
            g2.setColor(new Color(AppColors.CRIMSON_RED.getRed(), AppColors.CRIMSON_RED.getGreen(), AppColors.CRIMSON_RED.getBlue(), 80));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, cornerRadius, cornerRadius);
        }

        // Text & Icon
        setForeground(fg);
        super.paintComponent(g2);
        g2.dispose();
    }
}

