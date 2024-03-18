package com.fiap.fourlanches.order.domain.entities;

public enum Category {
    MEAL("meal"),
    DRINK("drink"),
    EXTRAS("extras");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static Category fromString(String text) {
        for (Category c : Category.values()) {
            if (c.value.equalsIgnoreCase(text)) {
                return c;
            }
        }
        return null;
    }
}
