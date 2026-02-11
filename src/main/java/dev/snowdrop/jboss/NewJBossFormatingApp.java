/// usr/bin/env jbang “$0” “$@” ; exit $?
//DEPS org.jboss.logmanager:jboss-logmanager:3.2.1.Final-SNAPSHOT
//DEPS org.aesh:terminal-tty:3.1
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager

package dev.snowdrop.jboss;

import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.TerminalColorCapability;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.formatters.ColorPatternFormatter;

import java.io.IOException;
import java.util.List;

public class NewJBossFormatingApp {

    public static void main(String[] args) throws IOException, InterruptedException {
        TerminalColorCapability cap;
        try {
            long start = System.nanoTime();

            TerminalConnection connection = new TerminalConnection();
            connection.openNonBlocking();
            cap = TerminalColorDetector.detect(connection);

            long elapsed = System.nanoTime() - start;
            double ms = elapsed / 1_000_000.0;

            System.out.printf("Theme: %s%n",cap.getTheme());
            System.out.printf("Theme detection took: [%.2f ms]%n",ms);

            int darken;
            if (cap.getTheme().isDark()) {
                darken = 0;
            } else {
                darken = 1;
            }

            ColorPatternFormatter fmt = new ColorPatternFormatter(darken, "%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n");
            for (java.util.logging.Level l : List.of(
                    java.util.logging.Level.OFF,
                    org.jboss.logmanager.Level.FATAL,
                    java.util.logging.Level.SEVERE,
                    org.jboss.logmanager.Level.ERROR,
                    java.util.logging.Level.WARNING,
                    org.jboss.logmanager.Level.WARN,
                    java.util.logging.Level.INFO,
                    org.jboss.logmanager.Level.INFO,
                    java.util.logging.Level.CONFIG,
                    org.jboss.logmanager.Level.DEBUG,
                    java.util.logging.Level.FINE,
                    org.jboss.logmanager.Level.TRACE,
                    java.util.logging.Level.FINER,
                    java.util.logging.Level.FINEST,
                    java.util.logging.Level.ALL)) {
                ExtLogRecord record = new ExtLogRecord(
                        l, "Testing level %s %s", ExtLogRecord.FormatStyle.PRINTF,
                        NewJBossFormatingApp.class.getName());
                record.setLoggerName("com.acme.logger");
                record.setParameters(new Object[]{Class.class, "Some text"});
                System.out.print(fmt.format(record));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
