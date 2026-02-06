///usr/bin/env jbang "$0" "$@"; exit $?
//DEPS org.aesh:terminal-tty:3.0

package dev.snowdrop.ansi;

import org.aesh.terminal.Connection;
import org.aesh.terminal.tty.TerminalConnection;
import org.aesh.terminal.utils.ANSIBuilder;
import org.aesh.terminal.utils.TerminalColorCapability;
import org.aesh.terminal.utils.TerminalTheme;

import java.io.IOException;

/**
 * Visualizes HSL Colors across a 360-degree Hue wheel.
 *
 *  * HSB or HSV where v= value or brightness vs HSL where L is lightness : https://www.computerhope.com/jargon/h/hsb.htm
 *  * <p>
 *  * With HSL, the H (hue) is placed on a RGB (Red, Green, and Blue) color wheel, where 0° is red, 60° is yellow, 120° is green, 240° is blue, and 300° is magenta.
 *  * If you were to go around the wheel (360° the maximum value), it would be back to red.
 *  * <p>
 *  * The S (saturation) starts at gray at 0% with a lightness of 50%, which is no additional white or black.
 *  * The gray is removed from the color as the saturation increases to 100%.
 *  * <p>
 *  * Starting the L (lightness) of 50% gives the color the most vivid color as it has no additional white or black added to the color.
 *  * Decreasing it below 50% makes the color lighter by adding more white. Increasing it above 50% makes the color darker by adding black.
 *  * <p>
 *  * Below is an example of how an HSL value for the color red is written in HTML using a CSS style.
 *  *
 *  * <p style="color:hsl(0deg 100.00% 50.00%);">Red text.</p>
 *  * In the code above, the HSL value starts at 0° in the hue color wheel, with a 100% saturation (no gray), for pure red, and a 50% lightness (no additional white or black).
 *  * <p>
 *
 */
public class ColorHSL {

    public static void main(String[] args) throws IOException {
        try (Connection connection = new TerminalConnection()) {
            connection.openNonBlocking();
            TerminalColorCapability cap = TerminalColorCapability.builder().build();

            TerminalTheme theme = connection.getColorCapability().getTheme();
            boolean isDark = (theme == TerminalTheme.DARK);

            System.out.println("-".repeat(50));
            System.out.println("Colors supported: " + connection.getColorCapability().getColorDepth());
            System.out.printf("Theme detected: %s%n",theme);
            System.out.printf("Is Theme dark: %s%n",isDark);

            System.out.println("-".repeat(50));
            System.out.println("HSL Color Wheel Visualization");
            System.out.println("-".repeat(50));

            // Base lightness for Dark Mode (High value to pop on black)
            int darkL = 65;
            int lightL = 100 - darkL;
            int activeL = isDark ? darkL : lightL;

            // Saturation levels: 50% (Muted) -> 100% (Vivid)
            int[] saturations = {80};

            for (int s : saturations) {
                System.out.println("Hue  | Color Sample Output");
                System.out.println("-----|--------------------------------------------");

                for (int h = 0; h <= 360; h += 20) {
                    int[] rgb = hslToRgb(h, s, activeL);

                    // Build the preview string
                    String sample = ANSIBuilder.builder(cap)
                        .rgb(rgb[0], rgb[1], rgb[2])
                        .append(String.format(" H:%3d° S:%2d%% L:%2d%% - This is a Log Message", h, s, activeL))
                        .toString();

                    System.out.printf("%3d° | %s%n", h, sample);
                }
            }
        }
    }

    /**
     * Converts HSL values to RGB array.
     * h [0, 360], s [0, 100], l [0, 100]
     */
    private static int[] hslToRgb(float h, float s, float l) {
        s /= 100f;
        l /= 100f;
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60f) % 2 - 1));
        float m = l - c / 2;
        float r = 0, g = 0, b = 0;

        if (h < 60) { r = c; g = x; }
        else if (h < 120) { r = x; g = c; }
        else if (h < 180) { g = c; b = x; }
        else if (h < 240) { g = x; b = c; }
        else if (h < 300) { r = x; b = c; }
        else { r = c; b = x; }

        return new int[]{
            (int) ((r + m) * 255),
            (int) ((g + m) * 255),
            (int) ((b + m) * 255)
        };
    }
}