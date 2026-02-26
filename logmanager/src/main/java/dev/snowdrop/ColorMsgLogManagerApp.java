package dev.snowdrop;

import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.jboss.logging.Logger;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;

import java.io.IOException;
import java.util.logging.Handler;

public class ColorMsgLogManagerApp {
    final static LogManager logManager = (LogManager) LogManager.getLogManager();
    final static Logger logger = Logger.getLogger(ColorMsgLogManagerApp.class.getName());

    public static void main(String[] args) throws IOException {
        var darken = isTerminalDark();
        setupLogManagerAndHandler(darken);

        logger.infof("Theme of the terminal is: %s", darken == 0 ? "Dark" :"Light");

        logger.trace("This is a TRACE message.");
        logger.debug("This is a DEBUG message.");
        logger.info("This is a INFO message.");
        logger.warn("This is a WARNING message.");
        logger.error( "This is a ERROR message.");
        logger.fatal( "This is a FATAL message.");
    }

    private static void setupLogManagerAndHandler(int darken) throws IOException {
        String logName = ColorMsgLogManagerApp.class.getName();

        // Remove from the Root Logger the handlers to avoid to get the messages twice as by default a console handler is created
        // https://issues.redhat.com/browse/LOGMGR-369
        // Clean up handlers on the root logger
        final var rootLogger = logManager.getLogger("");
        for (var handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        ColorHandler handler = new ColorHandler(darken);
        handler.setLevel(Level.TRACE);
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
}
