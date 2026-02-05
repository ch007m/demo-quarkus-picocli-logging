/// usr/bin/env jbang "$0" "$@"; exit $?
package dev.snowdrop;

import java.awt.*;

/**
 * See the color.org tool to play with HSB or RGB colors: https://colorizer.org/
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

        // Ansi terminal reset code
        String reset = "\u001B[0m";

        // Calculate the color code for the level according to its Hue value and Theme
        String loggingLevelColorCode = levelColorCode(hueValue, theme);

        String messageColorCode = messageColorCode(theme);

        // Log the message
        System.out.printf("%s%-6s%s %s%s%s%n",
            loggingLevelColorCode, level, reset,
            messageColorCode, logMsg, reset);
    }

    static String levelColorCode(int hueValue, Theme theme) {
        Color c = calculateColor(hueValue, theme);
        return String.format("\u001B[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());
    }

    static String messageColorCode(Theme theme) {
        Color c;
        if (theme == Theme.DARK) {
            c = new Color(Color.HSBtoRGB(140f, 0.10f, 0.80f));
        } else {
            c = new Color(Color.HSBtoRGB(140f, 0.10f, 0.40f));
        }
        return String.format("\u001B[38;2;%d;%d;%dm", c.getRed(), c.getGreen(), c.getBlue());
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

    static class HSB {
        private float hue;
        private float saturation;
        private float brightness;

        private HSB() {
            // Default constructor for fluent API
        }

        public static HSB withHue(float h) {
            HSB hsb = new HSB();
            hsb.hue = h;
            return hsb;
        }

        public HSB withSaturation(float s) {
            this.saturation = s;
            return this;
        }

        public HSB withBrightness(float b) {
            this.brightness = b;
            return this;
        }

        public float getHue() {
            return hue;
        }

        public float getSaturation() {
            return saturation;
        }

        public float getBrightness() {
            return brightness;
        }
    }
}