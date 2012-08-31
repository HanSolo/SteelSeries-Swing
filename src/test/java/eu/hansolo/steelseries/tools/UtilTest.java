package eu.hansolo.steelseries.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Font;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author hansolo
 */
public class UtilTest {

    public UtilTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getScaledInstance method, of class Util.
     */
    @Test
    public void testGetScaledInstance() {
        System.out.println("getScaledInstance");
        BufferedImage IMAGE = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        int TARGET_WIDTH = 20;
        int TARGET_HEIGHT = 30;
        Object HINT = java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        Util instance = Util.INSTANCE;
        int expWidthResult = 20;
        int expHeightResult = 30;
        BufferedImage result = instance.getScaledInstance(IMAGE, TARGET_WIDTH, TARGET_HEIGHT, HINT);
        assertEquals(expWidthResult, result.getWidth());
        assertEquals(expHeightResult, result.getHeight());
    }

    /**
     * Test of setAlpha method, of class Util.
     */
    @Test
    public void testSetAlpha_Color_float() {
        System.out.println("setAlpha");
        Color COLOR = new Color(1.0f, 0.0f, 0.0f, 1.0f);
        float ALPHA = 0.5F;
        Util instance = Util.INSTANCE;
        int expResult = 128;
        int result = instance.setAlpha(COLOR, ALPHA).getAlpha();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAlpha method, of class Util.
     */
    @Test
    public void testSetAlpha_Color_int() {
        System.out.println("setAlpha");
        Color COLOR = new Color(255, 0, 0, 255);
        int ALPHA = 128;
        Util instance = Util.INSTANCE;
        int expResult = 128;
        int result = instance.setAlpha(COLOR, ALPHA).getAlpha();
        assertEquals(expResult, result);
    }

    /**
     * Test of getColorFromFraction method, of class Util.
     */
    @Test
    public void testGetColorFromFraction() {
        System.out.println("getColorFromFraction");
        Color SOURCE_COLOR = new Color(255, 0, 0);
        Color DESTINATION_COLOR = new Color(0, 255, 0);
        float CURRENT_FRACTION = 0.5f;
        Util instance = Util.INSTANCE;
        Color expResult = new Color(128, 128, 0);
        Color result = instance.interpolateColor(SOURCE_COLOR, DESTINATION_COLOR, CURRENT_FRACTION);
        assertEquals(expResult, result);
    }

    /**
     * Test of setBrightness method, of class Util.
     */
    @Test
    public void testSetBrightness() {
        System.out.println("setBrightness");
        Color COLOR = new Color(255, 0, 0);
        float BRIGHTNESS = 0.5F;
        Util instance = Util.INSTANCE;
        Color expResult = new Color(128, 0, 0);
        Color result = instance.setBrightness(COLOR, BRIGHTNESS);
        assertEquals(expResult, result);
    }

    /**
     * Test of setSaturation method, of class Util.
     */
    @Test
    public void testSetSaturation() {
        System.out.println("setSaturation");
        Color COLOR = new Color(255, 0, 0);
        float SATURATION = 0.5F;
        Util instance = Util.INSTANCE;
        Color expResult = new Color(255, 128, 128);
        Color result = instance.setSaturation(COLOR, SATURATION);
        assertEquals(expResult, result);
    }

    /**
     * Test of setHue method, of class Util.
     */
    @Test
    public void testSetHue() {
        System.out.println("setHue");
        Color COLOR = new Color(255, 0, 0);
        float HUE = 0.5F;
        Util instance = Util.INSTANCE;
        Color expResult = new Color(0, 255, 255);
        Color result = instance.setHue(COLOR, HUE);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDigitalFont method, of class Util.
     */
    @Test
    public void testGetDigitalFont() {
        System.out.println("getDigitalFont");
        Util instance = Util.INSTANCE;
        Font expResult;
        try {
            expResult = java.awt.Font.createFont(0, this.getClass().getResourceAsStream("/eu/hansolo/steelseries/resources/digital.ttf"));
            java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(expResult);
        } catch (java.awt.FontFormatException exception) {
            expResult = new java.awt.Font("Verdana", 1, 24);
        } catch (java.io.IOException exception) {
            expResult = new java.awt.Font("Verdana", 1, 24);
        }

        Font result = instance.getDigitalFont();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStandardFont method, of class Util.
     */
    @Test
    public void testGetStandardFont() {
        System.out.println("getStandardFont");
        Util instance = Util.INSTANCE;
        Font expResult = new java.awt.Font("Verdana", 1, 24);
        Font result = instance.getStandardFont();
        assertEquals(expResult, result);
    }
}
