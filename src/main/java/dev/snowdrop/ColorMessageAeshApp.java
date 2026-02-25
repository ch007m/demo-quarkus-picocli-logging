/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final
//DEPS org.aesh:terminal-tty:3.2
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager

package dev.snowdrop;

import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.TerminalColorCapability;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class ColorMessageAeshApp {
    private final static String SPACE = " ";
    private static TerminalColorCapability cap;
    private static ANSIBuilder builder;

    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            long start = System.currentTimeMillis();
            TerminalConnection connection = new TerminalConnection();
            cap = TerminalColorDetector.detect(connection);

            long elapsed = System.currentTimeMillis() - start;

            System.out.printf("Theme: %s%n", cap.getTheme());
            System.out.printf("Theme detection took: [%s ms]%n", elapsed);

            for (String l : new String[]{"FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"}) {
                System.out.println(printMsg(l, "This is a log message"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String printMsg(String level, String message) {

        String timeStamp = OffsetDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        int levelColorCode = switch (level) {
            case "TRACE" -> cap.getSuggestedTraceCode();
            case "DEBUG" -> cap.getSuggestedDebugCode();
            case "INFO"-> cap.getSuggestedInfoCode();
            case "WARN"-> cap.getSuggestedWarningCode();
            case "ERROR" -> cap.getSuggestedErrorCode();
            case "FATAL" -> cap.getSuggestedFatalCode();
            default -> cap.getSuggestedMessageCode();
        };

        builder = ANSIBuilder.builder(cap)
                .timestamp(timeStamp)
                .append(SPACE);

        switch (level) {
            case "TRACE":
                builder.trace(level.toString());
                break;
            case "DEBUG":
                builder.debug(level.toString());
                break;
            case "INFO":
                builder.info(level.toString());
                break;
            case "WARN":
                builder.warning(level.toString());
                break;
            case "ERROR":
                builder.error(level.toString());
                break;
            case "FATAL":
                builder.fatal(level.toString());
                break;
            default:
                builder.success(level.toString());
                break;
        }

        return builder.append(SPACE)
                .message(message)
                .toString();
    }
}
