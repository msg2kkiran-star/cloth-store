package com.clothstore.product.entity;

public enum ProductColor {
    RED("Red"),
    GREEN("Green"),
    BLUE("Blue"),
    YELLOW("Yellow"),
    BLACK("Black"),
    WHITE("White"),
    NAVY("Navy"),
    GREY("Grey");

    private final String displayName;

    ProductColor(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
