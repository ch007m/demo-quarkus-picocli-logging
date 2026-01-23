package dev.snowdrop.logging.util;

import java.util.Arrays;

public enum Ansi {
    // Color code from:
    // http://www.topmudsites.com/forums/mud-coding/413-java-ansi.html
    // https://en.wikipedia.org/wiki/ANSI_escape_code#Colors

    // Foreground colors
    BLACK("[30m"),
    BLUE("[34m"),
    BRIGHTWHITE("[97m"),
    CYAN("[36m"),
    FAINT("[2m"),
    GREEN("[32m"),
    GREENLIGHT("[92m"),
    LIGHTGREY("[90m"),
    MAGENTA("[35m"),
    RED("[31m"),
    WHITE("[37m"),
    WHITEDIM("[37;2m"),
    YELLOW("[33m"),

    // Background colors
    BG_BLACK("[40m"),
    BG_RED("[41m"),
    BG_GREEN("[42m"),
    BG_YELLOW("[43m"),
    BG_BLUE("[44m"),
    BG_MAGENTA("[45m"),
    BG_CYAN("[46m"),
    BG_WHITE("[47m"),
    BG_BRIGHTBLACK("[100m"),
    BG_BRIGHTRED("[101m"),
    BG_BRIGHTGREEN("[102m"),
    BG_BRIGHTYELLOW("[103m"),
    BG_BRIGHTBLUE("[104m"),
    BG_BRIGHTMAGENTA("[105m"),
    BG_BRIGHTCYAN("[106m"),
    BG_BRIGHTWHITE("[107m"),

    RESET("[0m");

    private final String code;
    private static final String ESC_CHAR = "\u001b";

    /**
     * Ansi contructor
     *
     * @param code - the color selected to colorize the string
     */
    Ansi(String code) {
        this.code = code;
    }

    /**
     * Part represents a substring of something to colorize on the console such as: dat time, level, message
     *
     * @param text - the part of the text, string
     * @param color - the color
     */
    public record Part(String text, Ansi color) {}

    /**
     * The different parts of a message
     *
     * @param parts - the substring or parts of the message
     * @return the String colorized and formated
     */
    public static String message(Part... parts) {
        StringBuilder sb = new StringBuilder();
        for (Part part : parts) {
            sb.append(part.color.format(part.text)).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Colorize the string using the name of the color
     * @param name - the name of the color: RED, GREEN etc
     * @return the Ansi object with the proper ANSI code
     */
    public static Ansi color(String name) {
        return Arrays.stream(values())
            .filter(c -> c.name().equalsIgnoreCase(name))
            .findFirst()
            .orElse(RESET); // Fallback to Reset if color not found
    }

    /**
     * Encapsulate the string template formated with the selected color
     * @param pattern - the string template to format the txt
     * @param args - the arguments to be used by the formater
     * @return the string formated and colorized
     */
    public String format(String pattern, Object... args) {
        String message = String.format(pattern, args);
        return ESC_CHAR + this.code + message + ESC_CHAR + RESET.code;
    }
}
