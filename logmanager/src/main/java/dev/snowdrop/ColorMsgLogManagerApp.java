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
        setupLogManagerAndHandler();

        logger.infof("Theme of the terminal is: %s", isTerminalDark() == 0 ? "Dark" :"Light");

        logger.trace("This is a TRACE message.");
        logger.debug("This is a DEBUG message.");
        logger.info("This is a INFO message.");
        logger.warn("This is a WARNING message.");
        logger.error( "This is a ERROR message.");
        logger.fatal( "This is a FATAL message.");
    }

    private static void setupLogManagerAndHandler() throws IOException {
        String logName = ColorMsgLogManagerApp.class.getName();
        //listHandlers(logName);

        ColorHandler handler = new ColorHandler(isTerminalDark());
        handler.setLevel(Level.TRACE);
        logManager.getLogger(logName).setUseParentHandlers(false); // This is needed to avoid to log twice the messages
        logManager.getLogger(logName).addHandler(handler);
        logManager.getLogger(logName).setLevel(Level.ALL);
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

    private static void listHandlers(String loggerName) {
        Handler[] handlers = logManager.getLogger(loggerName).getHandlers();
        System.out.println("------ List the handlers ------");
        for(Handler h : handlers) {
            System.out.println(h);
        }
        System.out.println("------ end of list the handlers ------");

        handlers = logManager.getLogger(loggerName).getParent().getHandlers();
        System.out.println("------ List the parent handlers ------");
        for(Handler h : handlers) {
            System.out.println(h);
        }
        System.out.println("------ end of list the parent handlers ------");
    }
}
