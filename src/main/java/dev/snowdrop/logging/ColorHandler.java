package dev.snowdrop.logging;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;
import org.jboss.logmanager.formatters.ColorPatternFormatter;
import picocli.CommandLine;

import java.io.PrintWriter;

/**
 * Color log handler
 */
public class ColorHandler extends ExtHandler {

    private final ColorPatternFormatter formatter;

    /**
     * Creates a new handler with the given command spec and darken level.
     *
     * @param darken the darken level for the color formatter
     */
    public ColorHandler(int darken) {
        this.formatter = new ColorPatternFormatter(darken, "%d{HH:mm:ss} %-5p [%c{2.}] (%t) %s%e%n");
    }

    @Override
    protected void doPublish(ExtLogRecord record) {
        PrintWriter out = new PrintWriter(System.out);
        PrintWriter err = new PrintWriter(System.err);
        PrintWriter writer = (record.getLevel().intValue() >= org.jboss.logmanager.Level.ERROR.intValue())
                ? err : out;

        writer.print(formatter.format(record));
        writer.flush();
    }
}