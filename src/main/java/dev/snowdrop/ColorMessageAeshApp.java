/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final-SNAPSHOT
//DEPS org.aesh:terminal-tty:3.1
//SOURCES logging/util
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager

package dev.snowdrop;

import dev.snowdrop.logging.util.LEVEL;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.TerminalColorCapability;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ColorMessageAeshApp {
    private final static String SPACE = " ";

    public static void main(String[] args) throws IOException, InterruptedException {
        TerminalColorCapability cap;
        try {
            long start = System.nanoTime();

            TerminalConnection connection = new TerminalConnection();
            connection.openNonBlocking();
            cap = TerminalColorDetector.detect(connection);

            long elapsed = System.nanoTime() - start;
            double ms = elapsed / 1_000_000.0;

            System.out.printf("Theme: %s%n",cap.getTheme());
            System.out.printf("Theme detection took: [%.2f ms]%n", ms);

            for(LEVEL l : List.of(LEVEL.ERROR,LEVEL.WARN,LEVEL.INFO,LEVEL.DEBUG,LEVEL.TRACE)){
                System.out.println(printMsg(l, cap, "This is a log message"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String printMsg(LEVEL level, TerminalColorCapability cap, String message) {
        String timeStamp = OffsetDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        int levelColorCode = switch (level) {
            case ERROR -> cap.getSuggestedErrorCode();
            case WARN -> cap.getSuggestedWarningCode();
            case INFO -> cap.getSuggestedInfoCode();
            case DEBUG -> cap.getSuggestedDebugCode();
            case TRACE -> cap.getSuggestedTraceCode();
        };

        ANSIBuilder builder = ANSIBuilder.builder()
                .timestampCode(cap.getSuggestedTimestampCode())
                .messageCode(cap.getSuggestedMessageCode())
                .timestamp(timeStamp)
                .append(SPACE)
                .color256(levelColorCode, level.toString())
                .append(SPACE)
                .message(message);

        return builder.toString();
    }
}
