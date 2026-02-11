package dev.snowdrop.logging;

import dev.snowdrop.logging.util.LEVEL;
import jakarta.enterprise.context.ApplicationScoped;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.formatters.ColorPatternFormatter;
import picocli.CommandLine;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class LoggingService {
    private static final Logger logger = Logger.getLogger(LoggingService.class);

    @ConfigProperty(name = "cli.mode", defaultValue = "false")
    boolean isCliMode;

    @ConfigProperty(name = "cli.logging.verbose", defaultValue = "false")
    boolean isVerbose;

    @ConfigProperty(name = "cli.logging.colored", defaultValue = "true")
    boolean useAnsiColoredMsg;

    private CommandLine.Model.CommandSpec spec;
    private TerminalColorCapability cap;
    private final static String SPACE = " ";

    public LoggingService() {
    }

    public void colorDetector() {
        try {
            TerminalConnection connection = new TerminalConnection();
            connection.openNonBlocking();
            cap = TerminalColorDetector.detect(connection);
            connection.close();
        } catch (IOException e) {
            System.err.println("Error creating terminal connection: " + e.getMessage());
            System.exit(1);
        }
    }

    public void info(String message) {
        if (isCliMode) {
            var formatedMessage = colorizeMessage(LEVEL.INFO, message);
            spec.commandLine().getOut().println(formatedMessage);
        } else {
            logger.info(message);
        }
    }

    public void warn(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(colorizeMessage(LEVEL.WARN, message));
        } else {
            logger.warn(message);
        }
    }

    public void debug(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(colorizeMessage(LEVEL.DEBUG, message));
        } else {
            logger.debug(message);
        }
    }

    public void trace(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(colorizeMessage(LEVEL.TRACE, message));
        } else {
            logger.trace(message);
        }
    }

    public void fatal(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(colorizeMessage(LEVEL.FATAL, message));
        } else {
            logger.fatal(message);
        }
    }

    public void error(String s) {
        this.error(s, null);
    }

    public void error(String message, Throwable e) {
        if (isCliMode) {
            spec.commandLine().getOut().println(colorizeMessage(LEVEL.ERROR, message));
            if (e != null && isVerbose) {
                //e.printStackTrace(spec.commandLine().getErr());
                java.io.StringWriter sw = new java.io.StringWriter();
                e.printStackTrace(new java.io.PrintWriter(sw));
                String stackTrace = sw.toString();
                spec.commandLine().getErr().println(colorizeMessage(LEVEL.ERROR, stackTrace, true));
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

    public String colorizeMessage(LEVEL level, String message) {
        return colorizeMessage(level, message, false);
    }

    public String colorizeMessage(LEVEL level, String message, boolean isStackTrace) {
        int darken;
        if (cap.getTheme().isDark()) {
            darken = 0;
        } else {
            darken = 1;
        }
        ColorPatternFormatter fmt = new ColorPatternFormatter(darken, "%d{HH:mm:ss,SSS} %-5p %s%E");
        ExtLogRecord record = new ExtLogRecord(
                level.toJbossLevel(), message, ExtLogRecord.FormatStyle.PRINTF,
                LoggingService.class.getName());
        return fmt.format(record);
    }

    public String colorizeMessageWithAesh(LEVEL level, TerminalColorCapability cap, String message, boolean isStackTrace) {
        String timeStamp = OffsetDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (useAnsiColoredMsg) {
            int levelColorCode = switch (level) {
                case ERROR -> cap.getSuggestedErrorCode();
                case FATAL -> cap.getSuggestedErrorCode();
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
