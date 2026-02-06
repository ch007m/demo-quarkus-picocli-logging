/// usr/bin/env jbang "$0" "$@"; exit $?
//DEPS org.aesh:terminal-tty:3.0

package dev.snowdrop.ansi;

import org.aesh.terminal.Connection;
import org.aesh.terminal.tty.Capability;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.ColorDepth;
import org.aesh.terminal.utils.TerminalColorCapability;
import org.aesh.terminal.utils.TerminalTheme;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 *
 * This application uses Ansi Color code converted to RGB to colorize with the help of the Aesh Terminal and utils
 *
 * Color Tools:
 * <p>
 * Palette : https://colorizer.org/
 * Another color palette : https://hexdocs.pm/color_palette/color_table.html
 * <p>
 * Java color utils: https://github.com/ngageoint/color-java/blob/102fa4cbfaaf0883485d4f4c6796aa99f31eb380/src/main/java/mil/nga/color/ColorUtils.java#L373-L394
 */

public class ColorAnsiRGB {
    private static Connection connection;

    public static void main(String[] args) throws Exception {
        int oscCommandCode = 4;

        connection = new TerminalConnection();
        connection.openNonBlocking();

        System.out.println("-".repeat(80));
        System.out.printf("Color depth: %s%n",connection.getColorCapability().getTheme());

        TerminalTheme theme = connection.getColorCapability().getTheme();
        boolean isDark = (theme == TerminalTheme.DARK);

        System.out.println("-".repeat(80));
        System.out.printf("Detected Theme: %s%n",isDark);
        System.out.println("--------------------------------------------------");

        // 2. Define Lightness logic

        boolean oscSupported = TerminalColorDetector.isOscColorQuerySupported();
        System.out.println("is OSC supported: " + oscSupported);
        System.out.println("Theme : " + connection.getColorCapability().getTheme());

        if (oscSupported) {
            // Background detection
            var bkColor = TerminalColorDetector.queryBackgroundColor(connection, 100);
            System.out.println("Background color : " + Arrays.toString(bkColor));

            TerminalColorCapability cap = TerminalColorCapability.builder().build();

            // Pre-calculate our specific theme colors
            int[] timeStampRGB = queryRgb(oscCommandCode, get256ColorIndex(192, 192, 192));

            int r=176,g=208,b=176;
            //int[] msgRGB = queryRgb(oscCommandCode, get256ColorIndex(r,g,b));
            var ansiColorCode = getAnsiColorCodeFromRGB("Message",r,g,b);
            int[] rgb = connection.queryPaletteColor(ansiColorCode, 500);
            System.out.printf("%s color code: %-3d - rgb(%d,%d,%d)%n", "Message", ansiColorCode, rgb[0],rgb[1],rgb[2]);

            //System.exit(0);

            int[] lightGrayRGB = queryRgb(oscCommandCode, get256ColorIndex(208, 208, 208));
            int[] azureBlueRGB = connection.queryPaletteColor(75, 500);
            ; // get256ColorIndex(68, 136, 255)
            System.out.println("Package/class color : " + Arrays.toString(azureBlueRGB));
            int[] seaBlueRGB = queryRgb(oscCommandCode, get256ColorIndex(68, 170, 68));

            // Use IntStream for the 0..255 range (standard terminal limit is 255)
            IntStream.rangeClosed(0, 255).forEach(colorCode -> {
                int[] res = connection.queryPaletteColor(colorCode, 500);

                if (res != null) {
                    //ANSIBuilder builder = ANSIBuilder.builder(cap)
                    //    .rgb(timeStampRGB[0], timeStampRGB[1], timeStampRGB[2]).append("2026-02-03 14:29:29,797").append(" ")
                    //    .rgb(res[0], res[1], res[2]).append(String.format("%-18s", AnsiColor.getNameByIndex(colorCode))).append(" ")
                    //    .rgb(lightGrayRGB[0], lightGrayRGB[1], lightGrayRGB[2]).append("[")
                    //    .rgb(azureBlueRGB[0], azureBlueRGB[1], azureBlueRGB[2]).append("dev.sno.GreetingResource")
                    //    .rgb(lightGrayRGB[0], lightGrayRGB[1], lightGrayRGB[2]).append("]").append(" ")
                    //    .rgb(lightGrayRGB[0], lightGrayRGB[1], lightGrayRGB[2]).append("[")
                    //    .rgb(seaBlueRGB[0], seaBlueRGB[1], seaBlueRGB[2]).append("executor-thread-1")
                    //    .rgb(lightGrayRGB[0], lightGrayRGB[1], lightGrayRGB[2]).append("]").append(" ")
                    //    .rgb(msgRGB[0], msgRGB[1], msgRGB[2]).append("info log message");
                    //     System.out.println(builder.toString());

                    ANSIBuilder builder = ANSIBuilder.builder(cap)
                        .rgb(res[0],res[1],res[2]).append(String.format("%s", AnsiColor.getNameByIndex(colorCode)));
                    System.out.println(builder.toString());
                }
            });
        }

        connection.close();
    }

    private static int getAnsiColorCodeFromRGB(String msg, int r, int g, int b) {
        int ansiColorCode = get256ColorIndex(r,g,b);
        System.out.printf("%s color code: %-3d - rgb(%d,%d,%d)%n", msg, ansiColorCode, r,g,b);
        return ansiColorCode;
    }

    private static int[] queryRgb(int oscCode, int colorCode) {
        return connection.queryOsc(oscCode, String.format("%d;?", colorCode), 500,
            input -> parseOscResponse(input, oscCode, String.format("%d;?", colorCode), colorCode));
    }

    private static int get256ColorIndex(int r, int g, int b) {
        if (r == g && g == b) {
            if (r < 8) return 16;
            if (r > 248) return 231;
            return (int) Math.round(((r - 8) / 247.0) * 23) + 232;
        }
        int rIdx = (int) Math.round((r / 255.0) * 5);
        int gIdx = (int) Math.round((g / 255.0) * 5);
        int bIdx = (int) Math.round((b / 255.0) * 5);
        return 16 + (36 * rIdx) + (6 * gIdx) + bIdx;
    }

    private static int[] parseOscResponse(int[] input, int oscCode, String oscParam, int colorCode) {
        StringBuilder sb = new StringBuilder();
        for (int cp : input) sb.appendCodePoint(cp);
        String str = sb.toString();
        int start = str.indexOf("]" + oscCode + ";" + colorCode + ";rgb:");
        if (start < 0) return null;

        int rgbStart = str.indexOf("rgb:", start) + 4;
        int end = str.indexOf('\u0007', rgbStart);
        if (end < 0) end = str.indexOf("\u001B\\", rgbStart);
        if (end < 0) end = str.length();

        String[] parts = str.substring(rgbStart, end).split("/");
        if (parts.length != 3) return null;

        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            String hex = parts[i].trim();
            rgb[i] = Integer.parseInt(hex.substring(0, 2), 16);
        }
        return rgb;
    }

    public enum AnsiColor {
        BLACK(0, "Black"), RED(1, "Red"), GREEN(2, "Green"), YELLOW(3, "Yellow"),
        BLUE(4, "Blue"), MAGENTA(5, "Magenta"), CYAN(6, "Cyan"), WHITE(7, "White"),
        GREY(8, "Grey"), BRIGHT_RED(9, "Bright Red"), BRIGHT_GREEN(10, "Bright Green"),
        BRIGHT_YELLOW(11, "Bright Yellow"), BRIGHT_BLUE(12, "Bright Blue"),
        BRIGHT_MAGENTA(13, "Bright Magenta"), BRIGHT_CYAN(14, "Bright Cyan"),
        BRIGHT_WHITE(15, "Bright White");

        private final int index;
        private final String name;

        AnsiColor(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static String getNameByIndex(int index) {
            // Check static definitions
            for (AnsiColor color : AnsiColor.values()) {
                if (color.index == index) {
                    int r = (index / 36) * 51;
                    int g = ((index % 36) / 6) * 51;
                    int b = (index % 6) * 51;
                    return String.format("Color name: %-15s - code: %-3d - RGB(%d,%d,%d)", color.name, index, r, g, b);
                }
            }

            // Logical naming for Extended Palette
            if (index >= 232 && index <= 255) {
                int level = (int) (((index - 232) / 23.0) * 100);
                return String.format("Color name: %-15s - code: %-3d - Grey %d %%dd", "n/a", index, level);
            } else if (index >= 16 && index <= 231) {
                int i = index - 16;
                int r = (i / 36) * 51;
                int g = ((i % 36) / 6) * 51;
                int b = (i % 6) * 51;
                return String.format("Color name: %-15s - code: %-3d - RGB(%d,%d,%d)", "n/a", index, r, g, b);
            }
            return "Color " + index;
        }
    }
}