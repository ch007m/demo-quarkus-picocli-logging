package dev.snowdrop.logging;

import dev.snowdrop.logging.util.AnsiBuilder;
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

    public LoggingService() {
    }

    public void info(String message) {
        if (isCliMode) {
            String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            spec.commandLine().getOut().println(
                        new AnsiBuilder()
                            .add(timestamp, "WHITEDIM")
                            .add("INFO", "GREEN")
                            .add(message, "BRIGHTWHITE")
                            .build());
        } else {
            logger.info(message);
        }
    }

    public void warn(String message) {
        if (isCliMode) {
            String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            spec.commandLine().getOut().println(
                        new AnsiBuilder()
                            .add(timestamp, "WHITEDIM")
                            .add("WARN","YELLOW")
                            .add(message,"BRIGHTWHITE")
                            .build());
        } else {
            logger.warn(message);
        }
    }

    public void error(String s) {
        this.error(s, null);
    }

    public void error(String message, Throwable e) {
        if (isCliMode) {
            String timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            spec.commandLine().getOut().println(
                new AnsiBuilder()
                    .add(timestamp, "WHITEDIM")
                    .add("ERROR","RED")
                    .add(message,"BRIGHTWHITE")
                    .build());
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

}
