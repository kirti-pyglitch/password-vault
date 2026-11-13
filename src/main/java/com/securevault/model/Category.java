package com.securevault.model;

/**
 * Standard credential categories.
 */
public enum Category {
    ALL("All Items"),
    LOGINS("Logins"),
    BANKING("Finance & Banking"),
    CLOUD("Cloud & Infrastructure"),
    DEVELOPMENT("Developer Keys"),
    PERSONAL("Personal");

    private final String display;

    Category(String display) {
        this.display = display;
    }

    public String getDisplayName() {
        return display;
    }

    public static Category fromString(String name) {
        if (name == null) return LOGINS;
        for (Category c : values()) {
            if (c.name().equalsIgnoreCase(name) || c.display.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return LOGINS;
    }
}

