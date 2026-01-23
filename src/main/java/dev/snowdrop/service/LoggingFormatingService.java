package dev.snowdrop.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class LoggingFormatingService {
    private static final Logger logger = Logger.getLogger(MessageService.class);

    private final String ANSI_WARN = "@|bold,yellow WARN: %s |@";
    private final String ANSI_ERROR = "@|bold,red ERROR: %s |@";
    private static final String ESC_CHAR = "\u001B";

    public static final String WHITEDIM = "[37;2m";
    public static final String GREEN = "[32m";
    public static final String LIGHTGREY = "[90m";

    public static final String MAGENTA = "[35m";
    public static final String GREENLIGHT = "[92m";
    public static final String CYAN = "[36m";

    public static final String RESET = "\u001B[0m";

    private CommandLine.Model.CommandSpec spec;

    @ConfigProperty(name = "cli.mode", defaultValue = "false")
    boolean isCliMode;

    @ConfigProperty(name = "cli.logging.verbose", defaultValue = "false")
    boolean isVerbose;

    public LoggingFormatingService() {
    }

    public void info(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(
                    CommandLine.Help.Ansi.AUTO.string(
                            colorize(OffsetDateTime.now()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), WHITEDIM) + " " +
                                    colorize("INFO", GREEN) + " " + colorize(message, LIGHTGREY)));
        } else {
            logger.info(message);
        }
    }

    public void warn(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(
                    CommandLine.Help.Ansi.AUTO.string(String.format(ANSI_WARN, message)));
        } else {
            logger.warn(message);
        }
    }

    public void error(String s) {
        this.error(s, null);
    }

    public void error(String message, Throwable e) {
        if (isCliMode) {
            spec.commandLine().getErr().println(
                    CommandLine.Help.Ansi.AUTO.string(String.format(ANSI_ERROR, message)));
            if (e != null && isVerbose) {
                e.printStackTrace(spec.commandLine().getErr());
            }
        } else {
            if (isVerbose && e != null) {
                logger.error(message,e);
            } else {
                logger.error(message);
            }
        }
    }

    public void setSpec(CommandLine.Model.CommandSpec spec) {
        this.spec = spec;
    }

    /**
     * Applies color formatting to text.
     *
     * @param text the text to color
     * @param color the ANSI color code
     * @return the colored text
     */
    private static String colorize(String text, String color) {
        return ESC_CHAR + color + text + RESET;
    }

}
