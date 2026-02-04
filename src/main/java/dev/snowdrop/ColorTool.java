/// usr/bin/env jbang "$0" "$@"; exit $?
package dev.snowdrop;

import java.awt.*;

/**
 * See the color.org tool get the HSB or RGB or ... colors: https://colorizer.org/
 */
public class ColorTool {

    enum Theme {DARK, LIGHT}

    public static void main(String[] args) {
        boolean isDark = true;

        if (args.length > 0) {
            isDark = Boolean.parseBoolean(args[0]);
        }

        Theme selectedTheme = isDark ? Theme.DARK : Theme.LIGHT;
        printMessageWithLevels(selectedTheme);
    }

    static void printMessageWithLevels(Theme theme) {
        System.out.println("****************************************************");
        System.out.println(theme + " Theme");
        System.out.println("****************************************************");

        render("FATAL", 0, theme);
        render("ERROR", 30, theme);
        render("WARN ", 80, theme);
        render("INFO ", 150, theme);
        render("DEBUG", 200, theme);
        render("TRACE", 250, theme);
        System.out.println();
    }

    static void render(String level, int hueValue, Theme theme) {
        float h, s, v;

        // 1. Conventional Hues
        if (hueValue <= 30) h = 0f;          // Red (Fatal/Error)
        else if (hueValue <= 80) h = 35f;    // Orange (Warn)
        else if (hueValue <= 150) h = 120f;  // Green (Info)
        else if (hueValue <= 200) h = 190f;  // Cyan (Debug)
        else h = 210f;                       // Slate (Trace)

        if (theme == Theme.DARK) {
            // Dark Theme: Maximum visibility
            s = (hueValue > 200) ? 0.30f : 0.85f;
            v = 1.0f;
        } else {
            // LIGHT THEME: High Value (V) for 'Shiny' effect
            // Adjusted saturation to keep colors from looking 'muddy'
            s = (hueValue > 200) ? 0.60f : 0.95f;
            v = (hueValue <= 30) ? 0.90f : 0.80f;
            if (hueValue == 200) v = 0.95f; // Cyan pop
            if (hueValue == 150) v = 0.75f; // Green depth
        }

        int rgb = Color.HSBtoRGB(h / 360f, s, v);
        Color c = new Color(rgb);

        String startColor = String.format("\u001B[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());
        String reset = "\u001B[0m";

        System.out.printf("[%3d] %s%-6s%s - RGB: (%3d, %3d, %3d) - H:%.0f S:%.0f%% V:%.0f%%%n",
            hueValue, startColor, level, reset, c.getRed(), c.getGreen(), c.getBlue(), h, s * 100, v * 100);
    }
}