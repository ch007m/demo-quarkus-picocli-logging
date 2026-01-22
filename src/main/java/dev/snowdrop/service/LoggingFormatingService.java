package dev.snowdrop.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import picocli.CommandLine;

@ApplicationScoped
public class LoggingFormatingService {
    private static final Logger logger = Logger.getLogger(MessageService.class);
    private final String ANSI_WARN = "@|bold,yellow WARN: %s |@";
    private final String ANSI_ERROR = "@|bold,red ERROR: %s |@";
    private CommandLine.Model.CommandSpec spec;

    @ConfigProperty(name = "app.cli.mode", defaultValue = "false")
    boolean isCliMode;

    public LoggingFormatingService() {
    }

    public void info(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(
                    CommandLine.Help.Ansi.AUTO.string(message));
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

    public void error(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(
                    CommandLine.Help.Ansi.AUTO.string(String.format(ANSI_ERROR, message)));
        } else {
            logger.error(message);
        }
    }

    public void setSpec(CommandLine.Model.CommandSpec spec) {
        this.spec = spec;
    }
}
