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
        render("WARN ", 40, theme);
        render("INFO ", 100, theme);
        render("DEBUG", 180, theme);
        render("TRACE", 220, theme);
        System.out.println();
    }

    static void render(String level, int hueValue, Theme theme) {
        var logMsg = "This is a log message. The application has well started and is running on port: 8080.";

        Color c = calculateColor(hueValue, theme);

        String startColor = String.format("\u001B[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());
        String reset = "\u001B[0m";

        float h, s, v;
        h = 140f;
        s = 0.10f;
        v = 0.58f;
        int rgb = Color.HSBtoRGB(h, s, v);
        c = new Color(rgb);
        String msgColor = String.format("\u001B[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());;

        System.out.printf("%s%-6s%s %s%s%s%n",
            startColor, level, reset, msgColor, logMsg, reset);
    }

    static Color calculateColor(int h, Theme theme) {
        float s, v;
        if (theme == Theme.DARK) {
            s = 0.75f; // If we decrease the saturation, then the colors become less vivid
            v = 1.0f;
        } else {
            s = (h > 200) ? 0.60f : 0.75f;
            v = 0.70f;
        }

        int rgb = Color.HSBtoRGB(h / 360f, s, v);
        Color c = new Color(rgb);
        //System.out.printf("RGB: (%3d, %3d, %3d) - H:%.0f S:%.0f%% V:%.0f%%\n",
        //    c.getRed(), c.getGreen(), c.getBlue(), (float)h, s * 100, v * 100);
        return c;
    }
}