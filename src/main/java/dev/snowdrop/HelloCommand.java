/// usr/bin/env jbang “$0” “$@” ; exit $?
//SOURCES logging/PicocliColorHandler.java
//DEPS org.aesh:terminal-tty:3.2
//DEPS info.picocli:picocli:4.7.7
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager
package dev.snowdrop;

import dev.snowdrop.logging.PicocliColorHandler;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.jboss.logging.Logger;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.io.IOException;

@Command(name = "hello",
         mixinStandardHelpOptions = true,
         version = "1.0",
         description = "Enhanced Picocli + JBoss LogManager Integration")
public class HelloCommand implements Runnable {
    LogManager logManager = (LogManager) LogManager.getLogManager();
    Logger logger = Logger.getLogger(HelloCommand.class.getName());

    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        try {
            TerminalConnection tty = new TerminalConnection();
            var cap = TerminalColorDetector.detect(tty);

            int darken = cap.getTheme().isDark() ? 0 : 1;
            setupPicocliHandler(darken);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        logger.trace("This is a TRACE message.");
        logger.debug("This is a DEBUG message.");
        logger.info("This is a INFO message.");
        logger.warn("This is a WARNING message.");
        logger.error( "This is a ERROR message.");
        logger.fatal( "This is a FATAL message.");
    }

    private void setupPicocliHandler(int darken) throws IOException {
        PicocliColorHandler handler = new PicocliColorHandler(spec, darken);
        handler.setLevel(Level.TRACE);

        logManager.getLogger(HelloCommand.class.getName()).addHandler(handler);
        logManager.getLogger(HelloCommand.class.getName()).setLevel(Level.ALL);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new HelloCommand()).execute(args);
        System.exit(exitCode);
    }
}