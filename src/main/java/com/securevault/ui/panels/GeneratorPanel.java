package com.securevault.ui.panels;

import com.securevault.crypto.VaultEncryption;
import com.securevault.service.VaultService;
import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CustomButton;
import com.securevault.ui.components.CustomToggle;
import com.securevault.ui.components.HorizontalScoreIndicator;
import com.securevault.ui.components.Icons;
import com.securevault.ui.components.ScrollablePanel;
import com.securevault.ui.components.ToastNotification;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Dedicated Password Generator panel.
 * Visual focus centered on a Jet Black (#111111) display panel with Crimson Red copy action.
 */
public class GeneratorPanel extends JPanel {
    private final VaultService vaultService = VaultService.getInstance();

    private final JLabel passwordDisplay = new JLabel();
    private final JLabel lengthValueBadge = new JLabel("20");
    private final JSlider lengthSlider = new JSlider(8, 64, 20);

    private final CustomToggle upperToggle = new CustomToggle("Uppercase letters (A-Z)", true);
    private final CustomToggle lowerToggle = new CustomToggle("Lowercase letters (a-z)", true);
    private final CustomToggle digitsToggle = new CustomToggle("Numbers (0-9)", true);
    private final CustomToggle symbolsToggle = new CustomToggle("Special characters (!@#$%^&*)", true);
    private final CustomToggle ambiguousToggle = new CustomToggle("Exclude ambiguous characters (1, l, I, 0, O)", true);

    private final HorizontalScoreIndicator strengthIndicator = new HorizontalScoreIndicator();
    private final JLabel entropyDetailsLabel = new JLabel();

    private String currentPassword = "";

    public GeneratorPanel() {
        setLayout(new BorderLayout());
        setBackground(AppColors.WARM_OFF_WHITE);

        initUI();
        regenerate();
    }

    private void initUI() {
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppColors.WARM_OFF_WHITE);
        content.setBorder(new EmptyBorder(36, 44, 40, 44));

        // 1. Header
        JPanel headerRow = new JPanel(new GridLayout(2, 1, 0, 4));
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel title = new JLabel("Password Generator");
        title.setFont(Typography.DISPLAY_TITLE);
        title.setForeground(AppColors.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Generate cryptographically secure passphrases with customized character entropy.");
        subtitle.setFont(Typography.BODY_MEDIUM);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);

        headerRow.add(title);
        headerRow.add(subtitle);
        headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(headerRow);
        content.add(Box.createVerticalStrut(28));

        // 2. Visual Focus: Jet Black Display Panel
        JPanel displayPanel = createPasswordDisplayPanel();
        displayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        displayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        content.add(displayPanel);
        content.add(Box.createVerticalStrut(16));

        // 3. Strength & Entropy Bar
        JPanel strengthPanel = new JPanel(new BorderLayout(16, 0));
        strengthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        strengthPanel.setOpaque(false);
        strengthPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        strengthIndicator.setPreferredSize(new Dimension(300, 32));
        strengthPanel.add(strengthIndicator, BorderLayout.WEST);

        entropyDetailsLabel.setFont(Typography.METADATA);
        entropyDetailsLabel.setForeground(AppColors.TEXT_MUTED);
        entropyDetailsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        strengthPanel.add(entropyDetailsLabel, BorderLayout.CENTER);

        content.add(strengthPanel);
        content.add(Box.createVerticalStrut(28));

        // 4. Configuration Card
        JPanel configCard = createConfigCard();
        configCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        configCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        content.add(configCard);

        JScrollPane scrollPane = new JScrollPane(content);
        ThemeManager.styleScrollBar(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createPasswordDisplayPanel() {
        JPanel p = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                ThemeManager.applyQualityRendering(g2);
                g2.setColor(AppColors.JET_BLACK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(22, 28, 22, 24));

        passwordDisplay.setFont(Typography.MONO_LARGE);
        passwordDisplay.setForeground(AppColors.PURE_WHITE);
        p.add(passwordDisplay, BorderLayout.CENTER);

        // Actions: Regenerate & Copy
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton refreshBtn = new CustomButton("", CustomButton.Variant.BLACK);
        refreshBtn.setIcon(Icons.getIcon("refresh", 16, AppColors.PURE_WHITE));
        refreshBtn.setPreferredSize(new Dimension(38, 38));
        refreshBtn.setToolTipText("Regenerate Password");
        refreshBtn.addActionListener(e -> regenerate());
        actions.add(refreshBtn);

        CustomButton copyBtn = new CustomButton("Copy", CustomButton.Variant.PRIMARY);
        copyBtn.setPreferredSize(new Dimension(90, 38));
        copyBtn.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(currentPassword), null);
            ToastNotification.show(this, "Password copied to clipboard", true);
        });
        actions.add(copyBtn);

        p.add(actions, BorderLayout.EAST);
        return p;
    }

    private JPanel createConfigCard() {
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 28, 28, 28));

        // Slider Header: Label + Value Badge
        JPanel sliderHeader = new JPanel(new BorderLayout());
        sliderHeader.setOpaque(false);
        sliderHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        JLabel sliderTitle = new JLabel("CHARACTER LENGTH");
        sliderTitle.setFont(Typography.LABEL_BOLD);
        sliderTitle.setForeground(AppColors.TEXT_MUTED);
        sliderHeader.add(sliderTitle, BorderLayout.WEST);

        lengthValueBadge.setFont(Typography.BODY_BOLD);
        lengthValueBadge.setForeground(AppColors.CRIMSON_RED);
        sliderHeader.add(lengthValueBadge, BorderLayout.EAST);

        card.add(sliderHeader);
        card.add(Box.createVerticalStrut(10));

        // Slider Component
        lengthSlider.setOpaque(false);
        lengthSlider.setPaintTicks(false);
        lengthSlider.setPaintLabels(false);
        lengthSlider.addChangeListener(e -> {
            int val = lengthSlider.getValue();
            lengthValueBadge.setText(String.valueOf(val));
            regenerate();
        });
        card.add(lengthSlider);
        card.add(Box.createVerticalStrut(24));

        // Divider
        JPanel div = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(AppColors.LIGHT_GREY);
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(div);
        card.add(Box.createVerticalStrut(20));

        // Toggles Section
        JLabel optionsTitle = new JLabel("CHARACTER SET SELECTION");
        optionsTitle.setFont(Typography.LABEL_BOLD);
        optionsTitle.setForeground(AppColors.TEXT_MUTED);
        card.add(optionsTitle);
        card.add(Box.createVerticalStrut(14));

        // Toggles Grid
        JPanel togglesGrid = new JPanel(new GridLayout(3, 2, 24, 16));
        togglesGrid.setOpaque(false);

        upperToggle.addChangeListener(this::regenerate);
        lowerToggle.addChangeListener(this::regenerate);
        digitsToggle.addChangeListener(this::regenerate);
        symbolsToggle.addChangeListener(this::regenerate);
        ambiguousToggle.addChangeListener(this::regenerate);

        togglesGrid.add(upperToggle);
        togglesGrid.add(lowerToggle);
        togglesGrid.add(digitsToggle);
        togglesGrid.add(symbolsToggle);
        togglesGrid.add(ambiguousToggle);

        card.add(togglesGrid);
        return card;
    }

    private void regenerate() {
        int length = lengthSlider.getValue();
        boolean upper = upperToggle.isSelected();
        boolean lower = lowerToggle.isSelected();
        boolean digits = digitsToggle.isSelected();
        boolean symbols = symbolsToggle.isSelected();
        boolean avoidAmbiguous = ambiguousToggle.isSelected();

        // Ensure at least one set is active
        if (!upper && !lower && !digits && !symbols) {
            lowerToggle.setSelected(true);
            lower = true;
        }

        currentPassword = vaultService.generatePassword(length, upper, lower, digits, symbols, avoidAmbiguous);
        passwordDisplay.setText(currentPassword);

        int strength = VaultEncryption.calculatePasswordStrength(currentPassword);
        strengthIndicator.setScore(strength, "Entropy Rating");

        // Calculate bits of entropy: length * log2(poolSize)
        int poolSize = 0;
        if (upper) poolSize += 26;
        if (lower) poolSize += 26;
        if (digits) poolSize += 10;
        if (symbols) poolSize += 28;
        if (poolSize == 0) poolSize = 26;

        double entropyBits = length * (Math.log(poolSize) / Math.log(2));
        entropyDetailsLabel.setText(String.format("%.1f bits of cryptographic entropy", entropyBits));
    }
}
