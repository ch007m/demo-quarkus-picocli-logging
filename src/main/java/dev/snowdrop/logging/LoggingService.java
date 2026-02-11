package dev.snowdrop.logging;

import dev.snowdrop.logging.util.LEVEL;
import dev.snowdrop.service.MessageService;
import jakarta.enterprise.context.ApplicationScoped;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class LoggingService {
    private static final Logger logger = Logger.getLogger(MessageService.class);

    private CommandLine.Model.CommandSpec spec;
    private TerminalColorCapability cap;

    @ConfigProperty(name = "cli.mode", defaultValue = "false")
    boolean isCliMode;

    @ConfigProperty(name = "cli.logging.verbose", defaultValue = "false")
    boolean isVerbose;

    @ConfigProperty(name = "cli.logging.colored", defaultValue = "true")
    boolean useAnsiColoredMsg;

    private final static String TIMESTAMP_COLOR = "TIMESTAMP";
    private final static String MESSAGE_COLOR = "MESSAGE";
    private final static String SPACE = " ";

    private static boolean isDark;

    // CommandLine.Help.ColorScheme colorScheme;

    public LoggingService() {
    }

    public void setupAesh() {
        try {
            TerminalConnection connection = new TerminalConnection();
            // Using the default color code and check if the theme is dark or light
            cap = TerminalColorDetector.detect(connection);

            //TerminalTheme theme = connection.getColorCapability().getTheme();
            //isDark = (theme == TerminalTheme.DARK);

            //printCapability(connection,"   ",cap);
            //demonstrateColors(connection,cap);

            connection.close();
        } catch (IOException e) {
            System.err.println("Error creating terminal connection: " + e.getMessage());
            System.exit(1);
        }
    }

    public void info(String message) {
        // TODO: To be investigate to see if this is easier to use picocli Ansi and the colorScheme = spec.commandLine().getColorScheme();
        if (isCliMode) {
            //spec.commandLine().getOut().println(colorScheme.ansi().new Text("#" + message,colorScheme));
            var formatedMessage = newFormatedMessage(LEVEL.INFO, cap, message);
            spec.commandLine().getOut().println(formatedMessage);
        } else {
            logger.info(message);
        }
    }

    public void warn(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.WARN, cap, message));
        } else {
            logger.warn(message);
        }
    }

    public void debug(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.DEBUG, cap, message));
        } else {
            logger.debug(message);
        }
    }

    public void trace(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.TRACE, cap, message));
        } else {
            logger.trace(message);
        }
    }

    public void error(String s) {
        this.error(s, null);
    }

    public void error(String message, Throwable e) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.ERROR, cap, message));
            if (e != null && isVerbose) {
                //e.printStackTrace(spec.commandLine().getErr());
                java.io.StringWriter sw = new java.io.StringWriter();
                e.printStackTrace(new java.io.PrintWriter(sw));
                String stackTrace = sw.toString();
                spec.commandLine().getErr().println(newFormatedMessage(LEVEL.ERROR, cap, stackTrace, true));
            }
        } else {
            if (isVerbose && e != null) {
                logger.error(message, e);
            } else {
                logger.error(message);
            }
        }
    }

    public void setSpec(CommandLine.Model.CommandSpec spec) {
        this.spec = spec;
    }

    public String newFormatedMessage(LEVEL level, TerminalColorCapability cap, String message) {
        return newFormatedMessage(level, cap, message, false);
    }

    public String newFormatedMessage(LEVEL level, TerminalColorCapability cap, String message, boolean isStackTrace) {
        String timeStamp = OffsetDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (useAnsiColoredMsg) {
            int levelColorCode = switch (level) {
                case ERROR -> cap.getSuggestedErrorCode();
                case WARN -> cap.getSuggestedWarningCode();
                case INFO -> cap.getSuggestedInfoCode();
                case DEBUG -> cap.getSuggestedDebugCode();
                case TRACE -> cap.getSuggestedTraceCode();
            };

            /*
            int[] messageColor, timestampColor;
            int lightness = 80;
            if (isDark) {
                messageColor = new int[]{120, 25, lightness};
                timestampColor = new int[]{0, 0, lightness};
            } else {
                messageColor = new int[]{120, 25, 100 - lightness};
                timestampColor = new int[]{0, 0, 100 - lightness};
            }

            ANSIBuilder builder = ANSIBuilder.builder()
                    .timestampHsl(timestampColor[0], timestampColor[1], timestampColor[2])
                    .messageHsl(messageColor[0], messageColor[1], messageColor[2]);
             */

            ANSIBuilder builder = ANSIBuilder.builder()
                    .timestampCode(cap.getSuggestedTimestampCode())
                    .messageCode(cap.getSuggestedMessageCode())
                    //.timestampCode(96)
                    //.messageCode(95)
                    .timestamp(timeStamp)
                    .append(SPACE)
                    .color256(levelColorCode, level.toString())
                    .append(SPACE)
                    .message(message);

            return builder.toString();

        } else {
            return String.format("%s %s %s", timeStamp, level.toString(), message);
        }
    }
}
