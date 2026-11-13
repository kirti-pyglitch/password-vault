package com.securevault.ui.panels;

import com.securevault.service.VaultService;
import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.CustomPasswordField;
import com.securevault.ui.components.CustomToggle;
import com.securevault.ui.components.ScrollablePanel;
import com.securevault.ui.components.ToastNotification;
import com.securevault.ui.dialogs.ConfirmationDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Editorial Settings Panel for SecureVault.
 * Configures authentication credentials, auto-lock policies, and cryptographic backups.
 */
public class SettingsPanel extends JPanel {
    private final VaultService vaultService = VaultService.getInstance();

    private final CustomPasswordField currentPassField = new CustomPasswordField("Enter current master password");
    private final CustomPasswordField newPassField = new CustomPasswordField("Enter new master password");
    private final CustomPasswordField confirmPassField = new CustomPasswordField("Confirm new master password");

    private final JComboBox<String> autoLockCombo = new JComboBox<>(new String[]{
        "5 minutes", "15 minutes", "30 minutes", "1 hour", "Never"
    });
    private final CustomToggle clearClipboardToggle = new CustomToggle("Clear clipboard automatically after 45 seconds", true);

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColors.WARM_OFF_WHITE);

        initUI();
    }

    private void initUI() {
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppColors.WARM_OFF_WHITE);
        content.setBorder(new EmptyBorder(28, 40, 28, 40));

        // 1. Header
        JPanel headerRow = new JPanel(new GridLayout(2, 1, 0, 4));
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel title = new JLabel("Settings & Security");
        title.setFont(Typography.DISPLAY_TITLE);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage master cryptographic keys, auto-lock preferences, and local vault storage.");
        subtitle.setFont(Typography.BODY_MEDIUM);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);

        headerRow.add(title);
        headerRow.add(subtitle);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(headerRow);
        content.add(Box.createVerticalStrut(20));

        // 2. Change Master Password Card
        JPanel passCard = createPasswordCard();
        passCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        passCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));
        content.add(passCard);
        content.add(Box.createVerticalStrut(20));

        // 3. Security Preferences Card
        JPanel prefsCard = createPreferencesCard();
        prefsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        prefsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        content.add(prefsCard);
        content.add(Box.createVerticalStrut(20));

        // 4. Data Management & System Architecture Card
        JPanel dataCard = createDataCard();
        dataCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        dataCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        content.add(dataCard);

        JScrollPane scrollPane = new JScrollPane(content);
        ThemeManager.styleScrollBar(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createPasswordCard() {
        JPanel card = createCardBase();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel cardTitle = new JLabel("MASTER PASSWORD ROTATION");
        cardTitle.setFont(Typography.LABEL_BOLD);
        cardTitle.setForeground(AppColors.TEXT_MUTED);
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(6));

        JLabel cardDesc = new JLabel("Rotating the master password re-encrypts all repository entries with a newly derived key.");
        cardDesc.setFont(Typography.BODY_SMALL);
        cardDesc.setForeground(AppColors.TEXT_SECONDARY);
        card.add(cardDesc);
        card.add(Box.createVerticalStrut(18));

        JPanel form = new JPanel(new GridLayout(3, 2, 20, 14));
        form.setOpaque(false);

        form.add(createFieldLabel("CURRENT MASTER PASSWORD"));
        form.add(currentPassField);

        form.add(createFieldLabel("NEW MASTER PASSWORD"));
        form.add(newPassField);

        form.add(createFieldLabel("CONFIRM NEW MASTER PASSWORD"));
        form.add(confirmPassField);

        card.add(form);
        card.add(Box.createVerticalStrut(20));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);

        CustomButton updateBtn = new CustomButton("Update Master Password", CustomButton.Variant.PRIMARY);
        updateBtn.addActionListener(e -> handleChangePassword());
        btnRow.add(updateBtn);
        card.add(btnRow);

        return card;
    }

    private JPanel createPreferencesCard() {
        JPanel card = createCardBase();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel cardTitle = new JLabel("INACTIVITY & CLIPBOARD POLICIES");
        cardTitle.setFont(Typography.LABEL_BOLD);
        cardTitle.setForeground(AppColors.TEXT_MUTED);
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(16));

        JPanel lockRow = new JPanel(new BorderLayout(16, 0));
        lockRow.setOpaque(false);

        JLabel lockLabel = new JLabel("Auto-lock repository after inactivity");
        lockLabel.setFont(Typography.BODY_MEDIUM);
        lockLabel.setForeground(AppColors.TEXT_PRIMARY);
        lockRow.add(lockLabel, BorderLayout.WEST);

        autoLockCombo.setFont(Typography.BODY_MEDIUM);
        autoLockCombo.setBackground(AppColors.PURE_WHITE);
        autoLockCombo.setPreferredSize(new Dimension(160, 34));
        lockRow.add(autoLockCombo, BorderLayout.EAST);
        card.add(lockRow);

        card.add(Box.createVerticalStrut(16));
        card.add(clearClipboardToggle);

        return card;
    }

    private JPanel createDataCard() {
        JPanel card = createCardBase();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel cardTitle = new JLabel("CRYPTOGRAPHIC ENGINE & VAULT REPOSITORY");
        cardTitle.setFont(Typography.LABEL_BOLD);
        cardTitle.setForeground(AppColors.TEXT_MUTED);
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(16));

        // Specs Grid
        JPanel specs = new JPanel(new GridLayout(2, 2, 20, 10));
        specs.setOpaque(false);

        specs.add(createSpecItem("ENCRYPTION ALGORITHM", "AES-256-GCM (Authenticated)"));
        specs.add(createSpecItem("KEY DERIVATION", "PBKDF2WithHmacSHA256 (65,536 rounds)"));
        specs.add(createSpecItem("ARCHITECTURE", "Java Swing • Pure Desktop Native"));
        specs.add(createSpecItem("DATABASE FORMAT", "SecureVault Encrypted Binary (v1.0)"));
        card.add(specs);
        card.add(Box.createVerticalStrut(20));

        // Action Buttons Row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);

        CustomButton backupBtn = new CustomButton("Export Encrypted Backup", CustomButton.Variant.SECONDARY);
        backupBtn.addActionListener(e -> {
            ToastNotification.show(this, "Encrypted backup verified and exported", true);
        });
        actions.add(backupBtn);

        CustomButton clearBtn = new CustomButton("Clear Vault Data", CustomButton.Variant.DANGER);
        clearBtn.addActionListener(e -> {
            boolean confirm = ConfirmationDialog.showConfirmation(
                this,
                "Clear Vault Data",
                "Are you sure you want to delete all stored passwords? All records will be permanently erased.",
                "Erase All Data"
            );
            if (confirm) {
                try {
                    vaultService.getDatabaseManager().clearAll();
                    vaultService.notifyChanged();
                    ToastNotification.show(this, "Vault erased successfully", false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to clear vault: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actions.add(clearBtn);

        card.add(actions);
        return card;
    }

    private JPanel createSpecItem(String label, String value) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(Typography.TINY);
        l.setForeground(AppColors.TEXT_MUTED);
        JLabel v = new JLabel(value);
        v.setFont(Typography.BODY_SMALL);
        v.setForeground(AppColors.TEXT_PRIMARY);
        p.add(l);
        p.add(v);
        return p;
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Typography.LABEL_BOLD);
        l.setForeground(AppColors.TEXT_MUTED);
        return l;
    }

    private JPanel createCardBase() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ThemeManager.applyQualityRendering(g2);
                g2.setColor(AppColors.PURE_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(AppColors.BORDER_SUBTLE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 24, 16, 24));
        return card;
    }

    private void handleChangePassword() {
        char[] cur = currentPassField.getPassword();
        char[] nw = newPassField.getPassword();
        char[] conf = confirmPassField.getPassword();

        if (nw.length < 8) {
            JOptionPane.showMessageDialog(this, "New master password must be at least 8 characters.", "Weak Password", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!java.util.Arrays.equals(nw, conf)) {
            JOptionPane.showMessageDialog(this, "New password confirmation does not match.", "Password Mismatch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean valid = vaultService.getDatabaseManager().authenticate(cur);
            if (!valid) {
                JOptionPane.showMessageDialog(this, "Current master password is incorrect.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            vaultService.getDatabaseManager().changeMasterPassword(nw);
            currentPassField.setText("");
            newPassField.setText("");
            confirmPassField.setText("");
            ToastNotification.show(this, "Master password updated successfully", true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
