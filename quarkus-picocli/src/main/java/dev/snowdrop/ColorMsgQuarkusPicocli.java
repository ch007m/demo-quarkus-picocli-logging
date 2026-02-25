/// usr/bin/env jbang “$0” “$@” ; exit $?
//SOURCES logging/PicocliColorHandler.java
//DEPS org.aesh:terminal-tty:3.2
//DEPS info.picocli:picocli:4.7.7
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager
package dev.snowdrop;

import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;
import org.jboss.logmanager.formatters.ColorPatternFormatter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import jakarta.enterprise.context.Dependent;

import java.io.IOException;

@Command(name = "greeting",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Quarkus Picocli and JBoss LogManager")
public class ColorMsgQuarkusPicocli implements Runnable {
    static LogManager logManager = (LogManager) LogManager.getLogManager();
    static Logger logger = Logger.getLogger(ColorMsgQuarkusPicocli.class.getName());

    @ConfigProperty(name = "cli.log.msg.format")
    String logMsgFormat;

    @Spec
    CommandSpec spec;

    @Option(names = {"-c", "--color"}, negatable = true, defaultValue = "false",
            description = "Enable or disable colored output (default: ${DEFAULT-VALUE})")
    boolean color;

    @CommandLine.Option(names = {"-n", "--name"}, description = "Who will we greet?", defaultValue = "World")
    String name;

    private final GreetingService greetingService;

    public ColorMsgQuarkusPicocli(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @Override
    public void run() {
        if (color) {
            setupPicocliHandler(isTerminalDark());
        } else {
            org.jboss.logmanager.Logger rootLogger = logManager.getLogger(ColorMsgQuarkusPicocli.class.getName());
            rootLogger.setLevel(Level.ALL);
            rootLogger.setUseParentHandlers(true);
        }

        logger.trace("Hello " + name + "! This is a TRACE message.");
        logger.debug("Hello " + name + "! This is a DEBUG message.");
        // Messages logged with the following levels
        logger.info("Hello " + name + "! This is an INFO message.");
        logger.warn("Hello " + name + "! This is a WARNING message.");
        logger.error("Hello " + name + "! This is an ERROR message.");
        logger.fatal("Hello " + name + "! This is a FATAL message.");

        greetingService.sayHello(name);
    }

    private void setupPicocliHandler(int darken) {
        ColorHandler handler = new ColorHandler(spec, darken);
        handler.setLevel(Level.TRACE);
        handler.setFormatter(new ColorPatternFormatter(darken, logMsgFormat));

        logManager.getLogger(ColorMsgQuarkusPicocli.class.getName()).addHandler(handler);
        logManager.getLogger(ColorMsgQuarkusPicocli.class.getName()).setLevel(Level.ALL);
    }

    private static int isTerminalDark() {
        int darken = 0;
        try {
            TerminalConnection tty = new TerminalConnection();
            var cap = TerminalColorDetector.detect(tty);

            darken = cap.getTheme().isDark() ? 0 : 1;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return darken;
    }
}

@Dependent
class GreetingService {
    void sayHello(String name) {
        System.out.println("Hello " + name + "!");
    }
}