package com.securevault.ui.panels;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.Icons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fixed left navigation sidebar for SecureVault.
 * Built with Jet Black (#111111), white text, and Crimson Red (#D93636) active indicator.
 */
public class SidebarPanel extends JPanel {

    public enum NavItem {
        DASHBOARD("Dashboard", "dashboard"),
        VAULT("My Vault", "vault"),
        GENERATOR("Password Generator", "generator"),
        AUDIT("Security Audit", "audit"),
        SETTINGS("Settings", "settings");

        private final String label;
        private final String iconName;

        NavItem(String label, String iconName) {
            this.label = label;
            this.iconName = iconName;
        }

        public String getLabel() {
            return label;
        }

        public String getIconName() {
            return iconName;
        }
    }

    private NavItem activeItem = NavItem.DASHBOARD;
    private final Consumer<NavItem> onNavigation;
    private final Runnable onLogout;
    private final List<NavItemButton> navButtons = new ArrayList<>();

    public SidebarPanel(Consumer<NavItem> onNavigation, Runnable onLogout) {
        this.onNavigation = onNavigation;
        this.onLogout = onLogout;

        setLayout(new BorderLayout());
        setBackground(AppColors.SIDEBAR_BG);
        setPreferredSize(new Dimension(260, 800));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0x22, 0x22, 0x22)));

        initUI();
    }

    private void initUI() {
        // Top section: Brand & Logo
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(32, 24, 28, 24));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setOpaque(false);

        JLabel logoIcon = new JLabel(Icons.getIcon("shield", 26, AppColors.CRIMSON_RED));
        brandPanel.add(logoIcon);

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 2));
        brandText.setOpaque(false);

        JLabel brandName = new JLabel("SECUREVAULT");
        brandName.setFont(Typography.bold(14));
        brandName.setForeground(AppColors.PURE_WHITE);

        JLabel brandSub = new JLabel("ENCRYPTED DESKTOP");
        brandSub.setFont(Typography.TINY);
        brandSub.setForeground(AppColors.SLATE_GREY);

        brandText.add(brandName);
        brandText.add(brandSub);
        brandPanel.add(brandText);

        topContainer.add(brandPanel, BorderLayout.NORTH);
        add(topContainer, BorderLayout.NORTH);

        // Center section: Navigation Menu Items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(8, 12, 12, 12));

        for (NavItem item : NavItem.values()) {
            NavItemButton btn = new NavItemButton(item);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            navButtons.add(btn);
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(4));
        }

        add(menuPanel, BorderLayout.CENTER);

        // Bottom section: User status & Logout button
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setOpaque(false);
        bottomContainer.setBorder(new EmptyBorder(16, 12, 24, 12));

        // Security lock status pill
        JPanel statusPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ThemeManager.applyQualityRendering(g2);
                g2.setColor(new Color(0x18, 0x18, 0x18));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusPill.setOpaque(false);
        statusPill.setMaximumSize(new Dimension(236, 32));

        JLabel dot = new JLabel("●");
        dot.setFont(Typography.regular(10));
        dot.setForeground(AppColors.MUTED_TEAL);
        statusPill.add(dot);

        JLabel statusText = new JLabel("AES-256 GCM SECURED");
        statusText.setFont(Typography.TINY);
        statusText.setForeground(new Color(0xAA, 0xAA, 0xAA));
        statusPill.add(statusText);

        bottomContainer.add(statusPill);
        bottomContainer.add(Box.createVerticalStrut(10));

        // Logout Button
        LogoutButton logoutBtn = new LogoutButton();
        bottomContainer.add(logoutBtn);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    public void setActiveItem(NavItem item) {
        this.activeItem = item;
        for (NavItemButton btn : navButtons) {
            btn.repaint();
        }
    }

    public NavItem getActiveItem() {
        return activeItem;
    }

    /**
     * Individual navigation item button with crimson active indicator bar.
     */
    private class NavItemButton extends JComponent {
        private final NavItem item;
        private boolean isHovered = false;

        public NavItemButton(NavItem item) {
            this.item = item;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    setActiveItem(item);
                    if (onNavigation != null) {
                        onNavigation.accept(item);
                    }
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(236, 42);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, 42);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(220, 42);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            ThemeManager.applyQualityRendering(g2);

            int w = getWidth();
            int h = getHeight();
            boolean isActive = (activeItem == item);

            // Background hover/active
            if (isActive) {
                g2.setColor(AppColors.SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, w, h, 4, 4);

                // Crimson Red active indicator strip
                g2.setColor(AppColors.CRIMSON_RED);
                g2.fillRoundRect(2, 6, 3, h - 12, 2, 2);
            } else if (isHovered) {
                g2.setColor(AppColors.SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, w, h, 4, 4);
            }

            // Icon
            Color iconColor = isActive ? AppColors.PURE_WHITE : (isHovered ? AppColors.PURE_WHITE : AppColors.SIDEBAR_MUTED);
            Icon icon = Icons.getIcon(item.getIconName(), 16, iconColor);
            icon.paintIcon(this, g2, 16, (h - 16) / 2);

            // Label
            g2.setFont(isActive ? Typography.BODY_BOLD : Typography.BODY_MEDIUM);
            g2.setColor(isActive ? AppColors.PURE_WHITE : (isHovered ? AppColors.PURE_WHITE : AppColors.SIDEBAR_TEXT));
            FontMetrics fm = g2.getFontMetrics();
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(item.getLabel(), 44, textY);

            g2.dispose();
        }
    }

    /**
     * Dedicated Logout action button.
     */
    private class LogoutButton extends JComponent {
        private boolean isHovered = false;

        public LogoutButton() {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(236, 38));
            setPreferredSize(new Dimension(236, 38));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (onLogout != null) {
                        onLogout.run();
                    }
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(236, 42);
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, 42);
        }

        @Override
        public Dimension getMinimumSize() {
            return new Dimension(220, 42);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            ThemeManager.applyQualityRendering(g2);

            int w = getWidth();
            int h = getHeight();

            if (isHovered) {
                g2.setColor(new Color(0x24, 0x18, 0x18));
                g2.fillRoundRect(0, 0, w, h, 4, 4);
            }

            Icon icon = Icons.getIcon("logout", 15, isHovered ? AppColors.CRIMSON_RED : AppColors.SIDEBAR_MUTED);
            icon.paintIcon(this, g2, 16, (h - 15) / 2);

            g2.setFont(Typography.BODY_MEDIUM);
            g2.setColor(isHovered ? AppColors.CRIMSON_RED : AppColors.SIDEBAR_TEXT);
            FontMetrics fm = g2.getFontMetrics();
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString("Lock & Logout", 44, textY);

            g2.dispose();
        }
    }
}
