package dev.snowdrop.logging.util;

public class AnsiBuilder {
    private final StringBuilder sb = new StringBuilder();

    public AnsiBuilder add(Object text, String colorName) {
        sb.append(Ansi.color(colorName).format(String.valueOf(text))).append(" ");
        return this;
    }

    public AnsiBuilder add(Object text, int code) {
        String ansiCode = "[" + code + "m";
        String formattedText = "\u001b" + ansiCode + text + "\u001b[0m";
        sb.append(formattedText).append(" ");
        return this;
    }

    public StringBuilder raw() {
        return sb;
    }

    public String build() {
        return sb.toString();
    }

}