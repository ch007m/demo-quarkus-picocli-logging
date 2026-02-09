/// usr/bin/env jbang "$0" "$@"; exit $?
//DEPS org.aesh:terminal-tty:3.1

package dev.snowdrop.ansi;

import org.aesh.terminal.Connection;
import org.aesh.terminal.tty.TerminalConnection;

import java.util.Arrays;

public class QueryOSC {
    private static Connection connection;

    public static void main(String[] args) throws Exception {
        int oscCommandCode = 4;

        connection = new TerminalConnection();
        connection.openNonBlocking();

        boolean oscSupported = connection.supportsOscQueries();
        System.out.println("-".repeat(80));
        System.out.println("is OSC supported: " + oscSupported);
        System.out.println("Theme : " + connection.getColorCapability().getTheme());
        System.out.println("-".repeat(80));

        if (oscSupported) {
            long startTime = System.nanoTime();
            for (int colorCode : Arrays.asList(0,1,2,3,4,5,6,7,8,9)) {
                int[] rgb = connection.queryPaletteColor(colorCode, 15);
                System.out.printf("Color code: %-3d - rgb(%d,%d,%d)%n", colorCode, rgb[0], rgb[1], rgb[2]);
            }
            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
            System.out.println("Loop executed in " + elapsedMs + " ms");
        }
    }
}
