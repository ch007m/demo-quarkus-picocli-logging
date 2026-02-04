/// usr/bin/env jbang "$0" "$@"; exit $?
package dev.snowdrop;

import java.awt.*;

public class ColorTool {

    enum Theme {DARK, LIGHT}

    public static void main(String[] args) {
        boolean isDark = true;

        if (args.length > 0) {
            isDark = Boolean.parseBoolean(args[0]);
        }

        Theme selectedTheme = isDark ? Theme.DARK : Theme.LIGHT;
        renderColors(selectedTheme);
    }

    static void renderColors(Theme theme) {
        System.out.println("****************************************************");
        System.out.println("Selected :" + theme + " Theme");
        System.out.println("****************************************************");

        calculateVibrant("FATAL", 0, theme);
        calculateVibrant("ERROR", 30, theme);
        calculateVibrant("WARN ", 80, theme);
        calculateVibrant("INFO ", 150, theme);
        calculateVibrant("DEBUG", 200, theme);
        calculateVibrant("TRACE", 250, theme);
        System.out.println();
    }

    static void calculateVibrant(String level, int priority, Theme theme) {
        float h, s, v;

        // 1. Conventional Hues
        if (priority <= 30) h = 0f;         // Red (Fatal/Error)
        else if (priority <= 80) h = 35f;   // Orange (Warn)
        else if (priority <= 150) h = 120f;  // Green (Info)
        else if (priority <= 200) h = 190f;  // Cyan (Debug)
        else h = 210f;  // Slate (Trace)

        if (theme == Theme.DARK) {
            // Dark Theme: Maximum visibility
            s = (priority > 200) ? 0.30f : 0.85f;
            v = 1.0f;
        } else {
            // LIGHT THEME: High Value (V) for 'Shiny' effect
            // Adjusted saturation to keep colors from looking 'muddy'
            s = (priority > 200) ? 0.60f : 0.95f;
            v = (priority <= 30) ? 0.90f : 0.80f;
            if (priority == 200) v = 0.95f; // Cyan pop
            if (priority == 150) v = 0.75f; // Green depth
        }

        int rgb = Color.HSBtoRGB(h / 360f, s, v);
        Color c = new Color(rgb);

        String startColor = String.format("\u001B[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());
        String reset = "\u001B[0m";

        System.out.printf("[%3d] %s%-6s%s - RGB: (%3d, %3d, %3d) H:%.0f S:%.0f%% V:%.0f%%%n",
            priority, startColor, level, reset, c.getRed(), c.getGreen(), c.getBlue(), h, s * 100, v * 100);
    }
}