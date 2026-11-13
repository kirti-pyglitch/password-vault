package com.securevault.ui.dialogs;

import com.securevault.ui.AppColors;
import com.securevault.ui.ThemeManager;
import com.securevault.ui.Typography;
import com.securevault.ui.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Editorial confirmation dialog for high-impact actions like deletion.
 */
public class ConfirmationDialog extends JDialog {
    private boolean confirmed = false;

    public ConfirmationDialog(Window owner, String title, String message, String actionButtonText) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setLayout(new BorderLayout());

        JPanel card = new JPanel(new BorderLayout(0, 16));
        card.setBackground(AppColors.PURE_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_SUBTLE, 1),
            new EmptyBorder(24, 28, 24, 28)
        ));

        // Title & Description
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Typography.SECTION_TITLE);
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);

        JLabel msgLabel = new JLabel("<html><body style='width: 320px;'>" + message + "</body></html>");
        msgLabel.setFont(Typography.BODY_MEDIUM);
        msgLabel.setForeground(AppColors.TEXT_SECONDARY);

        textPanel.add(titleLabel);
        textPanel.add(msgLabel);
        card.add(textPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        CustomButton cancelBtn = new CustomButton("Cancel", CustomButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        CustomButton actionBtn = new CustomButton(actionButtonText, CustomButton.Variant.DANGER);
        actionBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(actionBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        add(card, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public static boolean showConfirmation(Component parent, String title, String message, String actionText) {
        Window win = SwingUtilities.getWindowAncestor(parent);
        ConfirmationDialog dialog = new ConfirmationDialog(win, title, message, actionText);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}
