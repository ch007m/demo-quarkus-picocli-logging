/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.aesh:terminal-tty:3.0

package dev.snowdrop.old;

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
    static final int MODE = 39;

    static final int FATAL_LEVEL = 1100;
    static final int ERROR_LEVEL = 1000;
    static final int WARN_LEVEL = 900;
    static final int INFO_LEVEL = 800;
    static final int DEBUG_LEVEL = 500;
    static final int TRACE_LEVEL = 400;
    static final int MESSAGE_LEVEL = 100;

    static final int LARGEST_LEVEL = ERROR_LEVEL;
    static final int SMALLEST_LEVEL = TRACE_LEVEL;

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
            // Parse command line arguments
            boolean darkTheme = parseDarkThemeArgument(args);
            String themeType = darkTheme ? "Dark" : "Light";

            // Don't use darken values > 0 as the result is really dark ...
            for (int num: List.of(0)) {
                darken = num;

                System.out.println("*".repeat(70));
                System.out.printf("Original jboss RGB calculation using darken: %d\n", darken);
                System.out.println("*".repeat(70));
                printUsingJBossHSVtoRGBFormula();
                System.out.println();

                System.out.println("*".repeat(70));
                System.out.printf("New RGB calculation for: %s Theme using darken: %d\n", themeType, darken);
                System.out.println("*".repeat(70));
                printUsingNewHSVtoRGFormula(darkTheme);
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses command line arguments to determine if dark theme should be used.
     *
     * @param args command line arguments
     * @return true for dark theme, false for light theme
     */
    static boolean parseDarkThemeArgument(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }

        String arg = args[0].toLowerCase();
        return switch (arg) {
            case "dark", "d" -> true;
            case "light", "l" -> false;
            case "--help", "-h", "help" -> {
                printUsageAndExit();
                yield false; // This line will never be reached
            }
            default -> {
                System.err.printf("Invalid argument: %s%n", args[0]);
                printUsageAndExit();
                yield false; // This line will never be reached
            }
        };
    }

    /**
     * Prints usage information and exits the program.
     */
    static void printUsageAndExit() {
        System.out.println("Usage: java JBossLoggingApp <theme>");
        System.out.println();
        System.out.println("Arguments:");
        System.out.println("  theme    Specify the theme type:");
        System.out.println("           - 'dark', 'd'  : Use dark theme colors");
        System.out.println("           - 'light', 'l' : Use light theme colors");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  jbang ./src/main/java/dev/snowdrop/JBossLoggingApp.java dark");
        System.out.println("  jbang ./src/main/java/dev/snowdrop/JBossLoggingApp.java light");
        System.out.println("  jbang ./src/main/java/dev/snowdrop/JBossLoggingApp.java d");
        System.out.println("  jbang ./src/main/java/dev/snowdrop/JBossLoggingApp.java l");
        System.exit(1);
    }

    static void printUsingJBossHSVtoRGBFormula() throws NoSuchFieldException, IllegalAccessException {
        for (String LEVEL: Arrays.asList("TRACE","DEBUG","INFO","WARN","ERROR", "FATAL")) {
            StringBuilder target = new StringBuilder();

            jbossLoggingHSVtoRGBFormula((int) JBossLoggingApp.class.getDeclaredField(LEVEL + "_LEVEL").get(null));

            startColor(target, 38, true, r,g,b);
            target.append(LEVEL);
            target.append(String.format(" - RGB: (%d, %d, %d)", r, g, b));
            endColor(target, MODE);

            // Use original JBoss color for consistency with original implementation
            startColor(target, 38, true, 176, 208, 176);
            target.append(" log message.");
            endColor(target, MODE);

            System.out.printf("%s\n", target);
        }
    }

    static void printUsingNewHSVtoRGFormula(boolean darkTheme) throws NoSuchFieldException, IllegalAccessException {
        for (String LEVEL: Arrays.asList("TRACE","DEBUG","INFO","WARN","ERROR", "FATAL")) {
            StringBuilder target = new StringBuilder();

            // int[] levelColor = generateLogLevelColor(darkTheme, (int) JBossLoggingApp.class.getDeclaredField(LEVEL + "_LEVEL").get(null));
            // startColor(target, 38, true, levelColor[0], levelColor[1], levelColor[2]);
            // target.append(LEVEL);
            // target.append(String.format(" - RGB: (%d, %d, %d)", levelColor[0], levelColor[1], levelColor[2]));
            // endColor(target, MODE);

            hsvToRGBwithFixedBrightColors((int) JBossLoggingApp.class.getDeclaredField(LEVEL + "_LEVEL").get(null), darkTheme);
            startColor(target, 38, true, r,g,b);

            target.append(LEVEL);
            target.append(String.format(" - RGB: (%d, %d, %d)", r,g,b));
            endColor(target, MODE);

            int[] messageColor = generateLogLevelColor(darkTheme, MESSAGE_LEVEL);
            startColor(target, 38, true, messageColor[0], messageColor[1], messageColor[2]);
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

    /**
     * Alternative method to calculate message colors using mathematical formulas
     * similar to the level color calculations instead of hard-coded RGB values.
     * @param darkTheme true for dark theme, false for light theme
     * @return array of [r, g, b] values for log message color
     */
    static int[] generateRGBColor(boolean darkTheme, int hue) {
        var intHue = Integer.valueOf(hue);
        float messageHue = intHue.floatValue() / 360.0f; // Convert to 0-1 range for HSBtoRGB

        float saturation;
        float brightness;
        int minBrightness;
        int maxBrightness;

        if (darkTheme) {
            // For dark themes, use higher saturation and brightness for better visibility
            saturation = 0.4f;      // Medium saturation to avoid overly vivid colors
            brightness = 0.85f;     // High brightness for good contrast on dark backgrounds
            minBrightness = 150;    // Ensure minimum brightness
            maxBrightness = 255;
        } else {
            // For light themes, use lower brightness and higher saturation
            saturation = 0.6f;      // Higher saturation for good visibility on light backgrounds
            brightness = 0.5f;      // Medium brightness to maintain readability
            minBrightness = 60;
            maxBrightness = 180;
        }

        // Convert HSB to RGB using Java's built-in method
        int rgb = java.awt.Color.HSBtoRGB(messageHue, saturation, brightness);

        // Extract RGB components and apply brightness constraints
        int r = Math.min(Math.max((rgb >> 16) & 0xFF, minBrightness), maxBrightness) >>> darken;
        int g = Math.min(Math.max((rgb >> 8) & 0xFF, minBrightness), maxBrightness) >>> darken;
        int b = Math.min(Math.max(rgb & 0xFF, minBrightness), maxBrightness) >>> darken;

        return new int[]{r, g, b};
    }

    static int[] generateLogLevelColor(boolean darkTheme, int logLevel) {
        // Improved gradient that produces colors similar to the appealing fixed palette
        // Based on analysis of the fixed color palette shown in the reference image

        // Define hue progression that matches the fixed palette's color sequence:
        // TRACE (purple) -> DEBUG (blue) -> INFO (green) -> WARN (orange) -> ERROR (red) -> FATAL (bright red)
        float hue;
        float saturation;
        float brightness;

        if (darkTheme) {
            // Dark theme: bright, saturated colors for visibility on dark backgrounds
            switch (logLevel) {
                case TRACE_LEVEL -> {
                    hue = 270f;        // Purple (like RGB 200,150,255)
                    saturation = 0.7f;
                    brightness = 0.95f;
                }
                case DEBUG_LEVEL -> {
                    hue = 200f;        // Blue-cyan (like RGB 100,200,255)
                    saturation = 0.8f;
                    brightness = 0.9f;
                }
                case INFO_LEVEL -> {
                    hue = 120f;        // Green (like RGB 150,255,150)
                    saturation = 0.7f;
                    brightness = 0.95f;
                }
                case WARN_LEVEL -> {
                    hue = 40f;         // Orange-yellow (like RGB 255,200,100)
                    saturation = 0.8f;
                    brightness = 0.9f;
                }
                case ERROR_LEVEL -> {
                    hue = 0f;          // Pure red (like RGB 255,0,0)
                    saturation = 0.9f;
                    brightness = 1.0f; // Maximum brightness for pure red
                }
                case FATAL_LEVEL -> {
                    hue = 300f;        // Bright magenta (more purple, less pink)
                    saturation = 1.0f; // Full saturation for vivid magenta
                    brightness = 1.0f; // Maximum brightness
                }
                case MESSAGE_LEVEL -> {
                    hue = 140f;        // Light green (like RGB 180,220,180)
                    saturation = 0.4f;
                    brightness = 0.8f;
                }
                default -> {
                    hue = 120f;
                    saturation = 0.7f;
                    brightness = 0.9f;
                }
            }
        } else {
            // Light theme: darker, more muted colors for readability on light backgrounds
            switch (logLevel) {
                case TRACE_LEVEL -> {
                    hue = 300f;        // Purple (like RGB 128,0,128)
                    saturation = 1.0f;
                    brightness = 0.5f;
                }
                case DEBUG_LEVEL -> {
                    hue = 210f;        // Blue (like RGB 0,128,255)
                    saturation = 1.0f;
                    brightness = 0.7f;
                }
                case INFO_LEVEL -> {
                    hue = 120f;        // Green (like RGB 0,128,0)
                    saturation = 1.0f;
                    brightness = 0.5f;
                }
                case WARN_LEVEL -> {
                    hue = 35f;         // Brown-orange (like RGB 192,129,66)
                    saturation = 0.8f;
                    brightness = 0.6f;
                }
                case ERROR_LEVEL -> {
                    hue = 0f;          // Red (like RGB 255,0,0)
                    saturation = 1.0f;
                    brightness = 0.7f;
                }
                case FATAL_LEVEL -> {
                    hue = 320f;        // Dark magenta for distinction from ERROR
                    saturation = 0.9f;
                    brightness = 0.5f;
                }
                case MESSAGE_LEVEL -> {
                    hue = 120f;        // Dark green (like RGB 100,120,100)
                    saturation = 0.3f;
                    brightness = 0.4f;
                }
                default -> {
                    hue = 120f;
                    saturation = 0.8f;
                    brightness = 0.6f;
                }
            }
        }

        // Convert HSB to RGB using Java's built-in method
        int rgb = java.awt.Color.HSBtoRGB(hue / 360f, saturation, brightness);

        // Define brightness constraints based on theme for fine-tuning
        int minBrightness;
        int maxBrightness;

        if (darkTheme) {
            minBrightness = 120;    // Slightly lower minimum for more color variation
            maxBrightness = 255;
        } else {
            minBrightness = 50;     // Lower minimum for better contrast on light backgrounds
            maxBrightness = 200;    // Slightly higher maximum for better visibility
        }

        // Extract RGB components and apply brightness constraints
        int r = Math.min(Math.max((rgb >> 16) & 0xFF, minBrightness), maxBrightness) >>> darken;
        int g = Math.min(Math.max((rgb >> 8) & 0xFF, minBrightness), maxBrightness) >>> darken;
        int b = Math.min(Math.max(rgb & 0xFF, minBrightness), maxBrightness) >>> darken;

        return new int[]{r, g, b};
    }

    static void jbossLoggingHSVtoRGBFormula(int LEVEL) {
        final int level = Math.max(Math.min(LEVEL, LARGEST_LEVEL), SMALLEST_LEVEL) - SMALLEST_LEVEL;
        r = ((level < 300 ? 0 : (level - 300) * (255 - SATURATION) / 300) + SATURATION) >>> darken;
        g = ((300 - abs(level - 300)) * (255 - SATURATION) / 300 + SATURATION) >>> darken;
        b = ((level > 300 ? 0 : level * (255 - SATURATION) / 300) + SATURATION) >>> darken;
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
            {192, 129, 66},  // WARN - Orange => old: {255, 165, 0},
            {255, 0, 0},     // ERROR - Red
            {128, 0, 0},     // FATAL - Dark Red
            {100, 120, 100}  // MESSAGE - Dark Green
        };

        int[][] darkThemeColors = {
            {200, 150, 255}, // TRACE - Bright Purple
            {100, 200, 255}, // DEBUG - Bright Blue
            {150, 255, 150}, // INFO - Bright Green
            {255, 200, 100}, // WARN - Bright Orange
            {255, 100, 100}, // ERROR - Bright Red
            {255, 50, 50},   // FATAL - Bright Red
            {180, 220, 180}  // MESSAGE - Light Green
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
