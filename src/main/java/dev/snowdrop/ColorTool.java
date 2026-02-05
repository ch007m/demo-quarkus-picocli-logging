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
        render("ERROR", 10, theme);
        render("WARN ", 35, theme);
        render("INFO ", 100, theme);
        render("DEBUG", 180, theme);
        render("TRACE", 220, theme);
        System.out.println();
    }

    static void render(String level, int hueValue, Theme theme) {
        float h, s, v;
        var logMsg = "This is a log message. The application has well started and is running on port: 8080.";
        h = hueValue;

        if (theme == Theme.DARK) {
            s = 0.75f; // If we decrease the saturation, then the colors become less vivid
            v = 1.0f;
        } else {
            s = (hueValue > 200) ? 0.60f : 0.75f;
            v = 0.85f;
        }

        int rgb = Color.HSBtoRGB(h / 360f, s, v);
        Color c = new Color(rgb);

        // Dark Theme: Muted Sage (174, 217, 185)
        // Light Theme: Deep Slate Green (60, 90, 70) for contrast
        String msgColor = (theme == Theme.DARK)
            ? "\u001B[38;2;174;217;185m"
            : "\u001B[38;2;179;201;187m";

        String startColor = String.format("\u001B[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());
        String reset = "\u001B[0m";

        System.out.printf("[%3d] %s%-6s%s - RGB: (%3d, %3d, %3d) - H:%.0f S:%.0f%% V:%.0f%% - %s%s%s%n",
            hueValue, startColor, level, reset, c.getRed(), c.getGreen(), c.getBlue(), h, s * 100, v * 100, msgColor, logMsg, reset);
    }
}