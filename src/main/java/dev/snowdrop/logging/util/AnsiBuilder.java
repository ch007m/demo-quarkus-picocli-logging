package dev.snowdrop.logging.util;

public class AnsiBuilder {
    private final StringBuilder sb = new StringBuilder();

    public AnsiBuilder add(Object text, String colorName) {
        sb.append(Ansi.color(colorName).format(String.valueOf(text))).append(" ");
        return this;
    }

    public String build() {
        return sb.toString();
    }

}