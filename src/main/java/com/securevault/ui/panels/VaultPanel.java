package com.securevault.ui.panels;

import com.securevault.model.Category;
import com.securevault.model.Credential;
import com.securevault.service.VaultService;
import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.CustomTextField;
import com.securevault.ui.components.Icons;
import com.securevault.ui.components.ScrollablePanel;
import com.securevault.ui.components.ToastNotification;
import com.securevault.ui.dialogs.ConfirmationDialog;
import com.securevault.ui.dialogs.CredentialDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Editorial credential management screen with structured table list and real-time filtering.
 */
public class VaultPanel extends JPanel {
    private final VaultService vaultService = VaultService.getInstance();
    private final CustomTextField searchField = new CustomTextField("Search by service, username, or URL...");
    private Category selectedCategory = Category.ALL;
    private final JPanel rowsContainer = new JPanel();
    private final JLabel countLabel = new JLabel("0 items");

    public VaultPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColors.WARM_OFF_WHITE);

        initUI();
        refreshList();

        vaultService.addListener(this::refreshList);
    }

    private void initUI() {
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppColors.WARM_OFF_WHITE);
        content.setBorder(new EmptyBorder(36, 44, 40, 44));

        // 1. Top Header Row
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("My Vault");
        title.setFont(Typography.DISPLAY_TITLE);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage, rotate, and audit your encrypted credentials.");
        subtitle.setFont(Typography.BODY_MEDIUM);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(subtitle);
        headerRow.add(titleBlock, BorderLayout.WEST);

        CustomButton addBtn = new CustomButton("+ Add Password", CustomButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddDialog());
        headerRow.add(addBtn, BorderLayout.EAST);

        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(headerRow);
        content.add(Box.createVerticalStrut(24));

        // 2. Filter & Search Controls Bar
        JPanel controlsBar = new JPanel(new BorderLayout(16, 0));
        controlsBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlsBar.setOpaque(false);
        controlsBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        // Search input
        searchField.setPreferredSize(new Dimension(280, 40));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                refreshList();
            }
        });
        controlsBar.add(searchField, BorderLayout.WEST);

        // Category Filter Pills
        JPanel filterPills = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterPills.setOpaque(false);

        for (Category cat : new Category[]{Category.ALL, Category.LOGINS, Category.BANKING, Category.CLOUD, Category.DEVELOPMENT, Category.PERSONAL}) {
            filterPills.add(createCategoryPill(cat));
        }

        controlsBar.add(filterPills, BorderLayout.CENTER);
        content.add(controlsBar);
        content.add(Box.createVerticalStrut(20));

        // 3. Table Header Bar
        JPanel tableHeader = new JPanel(new GridBagLayout());
        tableHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableHeader.setOpaque(false);
        tableHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        tableHeader.setBorder(new EmptyBorder(0, 20, 8, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;

        // Service column
        gbc.weightx = 0.30;
        gbc.gridx = 0;
        tableHeader.add(createHeaderLabel("SERVICE & IDENTITY"), gbc);

        // Category column
        gbc.weightx = 0.20;
        gbc.gridx = 1;
        tableHeader.add(createHeaderLabel("CATEGORY"), gbc);

        // Last updated column
        gbc.weightx = 0.20;
        gbc.gridx = 2;
        tableHeader.add(createHeaderLabel("LAST ROTATED"), gbc);

        // Actions column
        gbc.weightx = 0.30;
        gbc.gridx = 3;
        JLabel actLabel = createHeaderLabel("ACTIONS");
        actLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        tableHeader.add(actLabel, gbc);

        content.add(tableHeader);

        // 4. Rows Container inside White Table Panel
        rowsContainer.setLayout(new BoxLayout(rowsContainer, BoxLayout.Y_AXIS));
        rowsContainer.setBackground(AppColors.PURE_WHITE);
        rowsContainer.setBorder(ThemeManager.createPanelBorder());
        rowsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(rowsContainer);
        content.add(Box.createVerticalStrut(16));

        // Bottom Count Summary
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setOpaque(false);
        countLabel.setFont(Typography.METADATA);
        countLabel.setForeground(AppColors.TEXT_MUTED);
        footer.add(countLabel);
        content.add(footer);

        JScrollPane scrollPane = new JScrollPane(content);
        ThemeManager.styleScrollBar(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Typography.LABEL_BOLD);
        l.setForeground(AppColors.TEXT_MUTED);
        return l;
    }

    private JComponent createCategoryPill(Category cat) {
        String labelText;
        switch (cat) {
            case ALL: labelText = "All"; break;
            case LOGINS: labelText = "Logins"; break;
            case BANKING: labelText = "Finance"; break;
            case CLOUD: labelText = "Cloud"; break;
            case DEVELOPMENT: labelText = "Dev Keys"; break;
            case PERSONAL: labelText = "Personal"; break;
            default: labelText = cat.getDisplayName(); break;
        }

        JLabel pill = new JLabel(labelText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ThemeManager.applyQualityRendering(g2);
                boolean isActive = (selectedCategory == cat);
                if (isActive) {
                    g2.setColor(AppColors.JET_BLACK);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                } else {
                    g2.setColor(AppColors.PURE_WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                    g2.setColor(AppColors.BORDER_SUBTLE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        pill.setFont(Typography.LABEL_BOLD);
        pill.setForeground(selectedCategory == cat ? AppColors.PURE_WHITE : AppColors.TEXT_SECONDARY);
        pill.setBorder(new EmptyBorder(8, 14, 8, 14));
        pill.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        pill.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedCategory = cat;
                refreshList();
            }
        });

        return pill;
    }

    public void refreshList() {
        String query = searchField.getText();
        List<Credential> filtered = vaultService.search(query, selectedCategory);

        rowsContainer.removeAll();

        if (filtered.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setOpaque(false);
            empty.setBorder(new EmptyBorder(48, 20, 48, 20));

            JLabel icon = new JLabel(Icons.getIcon("search", 32, AppColors.SLATE_GREY));
            icon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel title = new JLabel("No matching credentials found");
            title.setFont(Typography.CARD_TITLE);
            title.setForeground(AppColors.TEXT_PRIMARY);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel desc = new JLabel("Try adjusting your keyword filter or add a new account record.");
            desc.setFont(Typography.BODY_SMALL);
            desc.setForeground(AppColors.TEXT_MUTED);
            desc.setAlignmentX(Component.CENTER_ALIGNMENT);

            empty.add(icon);
            empty.add(Box.createVerticalStrut(12));
            empty.add(title);
            empty.add(Box.createVerticalStrut(4));
            empty.add(desc);

            rowsContainer.add(empty);
            countLabel.setText("No entries matching current filter");
        } else {
            for (int i = 0; i < filtered.size(); i++) {
                Credential c = filtered.get(i);
                rowsContainer.add(createTableRow(c, i == filtered.size() - 1));
            }
            countLabel.setText("Showing " + filtered.size() + " credential" + (filtered.size() == 1 ? "" : "s"));
        }

        rowsContainer.revalidate();
        rowsContainer.repaint();
    }

    private JPanel createTableRow(Credential c, boolean isLast) {
        JPanel row = new JPanel(new GridBagLayout()) {
            private boolean isHover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (isHover) {
                    g.setColor(AppColors.ROW_HOVER);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                super.paintComponent(g);
                if (!isLast) {
                    g.setColor(AppColors.LIGHT_GREY);
                    g.drawLine(20, getHeight() - 1, getWidth() - 20, getHeight() - 1);
                }
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(14, 20, 14, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;

        // 1. Service & Username
        gbc.weightx = 0.30;
        gbc.gridx = 0;
        JPanel serviceBlock = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        serviceBlock.setOpaque(false);

        // Monogram
        JPanel monogram = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ThemeManager.applyQualityRendering(g2);
                g2.setColor(AppColors.BADGE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(AppColors.TEXT_PRIMARY);
                g2.setFont(Typography.bold(12));
                FontMetrics fm = g2.getFontMetrics();
                String initial = c.getInitial();
                int x = (getWidth() - fm.stringWidth(initial)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initial, x, y);
                g2.dispose();
            }
        };
        monogram.setPreferredSize(new Dimension(34, 34));
        monogram.setOpaque(false);
        serviceBlock.add(monogram);

        JPanel textBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        textBlock.setOpaque(false);
        JLabel titleLabel = new JLabel(c.getTitle());
        titleLabel.setFont(Typography.BODY_BOLD);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);

        JLabel userLabel = new JLabel(c.getUsername().isEmpty() ? c.getUrl() : c.getUsername());
        userLabel.setFont(Typography.METADATA);
        userLabel.setForeground(AppColors.TEXT_MUTED);

        textBlock.add(titleLabel);
        textBlock.add(userLabel);
        serviceBlock.add(textBlock);
        row.add(serviceBlock, gbc);

        // 2. Category
        gbc.weightx = 0.20;
        gbc.gridx = 1;
        JLabel catLabel = new JLabel(c.getCategory().getDisplayName());
        catLabel.setFont(Typography.METADATA);
        catLabel.setForeground(AppColors.TEXT_SECONDARY);
        row.add(catLabel, gbc);

        // 3. Last Updated
        gbc.weightx = 0.20;
        gbc.gridx = 2;
        JLabel timeLabel = new JLabel(formatRelativeTime(c.getUpdatedAt()));
        timeLabel.setFont(Typography.METADATA);
        timeLabel.setForeground(AppColors.TEXT_MUTED);
        row.add(timeLabel, gbc);

        // 4. Action Buttons
        gbc.weightx = 0.30;
        gbc.gridx = 3;
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        // Show/Hide password toggle button
        CustomButton toggleBtn = new CustomButton("Reveal", CustomButton.Variant.GHOST);
        toggleBtn.setPreferredSize(new Dimension(68, 32));
        toggleBtn.setFont(Typography.METADATA);
        toggleBtn.addActionListener(e -> {
            boolean isMasked = toggleBtn.getText().equals("Reveal");
            if (isMasked) {
                toggleBtn.setText(c.getPassword());
                toggleBtn.setFont(Typography.mono(11, true));
            } else {
                toggleBtn.setText("Reveal");
                toggleBtn.setFont(Typography.METADATA);
            }
        });
        actions.add(toggleBtn);

        // Copy button
        CustomButton copyBtn = new CustomButton("Copy", CustomButton.Variant.GHOST);
        copyBtn.setPreferredSize(new Dimension(60, 32));
        copyBtn.setFont(Typography.METADATA);
        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(c.getPassword()), null);
            ToastNotification.show(this, "Password copied to clipboard", true);
        });
        actions.add(copyBtn);

        // Edit button
        CustomButton editBtn = new CustomButton("", CustomButton.Variant.GHOST);
        editBtn.setIcon(Icons.getIcon("edit", 14, AppColors.TEXT_SECONDARY));
        editBtn.setPreferredSize(new Dimension(32, 32));
        editBtn.setToolTipText("Edit Credential");
        editBtn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            CredentialDialog dlg = new CredentialDialog(win, c);
            dlg.setVisible(true);
        });
        actions.add(editBtn);

        // Delete button
        CustomButton delBtn = new CustomButton("", CustomButton.Variant.GHOST);
        delBtn.setIcon(Icons.getIcon("trash", 14, AppColors.DEEP_BURGUNDY));
        delBtn.setPreferredSize(new Dimension(32, 32));
        delBtn.setToolTipText("Delete Credential");
        delBtn.addActionListener(e -> {
            boolean confirm = ConfirmationDialog.showConfirmation(
                this,
                "Delete Credential",
                "Are you sure you want to delete '" + c.getTitle() + "'? This action cannot be undone.",
                "Delete"
            );
            if (confirm) {
                try {
                    vaultService.deleteCredential(c.getId());
                    ToastNotification.show(this, "Credential deleted", false);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to delete: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actions.add(delBtn);

        row.add(actions, gbc);

        return row;
    }

    private void openAddDialog() {
        Window win = SwingUtilities.getWindowAncestor(this);
        CredentialDialog dlg = new CredentialDialog(win, null);
        dlg.setVisible(true);
    }

    private String formatRelativeTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long dayMs = 24L * 60L * 60L * 1000L;
        if (diff < dayMs) return "Today";
        if (diff < 2 * dayMs) return "Yesterday";
        long days = diff / dayMs;
        if (days < 30) return days + " days ago";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(new Date(timestamp));
    }
}
