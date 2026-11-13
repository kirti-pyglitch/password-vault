package com.securevault.ui;

import java.awt.Color;

/**
 * Design token definitions for SecureVault.
 * Strict color hierarchy based on human-designed cybersecurity desktop identity.
 */
public final class AppColors {
    private AppColors() {}

    // Primary Colors
    public static final Color JET_BLACK       = new Color(0x11, 0x11, 0x11); // #111111
    public static final Color PURE_WHITE      = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF
    public static final Color CRIMSON_RED     = new Color(0xD9, 0x36, 0x36); // #D93636
    public static final Color WARM_OFF_WHITE  = new Color(0xF3, 0xF1, 0xED); // #F3F1ED
    public static final Color SLATE_GREY      = new Color(0x77, 0x77, 0x77); // #777777
    public static final Color LIGHT_GREY      = new Color(0xE8, 0xE7, 0xE4); // #E8E7E4

    // Supporting Colors
    public static final Color MUTED_TEAL      = new Color(0x27, 0x7C, 0x70); // #277C70 (Positive / Secure)
    public static final Color SOFT_AMBER      = new Color(0xD8, 0x9A, 0x35); // #D89A35 (Warning)
    public static final Color DEEP_BURGUNDY   = new Color(0x70, 0x2B, 0x35); // #702B35 (Critical Alert)

    // Interactive & State Accents
    public static final Color RED_HOVER       = new Color(0xB8, 0x28, 0x28);
    public static final Color RED_PRESSED     = new Color(0x9E, 0x20, 0x20);
    public static final Color RED_TINT        = new Color(0xD9, 0x36, 0x36, 25);

    // Sidebar & Dark Chrome
    public static final Color SIDEBAR_BG      = JET_BLACK;
    public static final Color SIDEBAR_HOVER   = new Color(0x1E, 0x1E, 0x1E);
    public static final Color SIDEBAR_ACTIVE  = new Color(0x28, 0x28, 0x28);
    public static final Color SIDEBAR_TEXT    = new Color(0xDD, 0xDD, 0xDD);
    public static final Color SIDEBAR_MUTED   = new Color(0x88, 0x88, 0x88);

    // Surface & Border Elements
    public static final Color SURFACE_CARD    = PURE_WHITE;
    public static final Color BORDER_SUBTLE   = new Color(0xE0, 0xDD, 0xD6);
    public static final Color BORDER_LIGHT    = LIGHT_GREY;
    public static final Color ROW_HOVER       = new Color(0xF8, 0xF7, 0xF5);
    public static final Color ROW_SELECTED    = new Color(0xEE, 0xEA, 0xE2);

    // Text Hierarchy
    public static final Color TEXT_PRIMARY    = JET_BLACK;
    public static final Color TEXT_SECONDARY  = new Color(0x4A, 0x4A, 0x4A);
    public static final Color TEXT_MUTED      = SLATE_GREY;
    public static final Color TEXT_WHITE      = PURE_WHITE;

    // Badges / Tags
    public static final Color BADGE_BG        = new Color(0xEB, 0xE9, 0xE4);
    public static final Color TEAL_BG         = new Color(0x27, 0x7C, 0x70, 26);
    public static final Color AMBER_BG        = new Color(0xD8, 0x9A, 0x35, 26);
    public static final Color BURGUNDY_BG     = new Color(0x70, 0x2B, 0x35, 26);
}

