package com.securevault.ui.panels;

import com.securevault.model.Credential;
import com.securevault.model.VaultStats;
import com.securevault.service.VaultService;
import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CircularScoreIndicator;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.Icons;
import com.securevault.ui.components.ScrollablePanel;
import com.securevault.ui.components.ToastNotification;
import com.securevault.ui.dialogs.CredentialDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * Editorial Dashboard for SecureVault.
 * Combines generous spacing, compact data sections, and an asymmetrical layout.
 */
public class DashboardPanel extends JPanel {
    private final VaultService vaultService = VaultService.getInstance();
    private final Consumer<SidebarPanel.NavItem> navigationHandler;
    private final CircularScoreIndicator scoreIndicator = new CircularScoreIndicator(92);
    private final JLabel scoreStatusLabel = new JLabel("HEALTHY");
    private final JLabel scoreDescriptionLabel = new JLabel();

    private final JLabel totalCountLabel = new JLabel("0");
    private final JLabel strongCountLabel = new JLabel("0");
    private final JLabel weakCountLabel = new JLabel("0");
    private final JLabel reusedCountLabel = new JLabel("0");

    private final JPanel recentListPanel = new JPanel();

    public DashboardPanel(Consumer<SidebarPanel.NavItem> navigationHandler) {
        this.navigationHandler = navigationHandler;
        setLayout(new BorderLayout());
        setBackground(AppColors.WARM_OFF_WHITE);

        initUI();
        refreshData();

        vaultService.addListener(this::refreshData);
    }

    private void initUI() {
        ScrollablePanel scrollContent = new ScrollablePanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(AppColors.WARM_OFF_WHITE);
        scrollContent.setBorder(new EmptyBorder(36, 44, 40, 44));

        // 1. Top Header Row: Greeting & Add Action
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);

        String greeting = getGreetingText();
        JLabel greetingLabel = new JLabel(greeting);
        greetingLabel.setFont(Typography.DISPLAY_TITLE);
        greetingLabel.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Your digital life, organised and protected.");
        subtitleLabel.setFont(Typography.BODY_MEDIUM);
        subtitleLabel.setForeground(AppColors.TEXT_SECONDARY);

        titleBlock.add(greetingLabel);
        titleBlock.add(subtitleLabel);
        headerRow.add(titleBlock, BorderLayout.WEST);

        CustomButton quickAddBtn = new CustomButton("+ Add Password", CustomButton.Variant.PRIMARY);
        quickAddBtn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            CredentialDialog dialog = new CredentialDialog(win, null);
            dialog.setVisible(true);
        });
        headerRow.add(quickAddBtn, BorderLayout.EAST);

        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollContent.add(headerRow);
        scrollContent.add(Box.createVerticalStrut(28));

        // 2. Health & Metrics Panel (Asymmetrical)
        JPanel healthCard = createHealthMetricsCard();
        healthCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        healthCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scrollContent.add(healthCard);
        scrollContent.add(Box.createVerticalStrut(32));

        // 3. Recent Accounts Section
        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentHeader.setOpaque(false);
        recentHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel recentTitle = new JLabel("RECENTLY UPDATED ACCOUNTS");
        recentTitle.setFont(Typography.LABEL_BOLD);
        recentTitle.setForeground(AppColors.TEXT_MUTED);
        recentHeader.add(recentTitle, BorderLayout.WEST);

        JLabel viewAllBtn = new JLabel("View All in Vault →");
        viewAllBtn.setFont(Typography.BODY_BOLD);
        viewAllBtn.setForeground(AppColors.CRIMSON_RED);
        viewAllBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAllBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (navigationHandler != null) {
                    navigationHandler.accept(SidebarPanel.NavItem.VAULT);
                }
            }
        });
        recentHeader.add(viewAllBtn, BorderLayout.EAST);

        scrollContent.add(recentHeader);
        scrollContent.add(Box.createVerticalStrut(12));

        // Recent accounts list container
        recentListPanel.setLayout(new BoxLayout(recentListPanel, BoxLayout.Y_AXIS));
        recentListPanel.setBackground(AppColors.PURE_WHITE);
        recentListPanel.setBorder(ThemeManager.createPanelBorder());
        recentListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentListPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));

        scrollContent.add(recentListPanel);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        ThemeManager.styleScrollBar(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHealthMetricsCard() {
        JPanel card = new JPanel(new BorderLayout(28, 0)) {
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
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Left section: Circular score indicator & status statement
        JPanel leftBlock = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leftBlock.setOpaque(false);
        leftBlock.add(scoreIndicator);

        JPanel statusTextCol = new JPanel();
        statusTextCol.setLayout(new BoxLayout(statusTextCol, BoxLayout.Y_AXIS));
        statusTextCol.setOpaque(false);

        JLabel healthTitle = new JLabel("VAULT RATING");
        healthTitle.setFont(Typography.LABEL_BOLD);
        healthTitle.setForeground(AppColors.TEXT_MUTED);

        scoreStatusLabel.setFont(Typography.bold(18));
        scoreStatusLabel.setForeground(AppColors.TEXT_PRIMARY);

        scoreDescriptionLabel.setFont(Typography.TINY);
        scoreDescriptionLabel.setForeground(AppColors.TEXT_SECONDARY);

        statusTextCol.add(Box.createVerticalStrut(4));
        statusTextCol.add(healthTitle);
        statusTextCol.add(Box.createVerticalStrut(2));
        statusTextCol.add(scoreStatusLabel);
        statusTextCol.add(Box.createVerticalStrut(2));
        statusTextCol.add(scoreDescriptionLabel);

        leftBlock.add(statusTextCol);
        card.add(leftBlock, BorderLayout.WEST);

        // Right section: 4 compact metrics divided by thin vertical borders
        JPanel metricsBlock = new JPanel(new GridLayout(1, 4, 0, 0));
        metricsBlock.setOpaque(false);

        metricsBlock.add(createMetricColumn("TOTAL", totalCountLabel, AppColors.TEXT_PRIMARY, "All stored"));
        metricsBlock.add(createMetricColumn("STRONG", strongCountLabel, AppColors.MUTED_TEAL, "High entropy"));
        metricsBlock.add(createMetricColumn("WEAK", weakCountLabel, AppColors.CRIMSON_RED, "Low entropy"));
        metricsBlock.add(createMetricColumn("REUSED", reusedCountLabel, AppColors.SOFT_AMBER, "Duplicated"));

        card.add(metricsBlock, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMetricColumn(String label, JLabel valueLabel, Color valueColor, String hint) {
        JPanel col = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Thin left divider
                g.setColor(AppColors.LIGHT_GREY);
                g.drawLine(0, 8, 0, getHeight() - 8);
            }
        };
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setBorder(new EmptyBorder(8, 12, 8, 4));

        JLabel title = new JLabel(label);
        title.setFont(Typography.TINY);
        title.setForeground(AppColors.TEXT_MUTED);

        valueLabel.setFont(Typography.bold(20));
        valueLabel.setForeground(valueColor);

        JLabel sub = new JLabel(hint);
        sub.setFont(Typography.TINY);
        sub.setForeground(AppColors.SLATE_GREY);

        col.add(title);
        col.add(Box.createVerticalStrut(4));
        col.add(valueLabel);
        col.add(Box.createVerticalStrut(4));
        col.add(sub);

        return col;
    }

    public void refreshData() {
        VaultStats stats = vaultService.getStats();

        scoreIndicator.setScore(stats.getHealthScore());
        totalCountLabel.setText(String.valueOf(stats.getTotalCount()));
        strongCountLabel.setText(String.valueOf(stats.getStrongCount()));
        weakCountLabel.setText(String.valueOf(stats.getWeakCount()));
        reusedCountLabel.setText(String.valueOf(stats.getReusedCount()));

        if (stats.getHealthScore() >= 85) {
            scoreStatusLabel.setText("Optimal Protection");
            scoreStatusLabel.setForeground(AppColors.MUTED_TEAL);
            scoreDescriptionLabel.setText("All credentials protected by verified entropy standards.");
        } else if (stats.getHealthScore() >= 60) {
            scoreStatusLabel.setText("Action Advised");
            scoreStatusLabel.setForeground(AppColors.SOFT_AMBER);
            scoreDescriptionLabel.setText(stats.getWeakCount() + " weak or duplicate credentials need review.");
        } else {
            scoreStatusLabel.setText("Critical Vulnerabilities");
            scoreStatusLabel.setForeground(AppColors.CRIMSON_RED);
            scoreDescriptionLabel.setText("Immediate password updates required to prevent credential stuffing.");
        }

        // Render recent list
        recentListPanel.removeAll();
        List<Credential> recent = stats.getRecentCredentials();

        if (recent.isEmpty()) {
            JPanel emptyState = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 36));
            emptyState.setOpaque(false);
            JLabel emptyLabel = new JLabel("No credentials found. Add your first account to begin monitoring.");
            emptyLabel.setFont(Typography.BODY_MEDIUM);
            emptyLabel.setForeground(AppColors.TEXT_MUTED);
            emptyState.add(emptyLabel);
            recentListPanel.add(emptyState);
        } else {
            for (int i = 0; i < recent.size(); i++) {
                Credential c = recent.get(i);
                recentListPanel.add(createRecentRow(c, i == recent.size() - 1));
            }
        }

        recentListPanel.revalidate();
        recentListPanel.repaint();
    }

    private JPanel createRecentRow(Credential c, boolean isLast) {
        JPanel row = new JPanel(new BorderLayout(16, 0)) {
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
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        // Left monogram icon + title + username
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        left.setOpaque(false);

        // Monogram
        JPanel iconPill = new JPanel() {
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
                int tx = (getWidth() - fm.stringWidth(initial)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initial, tx, ty);
                g2.dispose();
            }
        };
        iconPill.setOpaque(false);
        iconPill.setPreferredSize(new Dimension(34, 34));
        left.add(iconPill);

        JPanel details = new JPanel(new GridLayout(2, 1, 0, 2));
        details.setOpaque(false);
        JLabel titleLabel = new JLabel(c.getTitle());
        titleLabel.setFont(Typography.BODY_BOLD);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);

        JLabel userLabel = new JLabel(c.getUsername().isEmpty() ? "No username" : c.getUsername());
        userLabel.setFont(Typography.METADATA);
        userLabel.setForeground(AppColors.TEXT_MUTED);

        details.add(titleLabel);
        details.add(userLabel);
        left.add(details);

        row.add(left, BorderLayout.WEST);

        // Right category badge & copy password action
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 2));
        right.setOpaque(false);

        JLabel catBadge = new JLabel(c.getCategory().getDisplayName());
        catBadge.setFont(Typography.TINY);
        catBadge.setForeground(AppColors.TEXT_SECONDARY);
        catBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_SUBTLE, 1),
            new EmptyBorder(3, 8, 3, 8)
        ));
        right.add(catBadge);

        CustomButton copyBtn = new CustomButton("Copy", CustomButton.Variant.GHOST);
        copyBtn.setPreferredSize(new Dimension(64, 30));
        copyBtn.setFont(Typography.METADATA);
        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(c.getPassword()), null);
            ToastNotification.show(this, "Password copied to clipboard", true);
        });
        right.add(copyBtn);

        row.add(right, BorderLayout.EAST);
        return row;
    }

    private String getGreetingText() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good morning, welcome back.";
        if (hour < 17) return "Good afternoon, welcome back.";
        return "Good evening, welcome back.";
    }
}
