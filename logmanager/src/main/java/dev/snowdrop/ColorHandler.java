package dev.snowdrop;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.formatters.ColorPatternFormatter;

import java.io.PrintWriter;

/**
 * Color log handler
 */
public class ColorHandler extends ExtHandler {

    private final ColorPatternFormatter formatter;
    private PrintWriter out;
    private PrintWriter err;

    /**
     * Creates a new handler with the given command spec and darken level.
     *
     * @param darken the darken level for the color formatter
     */
    public ColorHandler(int darken) {
        this.formatter = new ColorPatternFormatter(darken, "%d{HH:mm:ss} %-5p [%c{2.}] (%t) %s%e%n");
        out = new PrintWriter(System.out);
        err = new PrintWriter(System.err);
    }

    @Override
    protected void doPublish(ExtLogRecord record) {

        int recordLevel = record.getLevel().intValue();
        int loggerLevel = org.jboss.logmanager.Level.ERROR.intValue();

        PrintWriter writer = recordLevel >= loggerLevel ? err : out;

        writer.print(formatter.format(record));
        writer.flush();
    }
}