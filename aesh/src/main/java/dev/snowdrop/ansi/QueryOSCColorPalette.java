/// usr/bin/env jbang "$0" "$@" ; exit $?

package dev.snowdrop.ansi;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class QueryOSCColorPalette {

    private static final Pattern OSC_RGB_PATTERN = Pattern.compile("\033\\](\\d+);(?:(\\d+);)?rgb:([0-9a-fA-F/]+)(?:\007|\033\\\\)");
    public static void main(String[] args) throws IOException, InterruptedException {
        setRawMode(true);
        Map<Integer, int[]> rgbList = new HashMap<>();
        try {
            rgbList.put(11, runQuery(11, null));
            for (int i = 0; i < 8; i++) {
                rgbList.put(i, runQuery(4, String.valueOf(i)));
            }
            System.out.print("\r\n--- Palette Visualization (TrueColor) ---\r\n");
            echoPalette(rgbList);

            int[] bgRgb = runQuery(11, null);
            boolean isDark = (bgRgb != null) && ((0.299 * bgRgb[0] + 0.587 * bgRgb[1] + 0.114 * bgRgb[2]) < 128);
            System.out.printf("\r\nDetected Theme: %s\r\n", isDark ? "DARK" : "LIGHT");

        } finally {
            setRawMode(false);
            // Print a final empty line after returning to sane mode to clear the prompt
            System.out.println();
        }
    }

    private static void echoPalette(Map<Integer, int[]> rgbList) {
        rgbList.forEach((index, rgb) -> {
            if (rgb != null) {
                String label = (index == 11) ? "Background" : "Index " + index;

                // TrueColor sequences: 48;2;R;G;Bm for background, 38;2;R;G;Bm for foreground
                String bgBlock = String.format("\033[48;2;%d;%d;%dm    \033[0m", rgb[0], rgb[1], rgb[2]);
                String fgText = String.format("\033[38;2;%d;%d;%dm Color Sample \033[0m", rgb[0], rgb[1], rgb[2]);

                System.out.printf("%-12s: %s %s\r\n", label, bgBlock, fgText);
            }
        });
    }

    private static int[] runQuery(int oscCode, String param) throws IOException, InterruptedException {
        String query = (param == null)
                ? String.format("\033]%d;?\007", oscCode)
                : String.format("\033]%d;%s;?\007", oscCode, param);

        System.out.print(query);
        System.out.flush();

        long start = System.nanoTime();
        String response = readResponse(500);
        long elapsed = System.nanoTime() - start;

        if (response.isEmpty()) {
            // Note the \r\n here for raw mode compatibility
            System.out.print("OSC " + oscCode + ": No response\r\n");
            return null;
        }

        Matcher matcher = OSC_RGB_PATTERN.matcher(response);
        if (matcher.find()) {
            String code = matcher.group(1);
            String p = matcher.group(2);
            String[] rgbHex = matcher.group(3).split("/");

            int[] rgb = new int[3];
            for (int i = 0; i < 3; i++) {
                String hex = rgbHex[i].trim();
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

            double ms = elapsed / 1_000_000.0;
            // Use \r\n to ensure the cursor returns to the start of the line
            System.out.printf("OSC %s (Param: %s) -> rgb(%s,%s,%s) [%.2f ms]\r\n",
                    code, (p != null ? p : "none"), rgb[0], rgb[1], rgb[2], ms);
            return rgb;
        }
        return null;
    }

    public static String getTheme(int[] rgb) {
        if (rgb == null) return "UNKNOWN (Defaulting to LIGHT)";

        // Calculate perceived brightness (0 to 255)
        double luminance = (0.299 * rgb[0]) + (0.587 * rgb[1]) + (0.114 * rgb[2]);

        // Threshold: 128 is the midpoint.
        // Closer to 0 is DARK, closer to 255 is LIGHT.
        return (luminance < 128) ? "DARK" : "LIGHT";
    }

    private static String readResponse(int timeoutMs) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        InputStream in = System.in;
        long deadline = System.currentTimeMillis() + timeoutMs;

        while (System.currentTimeMillis() < deadline) {
            if (in.available() > 0) {
                int b = in.read();
                if (b == -1) break;
                sb.append((char) b);
                // Check for terminators: BEL (\007) or ST (ESC \)
                if (b == 7 || sb.toString().endsWith("\033\\")) break;
            } else {
                Thread.sleep(5); // Shorter sleep for snappier response
            }
        }
        return sb.toString();
    }

    private static void setRawMode(boolean raw) throws IOException, InterruptedException {
        // 'stty raw' disables output processing (ONLCR), so \n doesn't become \r\n
        // 'stty -echo' is also recommended to prevent the query sequence from appearing
        ProcessBuilder pb = new ProcessBuilder("stty", raw ? "raw" : "sane");
        pb.inheritIO().start().waitFor(5, TimeUnit.SECONDS);
    }
}