package com.clothstore.product.entity;

public enum ProductColor {
    R("Red"),
    G("Green"),
    B("Blue"),
    Y("Yellow");

    private final String displayName;

    ProductColor(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
