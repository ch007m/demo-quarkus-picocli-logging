/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.aesh:terminal-tty:3.0

package dev.snowdrop.ansi;

import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.ColorDepth;
import org.aesh.terminal.utils.TerminalColorCapability;

import java.io.IOException;

public class AnsiApp {

    public static void main(String[] args) {
        try {

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
}
