package com.securevault.ui;

import com.securevault.service.VaultService;
import com.securevault.ui.components.Icons;
import com.securevault.ui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Main application window for SecureVault.
 * Desktop-first shell coordinating sidebar navigation, views, and session security.
 */
public class MainFrame extends JFrame {
    private final CardLayout rootCardLayout = new CardLayout();
    private final JPanel rootContainer = new JPanel(rootCardLayout);

    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentArea = new JPanel(contentCardLayout);

    private SidebarPanel sidebarPanel;
    private DashboardPanel dashboardPanel;
    private VaultPanel vaultPanel;
    private GeneratorPanel generatorPanel;
    private SecurityAuditPanel securityAuditPanel;
    private SettingsPanel settingsPanel;
    private LoginPanel loginPanel;

    public MainFrame() {
        super("SecureVault – Encrypted Desktop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 640));
        setPreferredSize(new Dimension(1220, 800));

        // Create window icon
        setIconImage(createAppIcon());

        initViews();
        pack();
        setLocationRelativeTo(null);
    }

    private void initViews() {
        // Authenticated main interface
        JPanel appShell = new JPanel(new BorderLayout());
        appShell.setBackground(AppColors.WARM_OFF_WHITE);

        sidebarPanel = new SidebarPanel(this::navigateTo, this::handleLogout);
        appShell.add(sidebarPanel, BorderLayout.WEST);

        dashboardPanel = new DashboardPanel(this::navigateTo);
        vaultPanel = new VaultPanel();
        generatorPanel = new GeneratorPanel();
        securityAuditPanel = new SecurityAuditPanel();
        settingsPanel = new SettingsPanel();

        contentArea.add(dashboardPanel, SidebarPanel.NavItem.DASHBOARD.name());
        contentArea.add(vaultPanel, SidebarPanel.NavItem.VAULT.name());
        contentArea.add(generatorPanel, SidebarPanel.NavItem.GENERATOR.name());
        contentArea.add(securityAuditPanel, SidebarPanel.NavItem.AUDIT.name());
        contentArea.add(settingsPanel, SidebarPanel.NavItem.SETTINGS.name());

        appShell.add(contentArea, BorderLayout.CENTER);

        // Login Panel
        loginPanel = new LoginPanel(this::handleLoginSuccess);

        rootContainer.add(loginPanel, "LOGIN");
        rootContainer.add(appShell, "APP");

        setContentPane(rootContainer);

        // Start on login view
        rootCardLayout.show(rootContainer, "LOGIN");
    }

    public void navigateTo(SidebarPanel.NavItem item) {
        sidebarPanel.setActiveItem(item);
        contentCardLayout.show(contentArea, item.name());

        // Refresh views if necessary
        switch (item) {
            case DASHBOARD:
                dashboardPanel.refreshData();
                break;
            case VAULT:
                vaultPanel.refreshList();
                break;
            case AUDIT:
                securityAuditPanel.refreshAudit();
                break;
            default:
                break;
        }
    }

    private void handleLoginSuccess() {
        dashboardPanel.refreshData();
        vaultPanel.refreshList();
        securityAuditPanel.refreshAudit();
        sidebarPanel.setActiveItem(SidebarPanel.NavItem.DASHBOARD);
        contentCardLayout.show(contentArea, SidebarPanel.NavItem.DASHBOARD.name());
        rootCardLayout.show(rootContainer, "APP");
    }

    private void handleLogout() {
        VaultService.getInstance().getDatabaseManager().lock();
        // Recreate login panel to clear any sensitive state
        rootContainer.remove(loginPanel);
        loginPanel = new LoginPanel(this::handleLoginSuccess);
        rootContainer.add(loginPanel, "LOGIN");
        rootCardLayout.show(rootContainer, "LOGIN");
    }

    private Image createAppIcon() {
        int size = 64;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        ThemeManager.applyQualityRendering(g2);
        g2.setColor(AppColors.JET_BLACK);
        g2.fillRoundRect(0, 0, size, size, 14, 14);

        Icon shield = Icons.getIcon("shield", 36, AppColors.CRIMSON_RED);
        shield.paintIcon(null, g2, (size - 36) / 2, (size - 36) / 2);

        g2.dispose();
        return img;
    }
}

