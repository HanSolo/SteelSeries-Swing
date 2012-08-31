package eu.hansolo.steelseries.gauges;

import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.NumberSystem;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;


/**
 *
 * @author hansolo
 */
public interface Lcd {

    public boolean isValueCoupled();

    public void setValueCoupled(final boolean VALUE_COUPLED);

    public double getLcdValue();

    public void setLcdValue(final double VALUE);

    public void setLcdValueAnimated(final double VALUE);

    public double getLcdThreshold();

    public void setLcdThreshold(final double LCD_THRESHOLD);

    public boolean isLcdThresholdVisible();

    public void setLcdThresholdVisible(final boolean LCD_THRESHOLD_VISIBLE);

    public boolean isLcdThresholdBehaviourInverted();

    public void setLcdThresholdBehaviourInverted(final boolean LCD_THRESHOLD_BEHAVIOUR_INVERTED);

    public boolean isLcdBlinking();

    public void setLcdBlinking(final boolean LCD_BLINKING);

    public int getLcdDecimals();

    public void setLcdDecimals(final int DECIMALS);

    public String getLcdUnitString();

    public void setLcdUnitString(final String UNIT);

    public boolean isLcdUnitStringVisible();

    public void setLcdUnitStringVisible(final boolean UNIT_STRING_VISIBLE);

    public boolean isCustomLcdUnitFontEnabled();

    public void setCustomLcdUnitFontEnabled(final boolean USE_CUSTOM_LCD_UNIT_FONT);

    public Font getCustomLcdUnitFont();

    public void setCustomLcdUnitFont(final Font CUSTOM_LCD_UNIT_FONT);

    public String getLcdInfoString();

    public void setLcdInfoString(final String INFO);

    public Font getLcdInfoFont();

    public void setLcdInfoFont(final Font LCD_INFO_FONT);

    public boolean isDigitalFont();

    public void setDigitalFont(final boolean DIGITAL_FONT);

    public LcdColor getLcdColor();

    public void setLcdColor(final LcdColor COLOR);

    public Paint getCustomLcdBackground();

    public void setCustomLcdBackground(final Paint CUSTOM_LCD_BACKGROUND);

    public Paint createCustomLcdBackgroundPaint(final Color[] LCD_COLORS);

    public boolean isLcdBackgroundVisible();

    public void setLcdBackgroundVisible(final boolean LCD_BACKGROUND_VISIBLE);

    public Color getCustomLcdForeground();

    public void setCustomLcdForeground(final Color CUSTOM_LCD_FOREGROUND);

    public String formatLcdValue(final double VALUE);

    public boolean isLcdScientificFormat();

    public void setLcdScientificFormat(final boolean LCD_SCIENTIFIC_FORMAT);

    public Font getLcdValueFont();

    public void setLcdValueFont(final Font LCD_VALUE_FONT);

    public Font getLcdUnitFont();

    public void setLcdUnitFont(final Font LCD_UNIT_FONT);

    public NumberSystem getLcdNumberSystem();

    public void setLcdNumberSystem(final NumberSystem NUMBER_SYSTEM);

    public Rectangle getLcdBounds();
}
