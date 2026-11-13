package com.securevault.ui.panels;

import com.securevault.model.AuditReport;
import com.securevault.model.Credential;
import com.securevault.service.VaultService;
import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CircularScoreIndicator;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.Icons;
import com.securevault.ui.components.ScrollablePanel;
import com.securevault.ui.dialogs.CredentialDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Editorial Security Audit overview screen.
 * Discloses weak, reused, and stale credentials with disciplined color coding:
 * Teal (#277C70), Amber (#D89A35), and Red (#D93636 / #702B35).
 */
public class SecurityAuditPanel extends JPanel {
    private final VaultService vaultService = VaultService.getInstance();

    private final CircularScoreIndicator circularScore = new CircularScoreIndicator(105);
    private final JLabel statusTitleLabel = new JLabel("Audit Status");
    private final JLabel statusSubtitleLabel = new JLabel();

    private final JLabel weakCountLabel = new JLabel("0");
    private final JLabel reusedCountLabel = new JLabel("0");
    private final JLabel oldCountLabel = new JLabel("0");
    private final JLabel missingCountLabel = new JLabel("0");

    private final JPanel recommendationsPanel = new JPanel();
    private final JPanel issuesListPanel = new JPanel();

    public SecurityAuditPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColors.WARM_OFF_WHITE);

        initUI();
        refreshAudit();

        vaultService.addListener(this::refreshAudit);
    }

    private void initUI() {
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppColors.WARM_OFF_WHITE);
        content.setBorder(new EmptyBorder(36, 44, 40, 44));

        // 1. Header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("Security Audit");
        title.setFont(Typography.DISPLAY_TITLE);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Cryptographic assessment and exposure detection across all vault credentials.");
        subtitle.setFont(Typography.BODY_MEDIUM);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(subtitle);
        headerRow.add(titleBlock, BorderLayout.WEST);

        CustomButton retestBtn = new CustomButton("Re-scan Vault", CustomButton.Variant.SECONDARY);
        retestBtn.setIcon(Icons.getIcon("refresh", 14, AppColors.TEXT_PRIMARY));
        retestBtn.addActionListener(e -> refreshAudit());
        headerRow.add(retestBtn, BorderLayout.EAST);

        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(headerRow);
        content.add(Box.createVerticalStrut(28));

        // 2. Health & Vulnerability Breakdown Card
        JPanel overviewCard = createOverviewCard();
        overviewCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        overviewCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        content.add(overviewCard);
        content.add(Box.createVerticalStrut(28));

        // 3. Recommended Actions Section
        JPanel recHeader = new JPanel(new BorderLayout());
        recHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        recHeader.setOpaque(false);
        recHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel recTitle = new JLabel("RECOMMENDED SECURITY IMPROVEMENTS");
        recTitle.setFont(Typography.LABEL_BOLD);
        recTitle.setForeground(AppColors.TEXT_MUTED);
        recHeader.add(recTitle, BorderLayout.WEST);
        content.add(recHeader);
        content.add(Box.createVerticalStrut(10));

        recommendationsPanel.setLayout(new BoxLayout(recommendationsPanel, BoxLayout.Y_AXIS));
        recommendationsPanel.setBackground(AppColors.PURE_WHITE);
        recommendationsPanel.setBorder(ThemeManager.createPanelBorder());
        recommendationsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        recommendationsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        content.add(recommendationsPanel);
        content.add(Box.createVerticalStrut(28));

        // 4. Detected Vulnerabilities & Action List
        JPanel issuesHeader = new JPanel(new BorderLayout());
        issuesHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        issuesHeader.setOpaque(false);
        issuesHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel issuesTitle = new JLabel("IDENTIFIED VULNERABILITIES & EXPOSURES");
        issuesTitle.setFont(Typography.LABEL_BOLD);
        issuesTitle.setForeground(AppColors.TEXT_MUTED);
        issuesHeader.add(issuesTitle, BorderLayout.WEST);
        content.add(issuesHeader);
        content.add(Box.createVerticalStrut(10));

        issuesListPanel.setLayout(new BoxLayout(issuesListPanel, BoxLayout.Y_AXIS));
        issuesListPanel.setBackground(AppColors.PURE_WHITE);
        issuesListPanel.setBorder(ThemeManager.createPanelBorder());
        issuesListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(issuesListPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        ThemeManager.styleScrollBar(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createOverviewCard() {
        JPanel card = new JPanel(new BorderLayout(24, 0)) {
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
        card.setBorder(new EmptyBorder(18, 24, 18, 24));

        // Left Score & Status
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);
        left.add(circularScore);

        JPanel statusCol = new JPanel();
        statusCol.setLayout(new BoxLayout(statusCol, BoxLayout.Y_AXIS));
        statusCol.setOpaque(false);

        JLabel label = new JLabel("OVERALL HEALTH");
        label.setFont(Typography.LABEL_BOLD);
        label.setForeground(AppColors.TEXT_MUTED);

        statusTitleLabel.setFont(Typography.bold(18));
        statusTitleLabel.setForeground(AppColors.TEXT_PRIMARY);

        statusSubtitleLabel.setFont(Typography.TINY);
        statusSubtitleLabel.setForeground(AppColors.TEXT_SECONDARY);

        statusCol.add(Box.createVerticalStrut(4));
        statusCol.add(label);
        statusCol.add(Box.createVerticalStrut(2));
        statusCol.add(statusTitleLabel);
        statusCol.add(Box.createVerticalStrut(2));
        statusCol.add(statusSubtitleLabel);

        left.add(statusCol);
        card.add(left, BorderLayout.WEST);

        // Right 4 metrics
        JPanel metrics = new JPanel(new GridLayout(1, 4, 0, 0));
        metrics.setOpaque(false);

        metrics.add(createAuditMetric("WEAK", weakCountLabel, AppColors.CRIMSON_RED, "Low entropy"));
        metrics.add(createAuditMetric("REUSED", reusedCountLabel, AppColors.DEEP_BURGUNDY, "Shared keys"));
        metrics.add(createAuditMetric("STALE", oldCountLabel, AppColors.SOFT_AMBER, "> 90 days"));
        metrics.add(createAuditMetric("INCOMPLETE", missingCountLabel, AppColors.SLATE_GREY, "Missing fields"));

        card.add(metrics, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAuditMetric(String title, JLabel valLabel, Color col, String desc) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(AppColors.LIGHT_GREY);
                g.drawLine(0, 8, 0, getHeight() - 8);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 12, 8, 4));

        JLabel t = new JLabel(title);
        t.setFont(Typography.TINY);
        t.setForeground(AppColors.TEXT_MUTED);

        valLabel.setFont(Typography.bold(20));
        valLabel.setForeground(col);

        JLabel d = new JLabel(desc);
        d.setFont(Typography.TINY);
        d.setForeground(AppColors.SLATE_GREY);

        p.add(t);
        p.add(Box.createVerticalStrut(4));
        p.add(valLabel);
        p.add(Box.createVerticalStrut(4));
        p.add(d);

        return p;
    }

    public void refreshAudit() {
        AuditReport report = vaultService.generateAuditReport();

        circularScore.setScore(report.getHealthScore());
        weakCountLabel.setText(String.valueOf(report.getWeakCredentials().size()));
        reusedCountLabel.setText(String.valueOf(report.getReusedCredentials().size()));
        oldCountLabel.setText(String.valueOf(report.getOldCredentials().size()));
        missingCountLabel.setText(String.valueOf(report.getMissingInfoCredentials().size()));

        statusTitleLabel.setText(report.getStatusLabel());
        if (report.getHealthScore() >= 85) {
            statusTitleLabel.setForeground(AppColors.MUTED_TEAL);
            statusSubtitleLabel.setText("No critical security gaps detected.");
        } else if (report.getHealthScore() >= 60) {
            statusTitleLabel.setForeground(AppColors.SOFT_AMBER);
            statusSubtitleLabel.setText("Credentials identified requiring manual rotation.");
        } else {
            statusTitleLabel.setForeground(AppColors.CRIMSON_RED);
            statusSubtitleLabel.setText("High vulnerability to credential stuffing attacks.");
        }

        // Render Recommendations
        recommendationsPanel.removeAll();
        List<String> recs = report.getRecommendations();
        for (int i = 0; i < recs.size(); i++) {
            String rec = recs.get(i);
            recommendationsPanel.add(createRecommendationRow(rec, i == recs.size() - 1));
        }
        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();

        // Render Issues
        issuesListPanel.removeAll();
        List<AuditReport.AuditIssue> issues = report.getAllIssues();
        if (issues.isEmpty()) {
            JPanel clean = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 32));
            clean.setOpaque(false);
            JLabel cleanLabel = new JLabel("All accounts meet enterprise security baselines.");
            cleanLabel.setFont(Typography.BODY_MEDIUM);
            cleanLabel.setForeground(AppColors.MUTED_TEAL);
            clean.add(cleanLabel);
            issuesListPanel.add(clean);
        } else {
            for (int i = 0; i < issues.size(); i++) {
                AuditReport.AuditIssue issue = issues.get(i);
                issuesListPanel.add(createIssueRow(issue, i == issues.size() - 1));
            }
        }
        issuesListPanel.revalidate();
        issuesListPanel.repaint();
    }

    private JPanel createRecommendationRow(String text, boolean isLast) {
        JPanel r = new JPanel(new BorderLayout(14, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!isLast) {
                    g.setColor(AppColors.LIGHT_GREY);
                    g.drawLine(20, getHeight() - 1, getWidth() - 20, getHeight() - 1);
                }
            }
        };
        r.setOpaque(false);
        r.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel bullet = new JLabel("•");
        bullet.setFont(Typography.bold(14));
        bullet.setForeground(AppColors.CRIMSON_RED);
        r.add(bullet, BorderLayout.WEST);

        JLabel label = new JLabel(text);
        label.setFont(Typography.BODY_MEDIUM);
        label.setForeground(AppColors.TEXT_PRIMARY);
        r.add(label, BorderLayout.CENTER);

        return r;
    }

    private JPanel createIssueRow(AuditReport.AuditIssue issue, boolean isLast) {
        JPanel row = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!isLast) {
                    g.setColor(AppColors.LIGHT_GREY);
                    g.drawLine(20, getHeight() - 1, getWidth() - 20, getHeight() - 1);
                }
            }
        };
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Severity indicator tag
        Color sevColor;
        String sevText;
        switch (issue.getSeverity()) {
            case CRITICAL:
                sevColor = AppColors.CRIMSON_RED;
                sevText = "CRITICAL";
                break;
            case WARNING:
                sevColor = AppColors.SOFT_AMBER;
                sevText = "WARNING";
                break;
            default:
                sevColor = AppColors.MUTED_TEAL;
                sevText = "SECURE";
                break;
        }

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JLabel badge = new JLabel(sevText);
        badge.setFont(Typography.TINY);
        badge.setForeground(sevColor);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(sevColor, 1),
            new EmptyBorder(2, 6, 2, 6)
        ));
        left.add(badge);

        JPanel textCol = new JPanel(new GridLayout(2, 1, 0, 2));
        textCol.setOpaque(false);

        JLabel title = new JLabel(issue.getTitle());
        title.setFont(Typography.BODY_BOLD);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel desc = new JLabel(issue.getDescription());
        desc.setFont(Typography.METADATA);
        desc.setForeground(AppColors.TEXT_SECONDARY);

        textCol.add(title);
        textCol.add(desc);
        left.add(textCol);
        row.add(left, BorderLayout.CENTER);

        // Fix button
        if (issue.getCredential() != null) {
            CustomButton fixBtn = new CustomButton("Update Account", CustomButton.Variant.SECONDARY);
            fixBtn.setPreferredSize(new Dimension(130, 32));
            fixBtn.setFont(Typography.METADATA);
            fixBtn.addActionListener(e -> {
                Window win = SwingUtilities.getWindowAncestor(this);
                CredentialDialog dlg = new CredentialDialog(win, issue.getCredential());
                dlg.setVisible(true);
            });
            row.add(fixBtn, BorderLayout.EAST);
        }

        return row;
    }
}
