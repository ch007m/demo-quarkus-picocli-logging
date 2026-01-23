package dev.snowdrop.logging;

import dev.snowdrop.logging.util.AnsiBuilder;
import dev.snowdrop.logging.util.LEVEL;
import dev.snowdrop.service.MessageService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class LoggingService {
    private static final Logger logger = Logger.getLogger(MessageService.class);

    private CommandLine.Model.CommandSpec spec;

    @ConfigProperty(name = "cli.mode", defaultValue = "false")
    boolean isCliMode;

    @ConfigProperty(name = "cli.logging.verbose", defaultValue = "false")
    boolean isVerbose;

    @ConfigProperty(name = "cli.logging.colored", defaultValue = "true")
    boolean useAnsiColoredMsg;
    
    private static String TIMESTAMP_COLOR = "TIMESTAMP";
    private static String MESSAGE_COLOR = "MESSAGE";

    CommandLine.Help.ColorScheme colorScheme;

    public LoggingService() {
    }

    public void info(String message) {
        // TODO: To be investigate to see if this is easier to use picocli Ansi and the colorScheme = spec.commandLine().getColorScheme();
        if (isCliMode) {
            //spec.commandLine().getOut().println(colorScheme.ansi().new Text("#" + message,colorScheme));
            spec.commandLine().getOut().println(formatedMessage(LEVEL.INFO, message));
        } else {
            logger.info(message);
        }
    }

    public void warn(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(formatedMessage(LEVEL.WARN, message));
        } else {
            logger.warn(message);
        }
    }

    public void error(String s) {
        this.error(s, null);
    }

    public void error(String message, Throwable e) {
        if (isCliMode) {
            spec.commandLine().getOut().println(formatedMessage(LEVEL.ERROR, message));
            if (e != null && isVerbose) {
                e.printStackTrace(spec.commandLine().getErr());
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

    public String formatedMessage(LEVEL level, String message) {
        String timeStamp = OffsetDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (useAnsiColoredMsg) {
            AnsiBuilder builder = new AnsiBuilder()
                .add(timeStamp, TIMESTAMP_COLOR)
                .add(level, level.getColor())
                .add(message, MESSAGE_COLOR);
            return builder.build();
        } else {
            return String.format("%s %s %s", timeStamp, level.toString(), message);
        }
    }

}
