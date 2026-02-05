/// usr/bin/env jbang "$0" "$@"; exit $?
//DEPS org.aesh:terminal-tty:3.0

package dev.snowdrop.ansi;

import org.aesh.terminal.Connection;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.TerminalColorCapability;

import java.util.Arrays;
import java.util.List;

// https://textual.textualize.io/api/color/
public class ColorDetector {
    private static Connection connection;

    public static void main(String[] args) throws Exception {
        int oscCode = 4;

        connection = new TerminalConnection();
        connection.openNonBlocking();

        System.out.println("Colors detected: " + connection.getColorCapability().getColorDepth());

        boolean oscSupported = TerminalColorDetector.isOscColorQuerySupported();
        System.out.println("is OSC supported: " + oscSupported);
        System.out.println("Theme : " + connection.getColorCapability().getTheme());

        if (oscSupported) {
            var bkColor = TerminalColorDetector.queryBackgroundColor(connection, 100);
            System.out.println("Background color : " + Arrays.toString(bkColor));

            TerminalColorCapability cap = TerminalColorCapability.builder().build();

            // Ansi name: White
            int timeStampCode = get256ColorIndex(192, 192, 192);
            System.out.println("Time Stamp Code : " + timeStampCode);
            var timeStampRGB = connection.queryOsc(oscCode, String.format("%d;?", timeStampCode), 500,
                input -> parseOscResponse(input, oscCode, String.format("%d;?", timeStampCode), timeStampCode));

            // Ansi color name
            int msgCode = get256ColorIndex(176, 208, 176);
            System.out.println("Message Code : " + msgCode);
            var msgRGB = connection.queryOsc(oscCode, String.format("%d;?", msgCode), 500,
                input -> parseOscResponse(input, oscCode, String.format("%d;?", msgCode), msgCode));

            int lightGray = get256ColorIndex(208,208,208);
            System.out.println("Light Gray : " + lightGray);
            var lightGrayRGB = connection.queryOsc(oscCode, String.format("%d;?", lightGray), 500,
                input -> parseOscResponse(input, oscCode, String.format("%d;?", lightGray), lightGray));

            int azureBlue = get256ColorIndex(68, 136, 255);
            System.out.println("Azure Blue : " + azureBlue);
            var azureBlueRGB = connection.queryOsc(oscCode, String.format("%d;?", azureBlue), 500,
                input -> parseOscResponse(input, oscCode, String.format("%d;?", azureBlue), azureBlue));

            int seaBlue = get256ColorIndex(68, 170, 68);
            System.out.println("Sea Blue : " + seaBlue);
            var seaBlueRGB = connection.queryOsc(oscCode, String.format("%d;?", seaBlue), 500,
                input -> parseOscResponse(input, oscCode, String.format("%d;?", seaBlue), seaBlue));


            for (int colorCode : List.of(0,1,2,3,4,5,6,7)) {
                var res = connection.queryOsc(oscCode, String.format("%d;?", colorCode), 500,
                    input -> parseOscResponse(input, oscCode, String.format("%d;?", colorCode), colorCode));
                // System.out.printf("Color name: %s and code: %s.\n", AnsiColor.getNameByIndex(colorCode), Arrays.toString(res));

                int r,g,b;
                r = res[0];
                g = res[1];
                b = res[2];

                ANSIBuilder builder = ANSIBuilder.builder(cap)
                    .rgb(timeStampRGB[0],timeStampRGB[1],timeStampRGB[2]).append("2026-02-03 14:29:29,797").append(" ")
                    .rgb(r,g,b).append(AnsiColor.getNameByIndex(colorCode)).append("  ")
                    .rgb(lightGrayRGB[0],lightGrayRGB[1],lightGrayRGB[2]).append("[")
                    .rgb(azureBlueRGB[0],azureBlueRGB[1],azureBlueRGB[2]).append("dev.sno.GreetingResource")
                    .rgb(lightGrayRGB[0],lightGrayRGB[1],lightGrayRGB[2]).append("]").append(" ")
                    .rgb(lightGrayRGB[0],lightGrayRGB[1],lightGrayRGB[2]).append("[")
                    .rgb(seaBlueRGB[0],seaBlueRGB[1],seaBlueRGB[2]).append("executor-thread-1")
                    .rgb(lightGrayRGB[0],lightGrayRGB[1],lightGrayRGB[2]).append("]").append(" ")
                    .rgb(msgRGB[0],msgRGB[1],msgRGB[2]).append("info log message");
                System.out.println(builder.toString());

            }
        }

        connection.close();
    }

    private static int get256ColorIndex(int r, int g, int b) {
        // 1. Check if it's a grayscale value (r, g, and b are very close)
        if (r == g && g == b) {
            if (r < 8) return 16;       // Pure Black
            if (r > 248) return 231;    // Pure White
            // Map to the 24-step grayscale ramp (indices 232-255)
            return (int) Math.round(((r - 8) / 247.0) * 23) + 232;
        }

        // 2. Map to the 6x6x6 color cube (indices 16-231)
        // Formula: 16 + 36 * r_index + 6 * g_index + b_index
        int rIdx = (int) Math.round((r / 255.0) * 5);
        int gIdx = (int) Math.round((g / 255.0) * 5);
        int bIdx = (int) Math.round((b / 255.0) * 5);

        return 16 + (36 * rIdx) + (6 * gIdx) + bIdx;
    }

    private static int[] parseOscResponse(int[] input, int oscCode, String oscParam, int colorCode) {
        StringBuilder sb = new StringBuilder();
        for (int cp : input) {
            sb.appendCodePoint(cp);
        }
        String str = sb.toString();
        int start = str.indexOf("]" + oscCode + ";" + colorCode + ";rgb:");
        if (start < 0) {
            return null;
        }

        // Extract the rgb: part
        int rgbStart = str.indexOf("rgb:", start);
        if (rgbStart < 0) {
            return null;
        }
        rgbStart += 4; // skip "rgb:"

        // Find the terminator (BEL or ESC \)
        int end = str.indexOf('\u0007', rgbStart);
        if (end < 0) {
            end = str.indexOf("\u001B\\", rgbStart);
        }
        if (end < 0) {
            end = str.length();
        }

        String rgbPart = str.substring(rgbStart, end);

        // Parse RRRR/GGGG/BBBB
        String[] parts = rgbPart.split("/");
        if (parts.length != 3) {
            return null;
        }

        try {
            int[] rgb = new int[3];
            for (int i = 0; i < 3; i++) {
                String hex = parts[i].trim();
                int value;
                if (hex.length() == 4) {
                    // 4-digit hex (e.g., FFFF), take high byte
                    value = Integer.parseInt(hex, 16) >> 8;
                } else if (hex.length() == 2) {
                    // 2-digit hex
                    value = Integer.parseInt(hex, 16);
                } else {
                    return null;
                }
                rgb[i] = Math.min(255, Math.max(0, value));
            }
            return rgb;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public enum AnsiColor {
        // Standard Colors (0-7)
        BLACK(0, "Black"),
        RED(1, "Red"),
        GREEN(2, "Green"),
        YELLOW(3, "Yellow"),
        BLUE(4, "Blue"),
        MAGENTA(5, "Magenta"),
        CYAN(6, "Cyan"),
        WHITE(7, "White"),
        SILVER(37, "Silver"),

        // Bright/Bold Variants (8-15)
        BRIGHT_BLACK(8, "Bright Black"),
        BRIGHT_RED(9, "Bright Red"),
        BRIGHT_GREEN(10, "Bright Green"),
        BRIGHT_YELLOW(11, "Bright Yellow"),
        BRIGHT_BLUE(12, "Bright Blue"),
        BRIGHT_MAGENTA(13, "Bright Magenta"),
        BRIGHT_CYAN(14, "Bright Cyan"),
        BRIGHT_WHITE(15, "Bright White");

        private final int index;
        private final String name;

        AnsiColor(int index, String name) {
            this.index = index;
            this.name = name;
        }

        /**
         * Returns the name of the color based on the ANSI index (0-15).
         */
        public static String getNameByIndex(int index) {
            for (AnsiColor color : AnsiColor.values()) {
                if (color.index == index) {
                    return color.name;
                }
            }
            return "Unknown Index: " + index;
        }
    }
}