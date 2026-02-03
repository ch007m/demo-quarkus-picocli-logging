/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.aesh:terminal-tty:3.0-dev

package dev.snowdrop;

import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.ColorDepth;
import org.aesh.terminal.utils.TerminalColorCapability;

import java.io.IOException;

import static java.lang.Math.abs;

public class AnsiApp {
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
            darken = 0;
            System.out.printf("Printing different log messages using darken: %d\n", darken);
            printMessages();
            System.out.println("####################");

            darken = 1;
            System.out.printf("Printing different log messages using darken: %d\n", darken);
            printMessages();
            System.out.println("####################");

            TerminalConnection connection = new TerminalConnection();
            // Using the default color code and check if the theme is dark or light
            //cap = TerminalColorDetector.detect(connection);

            // Override the colors
            TerminalColorCapability cap = TerminalColorCapability.builder()
                .colorDepth(ColorDepth.TRUE_COLOR)
                //.infoCode(34)
                //.warningCode(33)
                //.errorCode(167)
                //.messageCode(32)
                //.timestampCode(244) // 16-colors Grey: 37, Light Grey: 90; True colors: 244
                .build();

            ANSIBuilder builder = ANSIBuilder.builder(cap)
                .rgb(192, 192, 192).append("2026-02-03 14:29:28,111").append(" ")
                .rgb(129, 192, 66).append("INFO").append("  ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 136, 255).append("io.quarkus")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 170, 68).append("Quarkus Main Thread")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(176, 208, 176).append("hello 1.0 on JVM (powered by Quarkus 3.24.2) started in 1.110s. Listening on: http://localhost:8080");
            System.out.println(builder.toString());

            builder = ANSIBuilder.builder(cap)
                .timestampRgb(192, 192, 192).timestamp("2026-02-03 14:29:29,797").append(" ")
                .infoRgb(129, 192, 66).info("INFO").append("  ")
                //.rgb(208,208,208).append("[")
                //.rgb(68,136,255).append("dev.sno.GreetingResource")
                //.rgb(208,208,208).append("]").append(" ")
                //.rgb(208,208,208).append("[")
                //.rgb(68,170,68).append("executor-thread-1")
                //.rgb(208,208,208).append("]").append(" ")
                .messageRgb(176, 208, 176).message("NEW info log message");
            System.out.println(builder.toString());

            builder = ANSIBuilder.builder(cap)
                .rgb(192, 192, 192).append("2026-02-03 14:29:29,797").append(" ")
                .rgb(129, 192, 66).append("INFO").append("  ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 136, 255).append("dev.sno.GreetingResource")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 170, 68).append("executor-thread-1")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(176, 208, 176).append("info log message");
            System.out.println(builder.toString());

            builder = ANSIBuilder.builder(cap)
                .timestampRgb(192, 192, 192).timestamp("2026-02-03 14:29:29,797").append(" ")
                .warningRgb(192, 129, 66).warning("WARN").append("  ")
                //.rgb(208,208,208).append("[")
                //.rgb(68,136,255).append("dev.sno.GreetingResource")
                //.rgb(208,208,208).append("]").append(" ")
                //.rgb(208,208,208).append("[")
                //.rgb(68,170,68).append("executor-thread-1")
                //.rgb(208,208,208).append("]").append(" ")
                .messageRgb(176, 208, 176).message("warn log message");
            System.out.println(builder.toString());

            builder = ANSIBuilder.builder(cap)
                .rgb(192, 192, 192).append("2026-02-03 14:29:29,797").append(" ")
                .rgb(255, 66, 66).append("ERROR").append("  ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 136, 255).append("dev.sno.GreetingResource")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 170, 68).append("executor-thread-1")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(176, 208, 176).append("error log message");
            System.out.println(builder.toString());

            builder = ANSIBuilder.builder(cap)
                .rgb(192, 192, 192).append("2026-02-03 14:29:29,797").append(" ")
                .rgb(66, 129, 129).append("DEBUG").append("  ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 136, 255).append("dev.sno.GreetingResource")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 170, 68).append("executor-thread-1")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(176, 208, 176).append("debug log message");
            System.out.println(builder.toString());

            builder = ANSIBuilder.builder(cap)
                .rgb(192, 192, 192).append("2026-02-03 14:29:29,797").append(" ")
                .rgb(66, 66, 66).append("TRACE").append("  ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 136, 255).append("dev.sno.GreetingResource")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(208, 208, 208).append("[")
                .rgb(68, 170, 68).append("executor-thread-1")
                .rgb(208, 208, 208).append("]").append(" ")
                .rgb(176, 208, 176).append("trace log message");
            System.out.println(builder.toString());

            // !! Don't forget to replace: \u001B[39m\u001B with \u001B[39m\u001B
                /*
                var infoMsg = "\u001B[39m\u001B[38;2;192;192;192m2026-02-03 14:29:29,797\u001B[39m\u001B[38;2;208;208;208m \u001B[39m\u001B[38;2;129;192;66mINFO \u001B[39m\u001B[38;2;208;208;208m [\u001B[39m\u001B[38;2;68;136;255mdev.sno.GreetingResource\u001B[39m\u001B[38;2;208;208;208m] (\u001B[39m\u001B[38;2;68;170;68mexecutor-thread-1\u001B[39m\u001B[38;2;208;208;208m) \u001B[39m\u001B[38;2;176;208;176minfo log message\u001B[39m\u001B[38;2;255;68;68m\u001B[39m\u001B[38;2;255;255;68m";
                var warnMsg = "\u001B[39m\u001B[38;2;192;192;192m2026-02-03 14:29:29,798\u001B[39m\u001B[38;2;208;208;208m \u001B[39m\u001B[38;2;192;129;66mWARN \u001B[39m\u001B[38;2;208;208;208m [\u001B[39m\u001B[38;2;68;136;255mdev.sno.GreetingResource\u001B[39m\u001B[38;2;208;208;208m] (\u001B[39m\u001B[38;2;68;170;68mexecutor-thread-1\u001B[39m\u001B[38;2;208;208;208m) \u001B[39m\u001B[38;2;176;208;176mwarn log message\u001B[39m\u001B[38;2;255;68;68m\u001B[39m\u001B[38;2;255;255;68m";
                var errorMsg = "\u001B[39m\u001B[38;2;192;192;192m2026-02-03 14:29:29,798\u001B[39m\u001B[38;2;208;208;208m \u001B[39m\u001B[38;2;255;66;66mERROR\u001B[39m\u001B[38;2;208;208;208m [\u001B[39m\u001B[38;2;68;136;255mdev.sno.GreetingResource\u001B[39m\u001B[38;2;208;208;208m] (\u001B[39m\u001B[38;2;68;170;68mexecutor-thread-1\u001B[39m\u001B[38;2;208;208;208m) \u001B[39m\u001B[38;2;176;208;176merror log message\u001B[39m\u001B[38;2;255;68;68m\u001B[39m\u001B[38;2;255;255;68m";
                var debugMsg = "\u001B[39m\u001B[38;2;192;192;192m2026-02-03 14:29:29,798\u001B[39m\u001B[38;2;208;208;208m \u001B[39m\u001B[38;2;66;129;129mDEBUG\u001B[39m\u001B[38;2;208;208;208m [\u001B[39m\u001B[38;2;68;136;255mdev.sno.GreetingResource\u001B[39m\u001B[38;2;208;208;208m] (\u001B[39m\u001B[38;2;68;170;68mexecutor-thread-1\u001B[39m\u001B[38;2;208;208;208m) \u001B[39m\u001B[38;2;176;208;176mdebug log message\u001B[39m\u001B[38;2;255;68;68m\u001B[39m\u001B[38;2;255;255;68m";
                var traceMsg = "\u001B[39m\u001B[38;2;192;192;192m2026-02-03 14:29:29,798\u001B[39m\u001B[38;2;208;208;208m \u001B[39m\u001B[38;2;66;66;66mTRACE\u001B[39m\u001B[38;2;208;208;208m [\u001B[39m\u001B[38;2;68;136;255mdev.sno.GreetingResource\u001B[39m\u001B[38;2;208;208;208m] (\u001B[39m\u001B[38;2;68;170;68mexecutor-thread-1\u001B[39m\u001B[38;2;208;208;208m) \u001B[39m\u001B[38;2;176;208;176mtrace log message\u001B[39m\u001B[38;2;255;68;68m\u001B[39m\u001B[38;2;255;255;68m";

                System.out.println("Text colored with RGB codes");
                System.out.println(infoMsg);
                System.out.println(warnMsg);
                System.out.println(errorMsg);
                System.out.println(debugMsg);
                System.out.println(traceMsg);
                 */

            connection.close();
        } catch (IOException e) {
            System.err.println("Error creating terminal connection: " + e.getMessage());
            System.exit(1);
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

    static void printMessages() {
        StringBuilder target = new StringBuilder();
        calculateRGB(INFO_LEVEL);
        startColor(target, 38, true, r,g,b);
        target.append("INFO log message");
        endColor(target, MODE);
        System.out.printf("%s\n", target);

        target = new StringBuilder();
        calculateRGB(WARN_LEVEL);
        startColor(target, 38, true, r,g,b);
        target.append("WARN log message");
        endColor(target, MODE);
        System.out.printf("%s\n", target);

        target = new StringBuilder();
        calculateRGB(ERROR_LEVEL);
        startColor(target, 38, true, r,g,b);
        target.append("ERROR log message");
        endColor(target, MODE);
        System.out.printf("%s\n", target);

        target = new StringBuilder();
        calculateRGB(DEBUG_LEVEL);
        startColor(target, 38, true, r,g,b);
        target.append("DEBUG log message");
        endColor(target, MODE);
        System.out.printf("%s\n", target);

        target = new StringBuilder();
        calculateRGB(TRACE_LEVEL);
        startColor(target, 38, true, r,g,b);
        target.append("TRACE log message");
        endColor(target, MODE);
        System.out.printf("%s\n", target);
    }
}
