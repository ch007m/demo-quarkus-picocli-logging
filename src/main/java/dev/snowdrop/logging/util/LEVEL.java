package dev.snowdrop.logging.util;

public enum LEVEL {
    INFO("GREEN"),
    WARN("YELLOW"),
    ERROR("RED"),
    DEBUG("CYAN");

    private final String color;

    LEVEL(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
