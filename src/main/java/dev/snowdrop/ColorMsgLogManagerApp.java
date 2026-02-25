package dev.snowdrop;

import dev.snowdrop.logging.ColorHandler;
import dev.snowdrop.logging.PicocliColorHandler;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.jboss.logging.Logger;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.LogManager;

import java.io.IOException;

public class ColorMsgLogManagerApp {
    static LogManager logManager = (LogManager) LogManager.getLogManager();
    static Logger logger = Logger.getLogger(ColorMsgLogManagerApp.class.getName());

    public static void main(String[] args) throws IOException {
        setupLogManagerAndHandler(isTerminalDark());

        logger.trace("This is a TRACE message.");
        logger.debug("This is a DEBUG message.");
        logger.info("This is a INFO message.");
        logger.warn("This is a WARNING message.");
        logger.error( "This is a ERROR message.");
        logger.fatal( "This is a FATAL message.");
    }

    private static void setupLogManagerAndHandler(int darken) throws IOException {
        ColorHandler handler = new ColorHandler(isTerminalDark());
        handler.setLevel(Level.TRACE);

        logManager.getLogger(HelloCommand.class.getName()).addHandler(handler);
        logManager.getLogger(HelloCommand.class.getName()).setLevel(Level.ALL);
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
