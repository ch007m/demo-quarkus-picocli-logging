/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.aesh:terminal-tty:3.0-dev

package dev.snowdrop;

import java.util.Arrays;

import static java.lang.Math.abs;

/**
 * Project able to calculate the RGB codes using HSV values
 *
 * Code inspired from:
 * - ColorUtil: https://github.com/jboss-logging/jboss-logmanager/blob/main/src/main/java/org/jboss/logmanager/formatters/ColorUtil.java#L37-L49
 * - ColorPatternFormatter: https://github.com/jboss-logging/jboss-logmanager/blob/main/src/main/java/org/jboss/logmanager/formatters/ColorPatternFormatter.java#L187-L195
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
    static int SATURATION = 66;

    static int darken;
    static int r;
    static int g;
    static int b;

    public static void main(String[] args) {
        try {
            for (int num: Arrays.asList(0,1)) {
                darken = num;
                System.out.println("\n################################################################################");
                System.out.printf("The jboss way to calculate RGB colors for messages to log using darken: %d\n", darken);
                System.out.println("################################################################################");
                printMessages();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void printMessages() throws NoSuchFieldException, IllegalAccessException {
        for (String LEVEL: Arrays.asList("TRACE","DEBUG","INFO","WARN","ERROR", "FATAL")) {
            StringBuilder target = new StringBuilder();

            calculateRGB((int) JBossLoggingApp.class.getDeclaredField(LEVEL + "_LEVEL").get(null));

            startColor(target, 38, true, r,g,b);
            target.append(LEVEL);
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

    static void calculateRGB(int LEVEL) {
        final int level = Math.max(Math.min(LEVEL, LARGEST_LEVEL), SMALLEST_LEVEL) - SMALLEST_LEVEL;
        r = ((level < 300 ? 0 : (level - 300) * (255 - SATURATION) / 300) + SATURATION) >>> darken;
        g = ((300 - abs(level - 300)) * (255 - SATURATION) / 300 + SATURATION) >>> darken;
        b = ((level > 300 ? 0 : level * (255 - SATURATION) / 300) + SATURATION) >>> darken;
    }
}
