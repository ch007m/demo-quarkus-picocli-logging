package dev.snowdrop.logging;

import dev.snowdrop.logging.util.AnsiBuilder;
import dev.snowdrop.logging.util.LEVEL;
import dev.snowdrop.service.MessageService;
import jakarta.enterprise.context.ApplicationScoped;
import org.aesh.terminal.tty.TerminalColorDetector;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSI;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.ColorDepth;
import org.aesh.terminal.utils.TerminalColorCapability;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import picocli.CommandLine;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class LoggingService {
    private static final Logger logger = Logger.getLogger(MessageService.class);

    private CommandLine.Model.CommandSpec spec;
    private TerminalColorCapability cap;

    @ConfigProperty(name = "cli.mode", defaultValue = "false")
    boolean isCliMode;

    @ConfigProperty(name = "cli.logging.verbose", defaultValue = "false")
    boolean isVerbose;

    @ConfigProperty(name = "cli.logging.colored", defaultValue = "true")
    boolean useAnsiColoredMsg;
    
    private final static String TIMESTAMP_COLOR = "TIMESTAMP";
    private final static String MESSAGE_COLOR = "MESSAGE";
    private final static String SPACE = " ";

    // CommandLine.Help.ColorScheme colorScheme;

    public LoggingService() {
    }

    public void setupAesh() {
        try {
            TerminalConnection connection = new TerminalConnection();
            // Using the default color code and check if the theme is dark or light
            // cap = TerminalColorDetector.detect(connection);

            // Override the colors
            cap = TerminalColorCapability.builder()
            .colorDepth(ColorDepth.TRUE_COLOR)
            .infoCode(34)
            .warningCode(33)
            .errorCode(167)
            .debugCode(244)
            .traceCode(90)
            .messageCode(31)
            .timestampCode(244) // 16-colors Grey: 37, Light Grey: 90; True colors: 244
            .build();

            printCapability(connection,"   ",cap);
            demonstrateColors(connection,cap);

            connection.close();
        } catch (IOException e) {
            System.err.println("Error creating terminal connection: " + e.getMessage());
            System.exit(1);
        }
    }

    public void info(String message) {
        // TODO: To be investigate to see if this is easier to use picocli Ansi and the colorScheme = spec.commandLine().getColorScheme();
        if (isCliMode) {
            //spec.commandLine().getOut().println(colorScheme.ansi().new Text("#" + message,colorScheme));
            var formatedMessage = newFormatedMessage(LEVEL.INFO, cap, message);
            spec.commandLine().getOut().println(formatedMessage);
        } else {
            logger.info(message);
        }
    }

    public void warn(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.WARN, cap, message));
        } else {
            logger.warn(message);
        }
    }

    public void debug(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.DEBUG, cap, message));
        } else {
            logger.debug(message);
        }
    }

    public void trace(String message) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.TRACE, cap, message));
        } else {
            logger.trace(message);
        }
    }

    public void error(String s) {
        this.error(s, null);
    }

    public void error(String message, Throwable e) {
        if (isCliMode) {
            spec.commandLine().getOut().println(newFormatedMessage(LEVEL.ERROR, cap, message));
            if (e != null && isVerbose) {
                //e.printStackTrace(spec.commandLine().getErr());
                java.io.StringWriter sw = new java.io.StringWriter();
                e.printStackTrace(new java.io.PrintWriter(sw));
                String stackTrace = sw.toString();
                spec.commandLine().getErr().println(newFormatedMessage(LEVEL.ERROR, cap, stackTrace, true));
            }
        } else {
            if (isVerbose && e != null) {
                logger.error(message, e);
            } else {
                logger.error(message);
            }
        }
    }

    public void setSpec(CommandLine.Model.CommandSpec spec) {
        this.spec = spec;
    }

    public String formatedMessage(LEVEL level, String message) {
        String timeStamp = OffsetDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (useAnsiColoredMsg) {
            AnsiBuilder builder = new AnsiBuilder()
                .add(timeStamp, TIMESTAMP_COLOR)
                .add(level, level.getColor())
                .add(message, MESSAGE_COLOR);
            return builder.build();
        } else {
            return String.format("%s %s %s", timeStamp, level.toString(), message);
        }
    }

    public String newFormatedMessage(LEVEL level, TerminalColorCapability cap, String message) {
        return newFormatedMessage(level, cap, message,false);
    }

    public String newFormatedMessage(LEVEL level, TerminalColorCapability cap, String message, boolean isStackTrace) {
        String timeStamp = OffsetDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (useAnsiColoredMsg) {
            // Get the appropriate color code based on the level
            int levelColorCode = switch (level) {
                case ERROR -> cap.getSuggestedErrorCode();
                case WARN -> cap.getSuggestedWarningCode();
                case INFO -> cap.getSuggestedInfoCode();
                case DEBUG -> cap.getSuggestedDebugCode();
                case TRACE -> cap.getSuggestedTraceCode();
            };

            ANSIBuilder builder = ANSIBuilder.builder(cap)
                .timestamp(timeStamp)
                .append(SPACE)
                .color256(levelColorCode, level.toString())
                .append(SPACE)
                .color256(cap.getSuggestedMessageCode(),message);

            /* Old code
            AnsiBuilder builder = new AnsiBuilder()
                .add(timeStamp, cap.getSuggestedTimestampCode())
                .add(level, levelColorCode)
                .add(message, isStackTrace ? cap.getSuggestedErrorCode() : cap.getSuggestedMessageCode());
             */

            return builder.toString();
        } else {
            return String.format("%s %s %s", timeStamp, level.toString(), message);
        }
    }

    /**
     * Prints the terminal color capability information.
     *
     * @param connection the terminal connection to use for output
     * @param indent the indentation string to prefix each line
     * @param cap the terminal color capability to print
     */
    private void printCapability(TerminalConnection connection, String indent, TerminalColorCapability cap) {
        connection.write(indent + "Color Depth: " + cap.getColorDepth() +
            " (" + cap.getColorDepth().getColorCount() + " colors)\n");
        connection.write(indent + "Theme:       " + cap.getTheme() +
            (cap.getTheme().isDark() ? " (using light text colors)" : " (using dark text colors)") + "\n");

        if (cap.hasBackgroundColor()) {
            int[] bg = cap.getBackgroundRGB();
            connection.write(indent + "Background:  " + cap.getBackgroundHex() +
                " RGB(" + bg[0] + ", " + bg[1] + ", " + bg[2] + ") " +
                formatColorSwatch(bg) + "\n");
        } else {
            connection.write(indent + "Background:  Not detected\n");
        }

        if (cap.hasForegroundColor()) {
            int[] fg = cap.getForegroundRGB();
            connection.write(indent + "Foreground:  " + cap.getForegroundHex() +
                " RGB(" + fg[0] + ", " + fg[1] + ", " + fg[2] + ") " +
                formatColorSwatch(fg) + "\n");
        } else {
            connection.write(indent + "Foreground:  Not detected\n");
        }
    }

    /**
     * Formats a color swatch using 24-bit color escape sequences.
     *
     * @param rgb the RGB color values as an array of three integers
     * @return an ANSI escape sequence that displays a colored block
     */
    private String formatColorSwatch(int[] rgb) {
        // Create a colored block using 24-bit color if available
        return "\u001B[48;2;" + rgb[0] + ";" + rgb[1] + ";" + rgb[2] + "m    " + ANSI.RESET;
    }

    /**
     * Demonstrates suggested colors for various message types based on terminal theme.
     *
     * @param connection the terminal connection to use for output
     * @param cap the terminal color capability containing theme information
     */
    private void demonstrateColors(TerminalConnection connection, TerminalColorCapability cap) {
        String indent = "   ";

        // Normal text
        int fg = cap.getSuggestedForegroundCode();
        connection.write(indent + "\u001B[" + fg + "mForeground color based on theme - Text (code " + fg + ")" + ANSI.RESET + "\n");

        // Error
        int error = cap.getSuggestedErrorCode();
        connection.write(indent + "\u001B[" + error + "mError message (code " + error + ")" + ANSI.RESET + "\n");

        // Success
        int success = cap.getSuggestedSuccessCode();
        connection.write(indent + "\u001B[" + success + "mSuccess message (code " + success + ")" + ANSI.RESET + "\n");

        // Warning
        int warning = cap.getSuggestedWarningCode();
        connection.write(indent + "\u001B[" + warning + "mWarning message (code " + warning + ")" + ANSI.RESET + "\n");

        // Info
        int info = cap.getSuggestedInfoCode();
        connection.write(indent + "\u001B[" + info + "mInfo message (code " + info + ")" + ANSI.RESET + "\n");
    }

}
