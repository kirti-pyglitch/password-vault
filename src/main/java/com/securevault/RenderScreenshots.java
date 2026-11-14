package com.securevault;

import com.securevault.service.VaultService;
import com.securevault.ui.*;
import com.securevault.ui.panels.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Renders high-resolution screenshot captures of all SecureVault screens for design review.
 */
public class RenderScreenshots {
    public static void main(String[] args) throws Exception {
        System.out.println(">>> RENDERING HIGH-RESOLUTION SECUREVAULT SCREENSHOTS");
        ThemeManager.initializeTheme();

        // 1. Render Login Screen
        renderComponent(new LoginPanel(() -> {}), 1200, 780, "screenshot_login.png");

        // Unlock vault with test master password so panels have rich realistic data
        char[] master = "MasterPassword2026!".toCharArray();
        VaultService.getInstance().getDatabaseManager().initializeVault(master);

        // 2. Render Full Desktop Shell on Dashboard
        renderAppShell(SidebarPanel.NavItem.DASHBOARD, new DashboardPanel(n -> {}), "screenshot_dashboard.png");

        // 3. Render Vault
        renderAppShell(SidebarPanel.NavItem.VAULT, new VaultPanel(), "screenshot_vault.png");

        // 4. Render Generator
        renderAppShell(SidebarPanel.NavItem.GENERATOR, new GeneratorPanel(), "screenshot_generator.png");

        // 5. Render Security Audit
        renderAppShell(SidebarPanel.NavItem.AUDIT, new SecurityAuditPanel(), "screenshot_audit.png");

        // 6. Render Settings
        renderAppShell(SidebarPanel.NavItem.SETTINGS, new SettingsPanel(), "screenshot_settings.png");

        System.out.println(">>> ALL 6 SCREENSHOTS RENDERED SUCCESSFULLY!");
    }

    private static void renderAppShell(SidebarPanel.NavItem navItem, JPanel contentPanel, String filename) throws Exception {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(AppColors.WARM_OFF_WHITE);
        SidebarPanel sidebar = new SidebarPanel(n -> {}, () -> {});
        sidebar.setActiveItem(navItem);
        shell.add(sidebar, BorderLayout.WEST);
        shell.add(contentPanel, BorderLayout.CENTER);

        shell.setSize(1220, 800);
        layoutRecursive(shell);
        System.out.println("DEBUG " + filename + " sidebar w=" + sidebar.getWidth() + " content w=" + contentPanel.getWidth());

        renderComponent(shell, 1220, 800, filename);
    }

    private static void renderComponent(Component comp, int w, int h, String filename) throws Exception {
        comp.setSize(w, h);
        layoutRecursive(comp);

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        ThemeManager.applyQualityRendering(g2);
        comp.paint(g2);
        g2.dispose();

        File out = new File(filename);
        ImageIO.write(img, "png", out);
        System.out.println("  Saved " + out.getAbsolutePath() + " (" + w + "x" + h + ")");
    }

    private static void layoutRecursive(Component c) {
        c.doLayout();
        if (c instanceof JScrollPane sp) {
            sp.getViewport().setSize(sp.getSize());
            sp.getViewport().doLayout();
            if (sp.getViewport().getView() != null) {
                Component view = sp.getViewport().getView();
                int vw = sp.getViewport().getWidth();
                int vh = Math.max(view.getPreferredSize().height, sp.getViewport().getHeight());
                view.setSize(vw, vh);
                view.setBounds(0, 0, vw, vh);
                view.doLayout();
                layoutRecursive(view);
            }
        }
        if (c instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                layoutRecursive(child);
            }
        }
    }
}
