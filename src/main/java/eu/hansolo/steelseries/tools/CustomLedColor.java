package eu.hansolo.steelseries.tools;

import java.awt.Color;


/**
 *
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public class CustomLedColor {

    public final Color COLOR;
    public final Color INNER_COLOR1_ON;
    public final Color INNER_COLOR2_ON;
    public final Color OUTER_COLOR_ON;
    public final Color CORONA_COLOR;
    public final Color INNER_COLOR1_OFF;
    public final Color INNER_COLOR2_OFF;
    public final Color OUTER_COLOR_OFF;

    public CustomLedColor(final Color COLOR) {
        this.COLOR = COLOR;
        final float HUE = Color.RGBtoHSB(COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue(), null)[0];
        if (COLOR.getRed() == COLOR.getGreen() && COLOR.getRed() == COLOR.getBlue()) {
            INNER_COLOR1_ON = Color.getHSBColor(HUE, 0.0f, 1.0f);
            INNER_COLOR2_ON = Color.getHSBColor(HUE, 0.0f, 1.0f);
            OUTER_COLOR_ON = Color.getHSBColor(HUE, 0.0f, 0.99f);
            CORONA_COLOR = Color.getHSBColor(HUE, 0.0f, 1.00f);
            INNER_COLOR1_OFF = Color.getHSBColor(HUE, 0.0f, 0.35f);
            INNER_COLOR2_OFF = Color.getHSBColor(HUE, 0.0f, 0.35f);
            OUTER_COLOR_OFF = Color.getHSBColor(HUE, 0.0f, 0.26f);
        } else {
            INNER_COLOR1_ON = Color.getHSBColor(HUE, 0.75f, 1.0f);
            INNER_COLOR2_ON = Color.getHSBColor(HUE, 0.75f, 1.0f);
            OUTER_COLOR_ON = Color.getHSBColor(HUE, 1.0f, 0.99f);
            CORONA_COLOR = Color.getHSBColor(HUE, 0.75f, 1.00f);
            INNER_COLOR1_OFF = Color.getHSBColor(HUE, 1.0f, 0.35f);
            INNER_COLOR2_OFF = Color.getHSBColor(HUE, 1.0f, 0.35f);
            OUTER_COLOR_OFF = Color.getHSBColor(HUE, 1.0f, 0.26f);
        }
    }
}
