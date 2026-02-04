/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.aesh:terminal-tty:3.0-dev

package dev.snowdrop;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Project able to calculate the RGB codes using HSV values
 * <p>
 * Code inspired from:
 * - ColorUtil: https://github.com/jboss-logging/jboss-logmanager/blob/main/src/main/java/org/jboss/logmanager/formatters/ColorUtil.java#L37-L49
 * - ColorPatternFormatter: https://github.com/jboss-logging/jboss-logmanager/blob/main/src/main/java/org/jboss/logmanager/formatters/ColorPatternFormatter.java#L187-L195
 * <p>
 *  The LEVEL value (400 -> 1100) is based on HSV: https://en.wikipedia.org/wiki/HSL_and_HSV and represents the dimensional value
 * <p>
 *  JDK proposes formula to convert HSB values to RGB: https://github.com/openjdk/jdk/blob/master/src/java.desktop/share/classes/java/awt/Color.java#L821
 */
public class JBossLoggingApp {
    static int MODE = 39;

    static int FATAL_LEVEL = 1100;
    static int ERROR_LEVEL = 1000;
    static int WARN_LEVEL = 900;
    static int INFO_LEVEL = 800;
    static int DEBUG_LEVEL = 500;
    static int TRACE_LEVEL = 400;

    static int LARGEST_LEVEL = ERROR_LEVEL;
    static int SMALLEST_LEVEL = TRACE_LEVEL;

    /**
     *  This saturation value of 66 is a good compromise.
     *  If we increase it to 100, 200 then we can better see the TRACE level on a dark terminal but the colors are becoming insipid
     *  If we decrease the values, then the colors appears more shiny but that don't relly help us to better show the TRACE
     */
    static int SATURATION = 0;

    static int darken;
    static int r;
    static int g;
    static int b;

    public static void main(String[] args) {
        try {
            // Don't use darken values > 0 as the result is really dark ...
            for (int num: List.of(0)) {
                darken = num;

                System.out.println("\n***********************************************************************************");
                System.out.printf("Original jboss RGB calculation using darken: %d\n", darken);
                System.out.println("*************************************************************************************");
                printUsingJBossHSVtoRGBFormula();

                System.out.println("\n***********************************************************************************");
                System.out.printf("New RGB calculation for: Light Theme using darken: %d\n", darken);
                System.out.println("*************************************************************************************");
                printUsingNewHSVtoRGBFormula(false);

                System.out.println("\n***********************************************************************************");
                System.out.printf("New RGB calculation for: Dark Theme using darken: %d\n", darken);
                System.out.println("*************************************************************************************");
                printUsingNewHSVtoRGBFormula(true);

                System.out.println("\n***********************************************************************************");
                System.out.printf("Alternative RGB calculation for: Light Theme using darken: %d\n", darken);
                System.out.println("*************************************************************************************");
                printHSVtoRGBwithFixedBrightColors(false);

                System.out.println("\n***********************************************************************************");
                System.out.printf("Alternative RGB calculation for: Dark Theme using darken: %d\n", darken);
                System.out.println("*************************************************************************************");
                printHSVtoRGBwithFixedBrightColors(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printUsingJBossHSVtoRGBFormula() throws NoSuchFieldException, IllegalAccessException {
        for (String LEVEL: Arrays.asList("TRACE","DEBUG","INFO","WARN","ERROR", "FATAL")) {
            StringBuilder target = new StringBuilder();

            jbossLoggingHSVtoRGBFormula((int) JBossLoggingApp.class.getDeclaredField(LEVEL + "_LEVEL").get(null));

            startColor(target, 38, true, r,g,b);
            target.append(LEVEL);
            target.append(String.format(" - RGB: (%d, %d, %d)", r, g, b));
            endColor(target, MODE);

            startColor(target, 38, true, 176, 208, 176);
            target.append(" log message.");
            endColor(target, MODE);

            System.out.printf("%s\n", target);
        }
    }

    static void printUsingNewHSVtoRGBFormula(boolean darkTheme) throws NoSuchFieldException, IllegalAccessException {
        for (String LEVEL: Arrays.asList("TRACE","DEBUG","INFO","WARN","ERROR", "FATAL")) {
            StringBuilder target = new StringBuilder();

            newHSVtoRGBFormula((int) JBossLoggingApp.class.getDeclaredField(LEVEL + "_LEVEL").get(null), darkTheme);

            startColor(target, 38, true, r,g,b);
            target.append(LEVEL);
            target.append(String.format(" - RGB: (%d, %d, %d)", r, g, b));
            endColor(target, MODE);

            startColor(target, 38, true, 176, 208, 176);
            target.append(" log message.");
            endColor(target, MODE);

            System.out.printf("%s\n", target);
        }
    }

    static void printHSVtoRGBwithFixedBrightColors(boolean darkTheme) throws NoSuchFieldException, IllegalAccessException {
        for (String LEVEL: Arrays.asList("TRACE","DEBUG","INFO","WARN","ERROR", "FATAL")) {
            StringBuilder target = new StringBuilder();

            hsvToRGBwithFixedBrightColors((int) JBossLoggingApp.class.getDeclaredField(LEVEL + "_LEVEL").get(null), darkTheme);

            startColor(target, 38, true, r,g,b);
            target.append(LEVEL);
            target.append(String.format(" - RGB: (%d, %d, %d)", r, g, b));
            endColor(target, MODE);

            startColor(target, 38, true, 176, 208, 176);
            target.append(" log message.");
            endColor(target, MODE);

            System.out.printf("%s\n", target);
        }
    }

    static StringBuilder startColor(StringBuilder target, int mode, boolean trueColor, int r, int g, int b) {
        return target.appendCodePoint(27).append('[').append(mode).append(';').append(2).append(';').append(clip(r))
            .append(';').append(clip(g)).append(';').append(clip(b)).append('m');
    }

    static StringBuilder endColor(StringBuilder target, int mode) {
        return target.appendCodePoint(27).append('[').append(mode).append('m');
    }

    static int clip(int color) {
        return Math.min(Math.max(0, color), 255);
    }

    static void jbossLoggingHSVtoRGBFormula(int LEVEL) {
        final int level = Math.max(Math.min(LEVEL, LARGEST_LEVEL), SMALLEST_LEVEL) - SMALLEST_LEVEL;
        r = ((level < 300 ? 0 : (level - 300) * (255 - SATURATION) / 300) + SATURATION) >>> darken;
        g = ((300 - abs(level - 300)) * (255 - SATURATION) / 300 + SATURATION) >>> darken;
        b = ((level > 300 ? 0 : level * (255 - SATURATION) / 300) + SATURATION) >>> darken;
    }

    /**
     * Enhanced RGB calculation method that provides better visibility for dark themes.
     * This method ensures TRACE level has sufficient brightness while maintaining
     * good color differentiation across all log levels.
     * <p>
     * Key improvements:
     * - Minimum brightness threshold to ensure visibility on dark backgrounds
     * - Better color distribution across the spectrum
     * - Optional dark theme mode with inverted brightness scaling
     */
    static void newHSVtoRGBFormula(int LEVEL, boolean darkTheme) {
        final int level = Math.max(Math.min(LEVEL, LARGEST_LEVEL), SMALLEST_LEVEL) - SMALLEST_LEVEL;

        // Base brightness - higher minimum for dark themes
        int minBrightness = darkTheme ? 120 : 60;
        int maxBrightness = darkTheme ? 255 : 200;

        // Calculate hue based on level (0-600 range mapped to color spectrum)
        float hue = level / 600.0f;

        // For dark themes, boost saturation and brightness for lower levels
        float saturation = darkTheme ? 0.8f : 0.7f;
        float brightness;

        if (darkTheme) {
            // In dark mode, make TRACE brighter than other levels for visibility
            brightness = level <= 100 ? 0.9f : 0.6f + (1.0f - hue) * 0.3f;
        } else {
            // In light mode, use traditional scaling
            brightness = 0.4f + hue * 0.5f;
        }

        // Convert HSB to RGB
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);

        r = Math.min(Math.max((rgb >> 16) & 0xFF, minBrightness), maxBrightness) >>> darken;
        g = Math.min(Math.max((rgb >> 8) & 0xFF, minBrightness), maxBrightness) >>> darken;
        b = Math.min(Math.max(rgb & 0xFF, minBrightness), maxBrightness) >>> darken;
    }

    /**
     * Alternative RGB calculation with fixed bright colors for dark themes.
     * Uses predefined color palettes optimized for terminal visibility.
     */
    static void hsvToRGBwithFixedBrightColors(int LEVEL, boolean darkTheme) {
        // Predefined color palettes for better terminal visibility
        int[][] lightThemeColors = {
            {128, 0, 128},   // TRACE - Purple
            {0, 128, 255},   // DEBUG - Blue
            {0, 128, 0},     // INFO - Green
            {255, 165, 0},   // WARN - Orange
            {255, 0, 0},     // ERROR - Red
            {128, 0, 0}      // FATAL - Dark Red
        };

        int[][] darkThemeColors = {
            {200, 150, 255}, // TRACE - Bright Purple
            {100, 200, 255}, // DEBUG - Bright Blue
            {150, 255, 150}, // INFO - Bright Green
            {255, 200, 100}, // WARN - Bright Orange
            {255, 100, 100}, // ERROR - Bright Red
            {255, 50, 50}    // FATAL - Bright Red
        };

        int colorIndex = switch (LEVEL) {
            case 400 -> 0;  // TRACE
            case 500 -> 1;  // DEBUG
            case 800 -> 2;  // INFO
            case 900 -> 3;  // WARN
            case 1000 -> 4; // ERROR
            case 1100 -> 5; // FATAL
            default -> 0;
        };

        int[][] palette = darkTheme ? darkThemeColors : lightThemeColors;
        r = palette[colorIndex][0] >>> darken;
        g = palette[colorIndex][1] >>> darken;
        b = palette[colorIndex][2] >>> darken;
    }
}
