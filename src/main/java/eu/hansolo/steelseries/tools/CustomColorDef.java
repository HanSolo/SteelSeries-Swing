package eu.hansolo.steelseries.tools;

import java.awt.Color;


/**
 *
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public class CustomColorDef {

    public final Color COLOR;
    public final Color VERY_DARK;
    public final Color DARK;
    public final Color MEDIUM;
    public final Color NORMAL;
    public final Color LIGHT;
    public final Color LIGHTER;
    public final Color VERY_LIGHT;

    public CustomColorDef(final Color COLOR) {
        this.COLOR = COLOR;
        final float HUE = Color.RGBtoHSB(COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue(), null)[0];
        if (COLOR.getRed() == COLOR.getGreen() && COLOR.getRed() == COLOR.getBlue()) {
            VERY_DARK = Color.getHSBColor(HUE, 0.0f, 0.32f);
            DARK = Color.getHSBColor(HUE, 0.0f, 0.62f);
            MEDIUM = Color.getHSBColor(HUE, 0.0f, 0.74f);
            NORMAL = COLOR;
            LIGHT = Color.getHSBColor(HUE, 0.0f, 0.84f);
            LIGHTER = Color.getHSBColor(HUE, 0.0f, 0.94f);
            VERY_LIGHT = Color.getHSBColor(HUE, 0.0f, 1.0f);
        } else {
            VERY_DARK = Color.getHSBColor(HUE, 1.0f, 0.32f);
            DARK = Color.getHSBColor(HUE, 1.0f, 0.62f);
            MEDIUM = Color.getHSBColor(HUE, 1.0f, 0.74f);
            NORMAL = COLOR;
            LIGHT = Color.getHSBColor(HUE, 0.65f, 0.84f);
            LIGHTER = Color.getHSBColor(HUE, 0.33f, 0.94f);
            VERY_LIGHT = Color.getHSBColor(HUE, 0.15f, 1.0f);
        }
    }
}
