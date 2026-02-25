/// usr/bin/env jbang “$0” “$@” ; exit $?
//SOURCES logging/PicocliColorHandler.java
//DEPS org.aesh:terminal-tty:3.2
//DEPS info.picocli:picocli:4.7.7
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager
package dev.snowdrop;

import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.jboss.logging.Logger;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.io.IOException;

@Command(name = "hello",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Picocli with JBoss LogManager")
public class ColorMsgPicocliApp implements Runnable {
    static LogManager logManager = (LogManager) LogManager.getLogManager();
    static Logger logger = Logger.getLogger(ColorMsgPicocliApp.class.getName());

    @Spec
    CommandSpec spec;

    @Option(names = {"-c","--color"}, negatable = true, defaultValue = "false",
            description = "Enable or disable colored output (default: ${DEFAULT-VALUE})")
    boolean color;

    @CommandLine.Option(names = {"-n", "--name"}, description = "Who will we greet?", defaultValue = "World")
    String name;

    @Override
    public void run() {

        if (color) {
            var darken = isTerminalDark();
            setupLogManagerAndHandler(darken);
            logger.infof("Theme of the terminal is: %s", darken == 0 ? "Dark" :"Light");
        } else {
            org.jboss.logmanager.Logger rootLogger = logManager.getLogger(ColorMsgPicocliApp.class.getName());
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
    }

    private void setupLogManagerAndHandler(int darken) {
        String logName = ColorMsgPicocliApp.class.getName();

        ColorHandler handler = new ColorHandler(spec, darken);
        handler.setLevel(Level.TRACE);
        logManager.getLogger(logName).setUseParentHandlers(false); // This is needed to avoid to log twice the messages
        logManager.getLogger(logName).addHandler(handler);
        logManager.getLogger(logName).setLevel(Level.ALL);
    }

    private static int isTerminalDark() {
        int darken = 0;
        try {
            long start = System.currentTimeMillis();
            TerminalConnection connection = new TerminalConnection();
            var cap = TerminalColorDetector.detect(connection);
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("Theme detection took: [%s ms]%n", elapsed);

            darken = cap.getTheme().isDark() ? 0 : 1;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return darken;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ColorMsgPicocliApp()).execute(args);
        System.exit(exitCode);
    }
}