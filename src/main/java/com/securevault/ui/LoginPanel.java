package com.securevault.ui;

import com.securevault.crypto.VaultEncryption;
import com.securevault.service.VaultService;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.CustomPasswordField;
import com.securevault.ui.components.HorizontalScoreIndicator;
import com.securevault.ui.components.Icons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

/**
 * Editorial split-screen login and vault initialization experience.
 * Left: Jet Black with geometric security artwork and strong editorial typography.
 * Right: Pure White minimalist authentication form with Crimson Red action button.
 */
public class LoginPanel extends JPanel {
    private final VaultService vaultService = VaultService.getInstance();
    private final Runnable onLoginSuccess;

    private boolean isRegistrationMode = false;

    private final JLabel formTitle = new JLabel("Unlock Your Vault");
    private final JLabel formSubtitle = new JLabel("Enter your master passphrase to decrypt your repository.");
    private final CustomPasswordField passwordField = new CustomPasswordField("Master Password");
    private final CustomPasswordField confirmField = new CustomPasswordField("Confirm Master Password");
    private final JLabel confirmLabel = new JLabel("CONFIRM MASTER PASSWORD");
    private final HorizontalScoreIndicator strengthIndicator = new HorizontalScoreIndicator();
    private final CustomButton submitBtn = new CustomButton("Unlock Vault", CustomButton.Variant.PRIMARY);
    private final JLabel toggleModeLink = new JLabel("Create a new master vault →");
    private final JLabel errorLabel = new JLabel();

    public LoginPanel(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
        setLayout(new GridLayout(1, 2));

        // Automatically toggle to setup mode if vault not yet initialized
        if (!vaultService.getDatabaseManager().isInitialized()) {
            isRegistrationMode = true;
        }

        initUI();
    }

    private void initUI() {
        removeAll();

        // 1. Left Side: Dark Editorial Branding & Geometric Artwork
        JPanel leftPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ThemeManager.applyQualityRendering(g2);
                g2.setColor(AppColors.JET_BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw geometric abstract cybersecurity artwork
                drawGeometricArtwork(g2, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(54, 54, 54, 54));

        // Top Brand mark
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandRow.setOpaque(false);
        JLabel logo = new JLabel(Icons.getIcon("shield", 28, AppColors.CRIMSON_RED));
        JLabel brand = new JLabel("SECUREVAULT");
        brand.setFont(Typography.bold(16));
        brand.setForeground(AppColors.PURE_WHITE);
        brandRow.add(logo);
        brandRow.add(brand);
        leftPanel.add(brandRow, BorderLayout.NORTH);

        // Center Editorial Headline
        JPanel centerText = new JPanel();
        centerText.setLayout(new BoxLayout(centerText, BoxLayout.Y_AXIS));
        centerText.setOpaque(false);

        JLabel h1 = new JLabel("Your passwords.");
        h1.setFont(Typography.DISPLAY_LARGE);
        h1.setForeground(AppColors.PURE_WHITE);

        JLabel h2 = new JLabel("Your control.");
        h2.setFont(Typography.DISPLAY_LARGE);
        h2.setForeground(AppColors.CRIMSON_RED);

        JLabel desc = new JLabel("<html><body style='width: 320px; line-height: 1.5; color: #888888; font-size: 13px;'>" +
            "One secure place for everything you sign in to. Built with client-side zero-knowledge AES-256-GCM encryption." +
            "</body></html>");

        centerText.add(Box.createVerticalGlue());
        centerText.add(h1);
        centerText.add(Box.createVerticalStrut(4));
        centerText.add(h2);
        centerText.add(Box.createVerticalStrut(18));
        centerText.add(desc);
        centerText.add(Box.createVerticalGlue());

        leftPanel.add(centerText, BorderLayout.CENTER);

        // Bottom Security Pill
        JLabel bottomBadge = new JLabel("● HARDENED AES-256 ENCRYPTION • PBKDF2 DERIVATION");
        bottomBadge.setFont(Typography.TINY);
        bottomBadge.setForeground(AppColors.SLATE_GREY);
        leftPanel.add(bottomBadge, BorderLayout.SOUTH);

        add(leftPanel);

        // 2. Right Side: Pure White Minimalist Authentication
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(AppColors.PURE_WHITE);
        rightPanel.setBorder(new EmptyBorder(40, 54, 40, 54));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setOpaque(false);
        formCard.setPreferredSize(new Dimension(360, 480));

        // Form Titles
        formTitle.setFont(Typography.DISPLAY_TITLE);
        formTitle.setForeground(AppColors.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formSubtitle.setFont(Typography.BODY_SMALL);
        formSubtitle.setForeground(AppColors.TEXT_MUTED);
        formSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(formSubtitle);
        formCard.add(Box.createVerticalStrut(28));

        // Error message banner
        errorLabel.setFont(Typography.METADATA);
        errorLabel.setForeground(AppColors.CRIMSON_RED);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        errorLabel.setVisible(false);
        formCard.add(errorLabel);
        formCard.add(Box.createVerticalStrut(10));

        // Master Password Label + Input
        JLabel passLabel = new JLabel("MASTER PASSWORD");
        passLabel.setFont(Typography.LABEL_BOLD);
        passLabel.setForeground(AppColors.TEXT_MUTED);
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(passLabel);
        formCard.add(Box.createVerticalStrut(6));

        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        formCard.add(passwordField);
        formCard.add(Box.createVerticalStrut(16));

        // Confirm Password (Only shown in registration mode)
        confirmLabel.setFont(Typography.LABEL_BOLD);
        confirmLabel.setForeground(AppColors.TEXT_MUTED);
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmLabel.setVisible(isRegistrationMode);

        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        confirmField.setVisible(isRegistrationMode);

        strengthIndicator.setAlignmentX(Component.LEFT_ALIGNMENT);
        strengthIndicator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        strengthIndicator.setVisible(isRegistrationMode);

        formCard.add(confirmLabel);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(confirmField);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(strengthIndicator);
        formCard.add(Box.createVerticalStrut(20));

        // Submit Button
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        submitBtn.addActionListener(e -> handleSubmit());
        formCard.add(submitBtn);
        formCard.add(Box.createVerticalStrut(20));

        // Toggle Registration / Login Mode
        toggleModeLink.setFont(Typography.BODY_SMALL);
        toggleModeLink.setForeground(AppColors.CRIMSON_RED);
        toggleModeLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleModeLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleModeLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isRegistrationMode = !isRegistrationMode;
                updateMode();
            }
        });
        formCard.add(toggleModeLink);

        rightPanel.add(formCard);
        add(rightPanel);

        // Key listeners for Enter key submission
        KeyAdapter enterSubmit = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSubmit();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (isRegistrationMode) {
                    String pass = new String(passwordField.getPassword());
                    int s = VaultEncryption.calculatePasswordStrength(pass);
                    strengthIndicator.setScore(s, "Password Strength");
                }
            }
        };

        passwordField.addKeyListener(enterSubmit);
        confirmField.addKeyListener(enterSubmit);

        updateMode();
    }

    private void updateMode() {
        errorLabel.setVisible(false);
        if (isRegistrationMode) {
            formTitle.setText("Initialize Master Vault");
            formSubtitle.setText("Create your master encryption key. Cannot be recovered if lost.");
            submitBtn.setText("Create Master Vault");
            toggleModeLink.setText("Already initialized? Unlock existing vault →");
            confirmLabel.setVisible(true);
            confirmField.setVisible(true);
            strengthIndicator.setVisible(true);
        } else {
            formTitle.setText("Unlock Your Vault");
            formSubtitle.setText("Enter your master passphrase to decrypt your repository.");
            submitBtn.setText("Unlock Vault");
            toggleModeLink.setText("Create a new master vault →");
            confirmLabel.setVisible(false);
            confirmField.setVisible(false);
            strengthIndicator.setVisible(false);
        }
        revalidate();
        repaint();
    }

    private void handleSubmit() {
        char[] pass = passwordField.getPassword();
        if (pass.length == 0) {
            showError("Please enter your master password.");
            return;
        }

        if (isRegistrationMode) {
            char[] conf = confirmField.getPassword();
            if (pass.length < 8) {
                showError("Master password must be at least 8 characters.");
                return;
            }
            if (!java.util.Arrays.equals(pass, conf)) {
                showError("Master passwords do not match.");
                return;
            }

            try {
                vaultService.getDatabaseManager().initializeVault(pass);
                passwordField.setText("");
                confirmField.setText("");
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } catch (Exception ex) {
                showError("Initialization error: " + ex.getMessage());
            }
        } else {
            boolean success = vaultService.getDatabaseManager().authenticate(pass);
            if (success) {
                passwordField.setText("");
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                showError("Incorrect master password. Access denied.");
            }
        }
    }

    private void showError(String msg) {
        errorLabel.setText("⚠ " + msg);
        errorLabel.setVisible(true);
    }

    private void drawGeometricArtwork(Graphics2D g2, int w, int h) {
        // Geometric abstract grid and subtle concentric cybersecurity rings
        g2.setColor(new Color(0x1C, 0x1C, 0x1C));
        g2.setStroke(new BasicStroke(1.0f));

        // Concentric geometric circles centered at bottom right of the left panel
        int cx = Math.round(w * 0.85f);
        int cy = Math.round(h * 0.65f);

        for (int r = 60; r <= 360; r += 50) {
            g2.draw(new Ellipse2D.Float(cx - r, cy - r, 2 * r, 2 * r));
        }

        // Distinct crimson red security radial line
        g2.setColor(new Color(0xD9, 0x36, 0x36, 60));
        g2.drawLine(cx, cy, Math.round(w * 0.2f), Math.round(h * 0.25f));
        g2.draw(new Ellipse2D.Float(cx - 160, cy - 160, 320, 320));

        // Thin technical hash markings
        g2.setColor(new Color(0x28, 0x28, 0x28));
        for (int x = 40; x < w; x += 60) {
            g2.drawLine(x, h - 30, x, h - 22);
        }
    }
}
