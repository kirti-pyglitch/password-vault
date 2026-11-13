package com.securevault.ui.dialogs;

import com.securevault.crypto.VaultEncryption;
import com.securevault.model.Category;
import com.securevault.model.Credential;
import com.securevault.service.VaultService;
import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.CustomPasswordField;
import com.securevault.ui.components.CustomTextField;
import com.securevault.ui.components.HorizontalScoreIndicator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Editorial modal dialog for adding or editing a vault credential record.
 */
public class CredentialDialog extends JDialog {
    private final Credential credential;
    private boolean saved = false;

    private final CustomTextField titleField = new CustomTextField("e.g. GitHub, Google, AWS");
    private final CustomTextField urlField = new CustomTextField("e.g. https://github.com");
    private final CustomTextField usernameField = new CustomTextField("e.g. user@domain.com");
    private final CustomPasswordField passwordField = new CustomPasswordField("Master account password");
    private final JComboBox<Category> categoryCombo = new JComboBox<>(new Category[]{
        Category.LOGINS, Category.BANKING, Category.CLOUD, Category.DEVELOPMENT, Category.PERSONAL
    });
    private final JTextArea notesArea = new JTextArea(3, 20);
    private final HorizontalScoreIndicator strengthBar = new HorizontalScoreIndicator();

    public CredentialDialog(Window owner, Credential existing) {
        super(owner, existing == null ? "Add Credential" : "Edit Credential", ModalityType.APPLICATION_MODAL);
        this.credential = existing != null ? existing.copy() : new Credential();

        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(AppColors.PURE_WHITE);
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_SUBTLE, 1),
            new EmptyBorder(28, 32, 28, 32)
        ));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel(existing == null ? "New Credential" : "Edit Credential");
        titleLabel.setFont(Typography.SECTION_TITLE);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Store encrypted account records with AES-256 protection");
        subtitleLabel.setFont(Typography.METADATA);
        subtitleLabel.setForeground(AppColors.TEXT_MUTED);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        // Form Fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        int row = 0;

        // Title
        gbc.gridy = row++;
        form.add(createFieldLabel("SERVICE / WEBSITE NAME"), gbc);
        gbc.gridy = row++;
        form.add(titleField, gbc);

        // URL
        gbc.gridy = row++;
        form.add(createFieldLabel("WEBSITE URL"), gbc);
        gbc.gridy = row++;
        form.add(urlField, gbc);

        // Username
        gbc.gridy = row++;
        form.add(createFieldLabel("USERNAME / EMAIL"), gbc);
        gbc.gridy = row++;
        form.add(usernameField, gbc);

        // Password + Quick Generator
        gbc.gridy = row++;
        form.add(createFieldLabel("PASSWORD"), gbc);
        gbc.gridy = row++;

        JPanel passRow = new JPanel(new BorderLayout(8, 0));
        passRow.setOpaque(false);
        passRow.add(passwordField, BorderLayout.CENTER);

        CustomButton genBtn = new CustomButton("Generate", CustomButton.Variant.SECONDARY);
        genBtn.setPreferredSize(new Dimension(90, 40));
        genBtn.addActionListener(e -> {
            String gen = VaultService.getInstance().generatePassword(18, true, true, true, true, true);
            passwordField.setText(gen);
            updateStrength();
        });
        passRow.add(genBtn, BorderLayout.EAST);
        form.add(passRow, gbc);

        // Strength Bar
        gbc.gridy = row++;
        strengthBar.setPreferredSize(new Dimension(320, 24));
        form.add(strengthBar, gbc);

        // Category
        gbc.gridy = row++;
        form.add(createFieldLabel("CATEGORY"), gbc);
        gbc.gridy = row++;
        categoryCombo.setFont(Typography.BODY_MEDIUM);
        categoryCombo.setBackground(AppColors.PURE_WHITE);
        categoryCombo.setForeground(AppColors.TEXT_PRIMARY);
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category c) {
                    l.setText(c.getDisplayName());
                }
                l.setBorder(new EmptyBorder(6, 10, 6, 10));
                l.setFont(Typography.BODY_MEDIUM);
                return l;
            }
        });
        categoryCombo.setPreferredSize(new Dimension(320, 38));
        form.add(categoryCombo, gbc);

        // Notes
        gbc.gridy = row++;
        form.add(createFieldLabel("NOTES (OPTIONAL)"), gbc);
        gbc.gridy = row++;
        notesArea.setFont(Typography.BODY_SMALL);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_SUBTLE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(320, 65));
        notesScroll.setBorder(BorderFactory.createEmptyBorder());
        form.add(notesScroll, gbc);

        root.add(form, BorderLayout.CENTER);

        // Action Buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton cancelBtn = new CustomButton("Cancel", CustomButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());

        CustomButton saveBtn = new CustomButton("Save Credential", CustomButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> save());

        actions.add(cancelBtn);
        actions.add(saveBtn);
        root.add(actions, BorderLayout.SOUTH);

        add(root, BorderLayout.CENTER);

        // Populate existing data
        if (existing != null) {
            titleField.setText(existing.getTitle());
            urlField.setText(existing.getUrl());
            usernameField.setText(existing.getUsername());
            passwordField.setText(existing.getPassword());
            categoryCombo.setSelectedItem(existing.getCategory());
            notesArea.setText(existing.getNotes());
        }

        updateStrength();

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateStrength();
            }
        });

        pack();
        setLocationRelativeTo(owner);
    }

    private JLabel createFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Typography.LABEL_BOLD);
        l.setForeground(AppColors.TEXT_MUTED);
        return l;
    }

    private void updateStrength() {
        String pass = new String(passwordField.getPassword());
        int score = VaultEncryption.calculatePasswordStrength(pass);
        strengthBar.setScore(score, "Security Entropy");
    }

    private void save() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            titleField.requestFocus();
            return;
        }

        credential.setTitle(title);
        credential.setUrl(urlField.getText().trim());
        credential.setUsername(usernameField.getText().trim());
        credential.setPassword(new String(passwordField.getPassword()));
        credential.setCategory((Category) categoryCombo.getSelectedItem());
        credential.setNotes(notesArea.getText().trim());

        try {
            VaultService.getInstance().saveCredential(credential);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save credential: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}

