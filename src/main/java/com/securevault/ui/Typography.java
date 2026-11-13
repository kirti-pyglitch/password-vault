package com.securevault.ui;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Editorial typography system for SecureVault.
 * Resolves high-grade system fonts (SF Pro, Inter, Segoe UI) with clean fallbacks.
 */
public final class Typography {
    private Typography() {}

    private static final String FONT_FAMILY;
    private static final String MONO_FAMILY;

    static {
        Set<String> available = new HashSet<>(
            Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
        );

        String chosenFamily = "SansSerif";
        String[] preferred = {
            "SF Pro Display",
            "SF Pro Text",
            "Inter",
            ".AppleSystemUIFont",
            "Segoe UI",
            "Helvetica Neue",
            "Helvetica"
        };
        for (String f : preferred) {
            if (available.contains(f)) {
                chosenFamily = f;
                break;
            }
        }
        FONT_FAMILY = chosenFamily;

        String chosenMono = "Monospaced";
        String[] preferredMono = {
            "SF Mono",
            "JetBrains Mono",
            "Fira Code",
            "Consolas",
            "Menlo",
            "Courier New"
        };
        for (String m : preferredMono) {
            if (available.contains(m)) {
                chosenMono = m;
                break;
            }
        }
        MONO_FAMILY = chosenMono;
    }

    public static String getFamily() {
        return FONT_FAMILY;
    }

    public static String getMonoFamily() {
        return MONO_FAMILY;
    }

    // Display & Headings
    public static final Font DISPLAY_LARGE = new Font(FONT_FAMILY, Font.BOLD, 28);
    public static final Font DISPLAY_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font SECTION_TITLE = new Font(FONT_FAMILY, Font.BOLD, 17);
    public static final Font CARD_TITLE    = new Font(FONT_FAMILY, Font.BOLD, 14);

    // Body
    public static final Font BODY_LARGE    = new Font(FONT_FAMILY, Font.PLAIN, 15);
    public static final Font BODY_MEDIUM   = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font BODY_BOLD     = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font BODY_SMALL    = new Font(FONT_FAMILY, Font.PLAIN, 12);

    // Metadata & Labels
    public static final Font LABEL_BOLD    = new Font(FONT_FAMILY, Font.BOLD, 11);
    public static final Font METADATA      = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font TINY          = new Font(FONT_FAMILY, Font.PLAIN, 10);

    // Monospaced (Passwords & Keys)
    public static final Font MONO_LARGE    = new Font(MONO_FAMILY, Font.BOLD, 19);
    public static final Font MONO_MEDIUM   = new Font(MONO_FAMILY, Font.PLAIN, 14);
    public static final Font MONO_SMALL    = new Font(MONO_FAMILY, Font.PLAIN, 12);

    public static Font regular(float size) {
        return new Font(FONT_FAMILY, Font.PLAIN, Math.round(size));
    }

    public static Font bold(float size) {
        return new Font(FONT_FAMILY, Font.BOLD, Math.round(size));
    }

    public static Font mono(float size, boolean bold) {
        return new Font(MONO_FAMILY, bold ? Font.BOLD : Font.PLAIN, Math.round(size));
    }
}

