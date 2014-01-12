/*
 * Copyright (c) 2012, Gerrit Grunwald, Klaus Rheinwald
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * The names of its contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.hansolo.steelseries.extras;

import eu.hansolo.steelseries.gauges.AbstractGauge;
import eu.hansolo.steelseries.gauges.AbstractRadial;
import eu.hansolo.steelseries.tools.LcdColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;


/**
 * @author Gerrit Grunwald <han.solo at muenster.de>
 * @author Klaus Rheinwald <klaus at rheinwald.info>
 */
public class StopWatch extends AbstractRadial implements ActionListener {
    // <editor-fold defaultstate="collapsed" desc="Variable declaration">
    private static final double ANGLE_STEP = 6;
    private final Timer CLOCK_TIMER;
    private double minutePointerAngle = 0;
    private double secondPointerAngle = 0;
    // Background
    private final Point2D MAIN_CENTER = new Point2D.Double();
    private final Point2D SMALL_CENTER = new Point2D.Double();
    private final Rectangle2D LCD = new Rectangle2D.Double();
    // Images used to combine layers for background and foreground
    private BufferedImage backgroundImage;
    private BufferedImage foregroundImage;
    private BufferedImage smallTickmarkImage;
    private BufferedImage mainPointerImage;
    private BufferedImage mainPointerShadowImage;
    private BufferedImage smallPointerImage;
    private BufferedImage smallPointerShadowImage;
    private BufferedImage lcdThresholdImage;
    private BufferedImage disabledImage;
    private long start = 0;
    private long minutes = 0;
    private long seconds = 0;
    private long milliSeconds = 0;
    private boolean running = false;
    private boolean flatNeedle = false;
    private final Color SHADOW_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.65f);
    private final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);
    private TextLayout unitLayout;
    private final Rectangle2D UNIT_BOUNDARY = new Rectangle2D.Double();
    private double unitStringWidth;
    private TextLayout valueLayout;
    private final Rectangle2D VALUE_BOUNDARY = new Rectangle2D.Double();
    private TextLayout infoLayout;
    private final Rectangle2D INFO_BOUNDARY = new Rectangle2D.Double();
    // </editor-fold>
    private long time;

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public StopWatch() {
        super();
        setLcdUnitString("s");
        CLOCK_TIMER = new Timer(100, this);
        init(getInnerBounds().width, getInnerBounds().height);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Initialization">
    @Override
    public AbstractGauge init(final int WIDTH, final int HEIGHT) {
        final int GAUGE_WIDTH = isFrameVisible() ? WIDTH : getGaugeBounds().width;
        final int GAUGE_HEIGHT = isFrameVisible() ? HEIGHT : getGaugeBounds().height;

        if (GAUGE_WIDTH <= 1 || GAUGE_HEIGHT <= 1) {
            return this;
        }

        if (isLcdVisible()) {
            if (isDigitalFont()) {
                setLcdValueFont(getModel().getDigitalBaseFont().deriveFont(0.55f * GAUGE_WIDTH * 0.15f));
            } else {
                setLcdValueFont(getModel().getStandardBaseFont().deriveFont(0.5f * GAUGE_WIDTH * 0.15f));
            }

            if (isCustomLcdUnitFontEnabled()) {
                setLcdUnitFont(getCustomLcdUnitFont().deriveFont(0.25f * GAUGE_WIDTH * 0.15f));
            } else {
                setLcdUnitFont(getModel().getStandardBaseFont().deriveFont(0.25f * GAUGE_WIDTH * 0.15f));
            }

            setLcdInfoFont(getModel().getStandardInfoFont().deriveFont(0.15f * GAUGE_WIDTH * 0.15f));
        }
        
        if (!isFrameVisible()) {
            setFramelessOffset(-getInnerBounds().width * 0.0841121495, -getInnerBounds().width * 0.0841121495);
        } else {
            setFramelessOffset(getInnerBounds().x, getInnerBounds().y);
        }

        // Create Background Image
        if (backgroundImage != null) {
            backgroundImage.flush();
        }
        backgroundImage = UTIL.createImage(GAUGE_WIDTH, GAUGE_WIDTH, Transparency.TRANSLUCENT);

        // Create Foreground Image
        if (foregroundImage != null) {
            foregroundImage.flush();
        }

        foregroundImage = UTIL.createImage(GAUGE_WIDTH, GAUGE_WIDTH, Transparency.TRANSLUCENT);
        
        if (isForegroundVisible()) {
            switch (getFrameType()) {
                case SQUARE:
                    FOREGROUND_FACTORY.createLinearForeground(GAUGE_WIDTH, GAUGE_WIDTH, false, backgroundImage);
                    break;

                case ROUND:

                default:
                    FOREGROUND_FACTORY.createRadialForeground(GAUGE_WIDTH, false, getForegroundType(), foregroundImage);
                    break;
            }
        }
        
        if (isFrameVisible()) {
            switch (getFrameType()) {
                case SQUARE:
                    FRAME_FACTORY.createLinearFrame(GAUGE_WIDTH, GAUGE_WIDTH, getFrameDesign(), getCustomFrameDesign(), getFrameEffect(), backgroundImage);
                    break;
                case ROUND:

                default:
                    FRAME_FACTORY.createRadialFrame(GAUGE_WIDTH, getFrameDesign(), getCustomFrameDesign(), getFrameEffect(), backgroundImage);
                    break;
            }
        }

        if (isBackgroundVisible()) {
            switch (getFrameType()) {
                case SQUARE:
                    BACKGROUND_FACTORY.createLinearBackground(WIDTH, WIDTH, getBackgroundColor(), getCustomBackground(), getModel().getTextureColor(), backgroundImage);
                    break;
                case ROUND:

                default:
                    BACKGROUND_FACTORY.createRadialBackground(WIDTH, getBackgroundColor(), getCustomBackground(), getModel().getTextureColor(), backgroundImage);
                    break;
            }
        }

        if (isLcdVisible()) {
            createLcdImage(new Rectangle2D.Double(((getGaugeBounds().width - GAUGE_WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.55), (GAUGE_WIDTH * 0.4), (GAUGE_WIDTH * 0.1)), getLcdColor(), getCustomLcdBackground(), backgroundImage);
            LCD.setRect(((getGaugeBounds().width - GAUGE_WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.555), GAUGE_WIDTH * 0.4, GAUGE_WIDTH * 0.1);
        }

        create_TICKMARKS_Image(GAUGE_WIDTH, 60f, 0.075, 0.14, backgroundImage);

        if (smallTickmarkImage != null) {
            smallTickmarkImage.flush();
        }
        smallTickmarkImage = create_TICKMARKS_Image((int) (0.285 * GAUGE_WIDTH), 30f, 0.095, 0.17, null);

        if (mainPointerImage != null) {
            mainPointerImage.flush();
        }
        mainPointerImage = create_MAIN_POINTER_Image(GAUGE_WIDTH);

        if (mainPointerShadowImage != null) {
            mainPointerShadowImage.flush();
        }
        if (getModel().isPointerShadowVisible()) {
            mainPointerShadowImage = create_MAIN_POINTER_SHADOW_Image(GAUGE_WIDTH);
        } else {
            mainPointerShadowImage = null;
        }

        if (smallPointerImage != null) {
            smallPointerImage.flush();
        }
        smallPointerImage = create_SMALL_POINTER_Image(GAUGE_WIDTH);

        if (smallPointerShadowImage != null) {
            smallPointerShadowImage.flush();
        }
        if (getModel().isPointerShadowVisible()) {
            smallPointerShadowImage = create_SMALL_POINTER_SHADOW_Image(GAUGE_WIDTH);
        } else {
            smallPointerShadowImage = null;
        }

        if (isForegroundVisible()) {
            switch (getFrameType()) {
                case SQUARE:
                    FOREGROUND_FACTORY.createLinearForeground(GAUGE_WIDTH, GAUGE_WIDTH, false, backgroundImage);
                    break;

                case ROUND:

                default:
                    FOREGROUND_FACTORY.createRadialForeground(GAUGE_WIDTH, false, getForegroundType(), foregroundImage);
                    break;
            }
        }

        if (disabledImage != null) {
            disabledImage.flush();
        }
        disabledImage = create_DISABLED_Image(GAUGE_WIDTH);

        return this;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Visualization">
    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D G2 = (Graphics2D) g.create();

        MAIN_CENTER.setLocation(getGaugeBounds().getCenterX(), getGaugeBounds().getCenterY());
        SMALL_CENTER.setLocation(getGaugeBounds().getCenterX(), getGaugeBounds().height * 0.3130841121);
        
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Translate the coordinate system related to the insets
        G2.translate(getFramelessOffset().getX(), getFramelessOffset().getY());

        final AffineTransform OLD_TRANSFORM = G2.getTransform();

        // Draw combined background image
        G2.drawImage(backgroundImage, 0, 0, null);

        G2.drawImage(smallTickmarkImage, ((getGaugeBounds().width - smallTickmarkImage.getWidth()) / 2), (int) (SMALL_CENTER.getY() - smallTickmarkImage.getHeight() / 2.0), null);

        // Draw the small pointer
        G2.rotate(Math.toRadians(minutePointerAngle + (2 * Math.sin(Math.toRadians(minutePointerAngle)))), SMALL_CENTER.getX(), SMALL_CENTER.getY());
        G2.drawImage(smallPointerShadowImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);
        G2.rotate(Math.toRadians(minutePointerAngle), SMALL_CENTER.getX(), SMALL_CENTER.getY());
        G2.drawImage(smallPointerImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);

        // Draw LCD display
        if (isLcdVisible()) {
            if (getLcdColor() == LcdColor.CUSTOM) {
                G2.setColor(getCustomLcdForeground());
            } else {
                G2.setColor(getLcdColor().TEXT_COLOR);
            }
            G2.setFont(getLcdUnitFont());
            if (isLcdUnitStringVisible()) {
                unitLayout = new TextLayout(getLcdUnitString(), G2.getFont(), RENDER_CONTEXT);
                UNIT_BOUNDARY.setFrame(unitLayout.getBounds());
                G2.drawString(getLcdUnitString(), (float) (LCD.getX() + (LCD.getWidth() - UNIT_BOUNDARY.getWidth()) - LCD.getWidth() * 0.03), (float) (LCD.getY() + LCD.getHeight() * 0.76));
                unitStringWidth = UNIT_BOUNDARY.getWidth();
            } else {
                unitStringWidth = 0;
            }
            
            String lcdText = String.format( time < 0 ? "-%d:%02d.%1d" : "%d:%02d.%1d", Math.abs(minutes), Math.abs(seconds), Math.abs(milliSeconds/100));
            int digitalFontNo_1Offset = 0;
            if (isDigitalFont() && lcdText.startsWith("1")) {
                digitalFontNo_1Offset = (int) (LCD.getHeight() * 0.27);
            }
            G2.setFont(getLcdValueFont());
            valueLayout = new TextLayout(lcdText, G2.getFont(), RENDER_CONTEXT);
            VALUE_BOUNDARY.setFrame(valueLayout.getBounds());
            G2.drawString(lcdText, (float) (LCD.getX() + (LCD.getWidth() - unitStringWidth - VALUE_BOUNDARY.getWidth()) - LCD.getWidth() * 0.09) - digitalFontNo_1Offset, (float) (LCD.getY() + LCD.getHeight() * 0.76));
            // Draw lcd info string
            if (!getLcdInfoString().isEmpty()) {
                G2.setFont(getLcdInfoFont());
                infoLayout = new TextLayout(getLcdInfoString(), G2.getFont(), RENDER_CONTEXT);
                INFO_BOUNDARY.setFrame(infoLayout.getBounds());
                G2.drawString(getLcdInfoString(), LCD.getBounds().x + 5f, LCD.getBounds().y + (float) INFO_BOUNDARY.getHeight() + 5f);
            }
        }

        // Draw the main pointer
        G2.rotate(Math.toRadians(secondPointerAngle + (2 * Math.sin(Math.toRadians(secondPointerAngle)))), MAIN_CENTER.getX(), MAIN_CENTER.getY());
        G2.drawImage(mainPointerShadowImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);
        G2.rotate(Math.toRadians(secondPointerAngle), MAIN_CENTER.getX(), MAIN_CENTER.getY());
        G2.drawImage(mainPointerImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);

        // Draw combined foreground image
        G2.drawImage(foregroundImage, 0, 0, null);

        if (!isEnabled()) {
            G2.drawImage(disabledImage, 0, 0, null);
        }

        // Translate the coordinate system back to original
        G2.translate(-getInnerBounds().x, -getInnerBounds().y);

        G2.dispose();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters and Setters">
    /**
     * Returns true if the stopwatch is running
     * @return true if the stopwatch is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Start or stop the stopwatch
     * @param RUNNING
     */
    public void setRunning(final boolean RUNNING) {
        running = RUNNING;
        if (RUNNING) {
            if (!CLOCK_TIMER.isRunning()) {
                CLOCK_TIMER.start();
                start = System.currentTimeMillis();
                repaint(getGaugeBounds());
            }
        } else {
            if (CLOCK_TIMER.isRunning()) {
                CLOCK_TIMER.stop();
            }
        }
    }

    /**
     * Starts the stopwatch
     */
    public void start() {
        setRunning(true);
    }

    /**
     * Stops the stopwatch
     */
    public void stop() {
        setRunning(false);
    }

    /**
     * Resets the stopwatch
     */
    public void reset() {
        setRunning(false);
        start = 0;
        repaint(getGaugeBounds());
    }

    /**
     * Returns a string that contains MIN:SEC.MILLISEC of the measured time
     * @return a string that contains MIN:SEC.MILLISEC of the measured time
     */
    public String getMeasuredTime() {
        return String.format( time < 0 ? "-%d:%02d.%01d" : "%d:%02d.%01d", Math.abs(minutes), Math.abs(seconds), Math.abs(milliSeconds));
    }

    /**
     * Get the measured time im millisecs
     * @return the measured time im millisecs
     */
    public long getMeasuredTimeMillis() {
        return ((minutes * 60 + seconds) * 1000) + milliSeconds;
    }

    /**
     * Set the measured time in seconds
     * @param seconds
     */
    public void setMeasuredTime(double seconds) {
        setMeasuredTimeMillis(Math.round(seconds * 1000));    
    }

    /**
     * Set the measured time in seconds
     * @param seconds
     */
    public void setMeasuredTime(int seconds) {
        setMeasuredTimeMillis(seconds * 1000L);    
    }

    /**
     * Set the measured time in millis
     * @param millis
     */
    public void setMeasuredTimeMillis(long millis) {
        secondPointerAngle = (millis * ANGLE_STEP / 1000) % 360;
        minutePointerAngle = (millis * ANGLE_STEP / 1000 / 30) % 360;

        this.time = millis;
        
        minutes = millis / (1000 * 60);
        seconds =  (millis / 1000) % 60;
        milliSeconds = millis % 1000;

        repaint(getGaugeBounds());
    }

    public boolean isFlatNeedle() {
        return flatNeedle;
    }

    public void setFlatNeedle(final boolean FLAT_NEEDLE) {
        flatNeedle = FLAT_NEEDLE;
        init(getWidth(), getWidth());
        repaint(getGaugeBounds());
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Image related">
    private BufferedImage create_TICKMARKS_Image(final int WIDTH, final float RANGE,
                                                                final double TEXT_SCALE,
                                                                final double TEXT_DISTANCE_FACTOR,
                                                                BufferedImage image) {
        if (WIDTH <= 0) {
            return null;
        }
        if (image == null) {
            image = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        }

        final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);

        final Graphics2D G2 = image.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final int IMAGE_WIDTH = image.getWidth();
        final int IMAGE_HEIGHT = image.getHeight();

        final Font STD_FONT = new Font("Verdana", 0, (int) (TEXT_SCALE * WIDTH));
        final BasicStroke THIN_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final BasicStroke MEDIUM_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final BasicStroke THICK_STROKE = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final int TEXT_DISTANCE = (int) (TEXT_DISTANCE_FACTOR * WIDTH);
        final int MIN_LENGTH = (int) (0.025 * WIDTH);
        final int MED_LENGTH = (int) (0.035 * WIDTH);
        final int MAX_LENGTH = (int) (0.045 * WIDTH);
        final Color TEXT_COLOR = getBackgroundColor().LABEL_COLOR;
        final Color TICK_COLOR = getBackgroundColor().LABEL_COLOR;

        // Create the ticks itself
        final float RADIUS = IMAGE_WIDTH * 0.4f;
        final Point2D IMAGE_CENTER = new Point2D.Double(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT / 2.0f);

        // Draw ticks
        Point2D innerPoint;
        Point2D outerPoint;
        Point2D textPoint = null;
        Line2D tick;
        int counter = 0;
        int numberCounter = 0;
        int tickCounter = 0;

        G2.setFont(STD_FONT);

        double sinValue = 0;
        double cosValue = 0;

        double alpha; // angle for the tickmarks
        final double ALPHA_START = -Math.PI;
        float valueCounter; // value for the tickmarks

        final double ANGLE_STEPSIZE = (2 * Math.PI) / (RANGE);

        for (alpha = ALPHA_START, valueCounter = 0; Float.compare(valueCounter, RANGE + 1) <= 0; alpha -= ANGLE_STEPSIZE * 0.1, valueCounter += 0.1f) {
            G2.setStroke(THIN_STROKE);
            sinValue = Math.sin(alpha);
            cosValue = Math.cos(alpha);

            // tickmark every 2 units
            if (counter % 2 == 0) {
                G2.setStroke(THIN_STROKE);
                innerPoint = new Point2D.Double(IMAGE_CENTER.getX() + (RADIUS - MIN_LENGTH) * sinValue, IMAGE_CENTER.getY() + (RADIUS - MIN_LENGTH) * cosValue);
                outerPoint = new Point2D.Double(IMAGE_CENTER.getX() + RADIUS * sinValue, IMAGE_CENTER.getY() + RADIUS * cosValue);
                // Draw ticks
                G2.setColor(TICK_COLOR);
                tick = new Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);
            }

            // Different tickmark every 10 units
            if (counter == 10 || counter == 0) {
                G2.setColor(TEXT_COLOR);
                G2.setStroke(MEDIUM_STROKE);
                outerPoint = new Point2D.Double(IMAGE_CENTER.getX() + RADIUS * sinValue, IMAGE_CENTER.getY() + RADIUS * cosValue);
                textPoint = new Point2D.Double(IMAGE_CENTER.getX() + (RADIUS - TEXT_DISTANCE + STD_FONT.getSize() / 2f) * sinValue, IMAGE_CENTER.getY() + (RADIUS - TEXT_DISTANCE + STD_FONT.getSize() / 2f) * cosValue + TEXT_DISTANCE / 2.5f);

                // Draw text
                if (numberCounter == 5) {
                    final TextLayout TEXT_LAYOUT = new TextLayout(String.valueOf(Math.round(valueCounter)), G2.getFont(), RENDER_CONTEXT);
                    final Rectangle2D TEXT_BOUNDARY = TEXT_LAYOUT.getBounds();

                    if (Float.compare(valueCounter, RANGE) != 0) {
                        if (Math.ceil(valueCounter) != 60) {
                            G2.drawString(String.valueOf(Math.round(valueCounter)), (int) (textPoint.getX() - TEXT_BOUNDARY.getWidth() / 2.0), (int) ((textPoint.getY() - TEXT_BOUNDARY.getHeight() / 2.0)));
                        }
                    }
                    G2.setStroke(THICK_STROKE);
                    innerPoint = new Point2D.Double(IMAGE_CENTER.getX() + (RADIUS - MAX_LENGTH) * sinValue, IMAGE_CENTER.getY() + (RADIUS - MAX_LENGTH) * cosValue);
                    numberCounter = 0;
                } else {
                    G2.setStroke(MEDIUM_STROKE);
                    innerPoint = new Point2D.Double(IMAGE_CENTER.getX() + (RADIUS - MED_LENGTH) * sinValue, IMAGE_CENTER.getY() + (RADIUS - MED_LENGTH) * cosValue);
                }

                // Draw ticks
                G2.setColor(TICK_COLOR);
                tick = new Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);

                counter = 0;
                tickCounter++;
                numberCounter++;
            }

            counter++;
        }

        G2.dispose();

        return image;
    }

    private BufferedImage create_MAIN_POINTER_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        final GeneralPath STOPWATCHPOINTER = new GeneralPath();
        STOPWATCHPOINTER.setWindingRule(Path2D.WIND_EVEN_ODD);
        STOPWATCHPOINTER.moveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.102803738317757);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.4719626168224299, IMAGE_HEIGHT * 0.46261682242990654, IMAGE_WIDTH * 0.45794392523364486, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.45794392523364486, IMAGE_HEIGHT * 0.5);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.45794392523364486, IMAGE_HEIGHT * 0.5186915887850467, IMAGE_WIDTH * 0.4719626168224299, IMAGE_HEIGHT * 0.5373831775700935, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.6214953271028038);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.6214953271028038);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5280373831775701, IMAGE_HEIGHT * 0.5373831775700935, IMAGE_WIDTH * 0.5420560747663551, IMAGE_HEIGHT * 0.5186915887850467, IMAGE_WIDTH * 0.5420560747663551, IMAGE_HEIGHT * 0.5);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5420560747663551, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.5280373831775701, IMAGE_HEIGHT * 0.46261682242990654, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.closePath();
        if (flatNeedle) {
            G2.setColor(getPointerColor().MEDIUM);
            G2.fill(STOPWATCHPOINTER);
        } else {
            final Point2D POINTER_START = new Point2D.Double(STOPWATCHPOINTER.getBounds2D().getMinX(), 0);
            final Point2D POINTER_STOP = new Point2D.Double(STOPWATCHPOINTER.getBounds2D().getMaxX(), 0);
            final float[] POINTER_FRACTIONS = {
                0.0f,
                0.3888888889f,
                0.5f,
                0.6111111111f,
                1.0f
            };
            final Color[] POINTER_COLORS = {
                getPointerColor().MEDIUM,
                getPointerColor().MEDIUM,
                getPointerColor().LIGHT,
                getPointerColor().MEDIUM,
                getPointerColor().MEDIUM
            };
            final LinearGradientPaint GRADIENT = new LinearGradientPaint(POINTER_START, POINTER_STOP, POINTER_FRACTIONS, POINTER_COLORS);
            G2.setPaint(GRADIENT);
            G2.fill(STOPWATCHPOINTER);
            G2.setPaint(getPointerColor().DARK);
            G2.draw(STOPWATCHPOINTER);
        }

        final Ellipse2D SWBRASSRING = new Ellipse2D.Double(IMAGE_WIDTH * 0.4672897160053253, IMAGE_HEIGHT * 0.4672897160053253, IMAGE_WIDTH * 0.06542053818702698, IMAGE_HEIGHT * 0.06542053818702698);
        final Point2D SWBRASSRING_START = new Point2D.Double(0, SWBRASSRING.getBounds2D().getMaxY());
        final Point2D SWBRASSRING_STOP = new Point2D.Double(0, SWBRASSRING.getBounds2D().getMinY());
        final float[] SWBRASSRING_FRACTIONS = {
            0.0f,
            0.01f,
            0.99f,
            1.0f
        };
        final Color[] SWBRASSRING_COLORS = {
            new Color(230, 179, 92, 255),
            new Color(230, 179, 92, 255),
            new Color(196, 130, 0, 255),
            new Color(196, 130, 0, 255)
        };
        final LinearGradientPaint SWBRASSRING_GRADIENT = new LinearGradientPaint(SWBRASSRING_START, SWBRASSRING_STOP, SWBRASSRING_FRACTIONS, SWBRASSRING_COLORS);
        G2.setPaint(SWBRASSRING_GRADIENT);
        G2.fill(SWBRASSRING);

        final Ellipse2D SWRING1 = new Ellipse2D.Double(IMAGE_WIDTH * 0.47663551568984985, IMAGE_HEIGHT * 0.47663551568984985, IMAGE_WIDTH * 0.04672896862030029, IMAGE_HEIGHT * 0.04672896862030029);
        final Point2D SWRING1_CENTER = new Point2D.Double((0.5 * IMAGE_WIDTH), (0.5 * IMAGE_HEIGHT));
        final float[] SWRING1_FRACTIONS = {
            0.0f,
            0.19f,
            0.22f,
            0.8f,
            0.99f,
            1.0f
        };
        final Color[] SWRING1_COLORS = {
            new Color(197, 197, 197, 255),
            new Color(197, 197, 197, 255),
            new Color(0, 0, 0, 255),
            new Color(0, 0, 0, 255),
            new Color(112, 112, 112, 255),
            new Color(112, 112, 112, 255)
        };
        final RadialGradientPaint SWRING1_GRADIENT = new RadialGradientPaint(SWRING1_CENTER, (float) (0.02336448598130841 * IMAGE_WIDTH), SWRING1_FRACTIONS, SWRING1_COLORS);
        G2.setPaint(SWRING1_GRADIENT);
        G2.fill(SWRING1);

        G2.dispose();

        return IMAGE;
    }

    private BufferedImage create_MAIN_POINTER_SHADOW_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        final GeneralPath STOPWATCHPOINTER = new GeneralPath();
        STOPWATCHPOINTER.setWindingRule(Path2D.WIND_EVEN_ODD);
        STOPWATCHPOINTER.moveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.102803738317757);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.4719626168224299, IMAGE_HEIGHT * 0.46261682242990654, IMAGE_WIDTH * 0.45794392523364486, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.45794392523364486, IMAGE_HEIGHT * 0.5);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.45794392523364486, IMAGE_HEIGHT * 0.5186915887850467, IMAGE_WIDTH * 0.4719626168224299, IMAGE_HEIGHT * 0.5373831775700935, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.6214953271028038);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.6214953271028038);
        STOPWATCHPOINTER.lineTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5420560747663551);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5280373831775701, IMAGE_HEIGHT * 0.5373831775700935, IMAGE_WIDTH * 0.5420560747663551, IMAGE_HEIGHT * 0.5186915887850467, IMAGE_WIDTH * 0.5420560747663551, IMAGE_HEIGHT * 0.5);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5420560747663551, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.5280373831775701, IMAGE_HEIGHT * 0.46261682242990654, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.curveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.45794392523364486);
        STOPWATCHPOINTER.closePath();

        G2.setPaint(SHADOW_COLOR);
        G2.fill(STOPWATCHPOINTER);

        G2.dispose();

        return IMAGE;
    }

    private BufferedImage create_SMALL_POINTER_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        final GeneralPath STOPWATCHPOINTERSMALL = new GeneralPath();
        STOPWATCHPOINTERSMALL.setWindingRule(Path2D.WIND_EVEN_ODD);
        STOPWATCHPOINTERSMALL.moveTo(IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.3130841121495327);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.32242990654205606, IMAGE_WIDTH * 0.48598130841121495, IMAGE_HEIGHT * 0.3317757009345794, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.3364485981308411);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.3364485981308411, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.35046728971962615, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.35046728971962615);
        STOPWATCHPOINTERSMALL.lineTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.35046728971962615);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.35046728971962615, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.3364485981308411, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.3364485981308411);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.3317757009345794, IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.32242990654205606, IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.3130841121495327);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.3037383177570093, IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.29439252336448596, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.2897196261682243);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.2897196261682243, IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.20093457943925233, IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.20093457943925233);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.20093457943925233, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.2897196261682243, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.2897196261682243);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.48598130841121495, IMAGE_HEIGHT * 0.29439252336448596, IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.3037383177570093, IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.3130841121495327);
        STOPWATCHPOINTERSMALL.closePath();
        if (flatNeedle) {
            G2.setColor(getPointerColor().MEDIUM);
            G2.fill(STOPWATCHPOINTERSMALL);
        } else {
            final Point2D POINTER_START = new Point2D.Double(STOPWATCHPOINTERSMALL.getBounds2D().getMinX(), 0);
            final Point2D POINTER_STOP = new Point2D.Double(STOPWATCHPOINTERSMALL.getBounds2D().getMaxX(), 0);
            final float[] POINTER_FRACTIONS = {
                0.0f,
                0.3888888889f,
                0.5f,
                0.6111111111f,
                1.0f
            };
            final Color[] POINTER_COLORS = {
                getPointerColor().MEDIUM,
                getPointerColor().MEDIUM,
                getPointerColor().LIGHT,
                getPointerColor().MEDIUM,
                getPointerColor().MEDIUM
            };
            final LinearGradientPaint GRADIENT = new LinearGradientPaint(POINTER_START, POINTER_STOP, POINTER_FRACTIONS, POINTER_COLORS);
            G2.setPaint(GRADIENT);
            G2.fill(STOPWATCHPOINTERSMALL);
            G2.setPaint(getPointerColor().DARK);
            G2.draw(STOPWATCHPOINTERSMALL);
        }

        final Ellipse2D SWBRASSRINGSMALL = new Ellipse2D.Double(IMAGE_WIDTH * 0.4813084006309509, IMAGE_HEIGHT * 0.29439252614974976, IMAGE_WIDTH * 0.037383198738098145, IMAGE_HEIGHT * 0.03738316893577576);
        G2.setColor(new Color(0xC48200));
        G2.fill(SWBRASSRINGSMALL);

        final Ellipse2D SWRING1SMALL = new Ellipse2D.Double(IMAGE_WIDTH * 0.4859813153743744, IMAGE_HEIGHT * 0.29906541109085083, IMAGE_WIDTH * 0.02803739905357361, IMAGE_HEIGHT * 0.02803739905357361);
        G2.setColor(new Color(0x999999));
        G2.fill(SWRING1SMALL);

        final Ellipse2D SWRING1SMALL0 = new Ellipse2D.Double(IMAGE_WIDTH * 0.49065420031547546, IMAGE_HEIGHT * 0.3037383258342743, IMAGE_WIDTH * 0.018691569566726685, IMAGE_HEIGHT * 0.018691569566726685);
        G2.setColor(Color.BLACK);
        G2.fill(SWRING1SMALL0);

        G2.dispose();

        return IMAGE;
    }

    private BufferedImage create_SMALL_POINTER_SHADOW_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, WIDTH, Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        final GeneralPath STOPWATCHPOINTERSMALL = new GeneralPath();
        STOPWATCHPOINTERSMALL.setWindingRule(Path2D.WIND_EVEN_ODD);
        STOPWATCHPOINTERSMALL.moveTo(IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.3130841121495327);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.32242990654205606, IMAGE_WIDTH * 0.48598130841121495, IMAGE_HEIGHT * 0.3317757009345794, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.3364485981308411);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.3364485981308411, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.35046728971962615, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.35046728971962615);
        STOPWATCHPOINTERSMALL.lineTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.35046728971962615);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.35046728971962615, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.3364485981308411, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.3364485981308411);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.3317757009345794, IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.32242990654205606, IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.3130841121495327);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.3037383177570093, IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.29439252336448596, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.2897196261682243);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.2897196261682243, IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.20093457943925233, IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.20093457943925233);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.20093457943925233, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.2897196261682243, IMAGE_WIDTH * 0.4953271028037383, IMAGE_HEIGHT * 0.2897196261682243);
        STOPWATCHPOINTERSMALL.curveTo(IMAGE_WIDTH * 0.48598130841121495, IMAGE_HEIGHT * 0.29439252336448596, IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.3037383177570093, IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.3130841121495327);
        STOPWATCHPOINTERSMALL.closePath();

        G2.setPaint(SHADOW_COLOR);
        G2.fill(STOPWATCHPOINTERSMALL);

        G2.dispose();

        return IMAGE;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Size related">
    @Override
    public Point2D getCenter() {
        return new Point2D.Double(getInnerBounds().getCenterX() + getInnerBounds().x, getInnerBounds().getCenterX() + getInnerBounds().y);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return getInnerBounds();
    }

    @Override
    public Rectangle getLcdBounds() {
        return new Rectangle();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Misc">
    @Override
    public void dispose() {
        CLOCK_TIMER.removeActionListener(this);
        super.dispose();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ActionListener">
    @Override
    public void actionPerformed(final ActionEvent EVENT) {
        if (EVENT.getSource().equals(CLOCK_TIMER)) {
            setMeasuredTimeMillis(System.currentTimeMillis() - start);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ComponentListener">
//    @Override
//    public void componentResized(ComponentEvent event) {
//        final int SIZE = getWidth() < getHeight() ? getWidth() : getHeight();
//        setPreferredSize(new java.awt.Dimension(SIZE, SIZE));
//
//        if (SIZE < getMinimumSize().width || SIZE < getMinimumSize().height) {
//            setPreferredSize(getMinimumSize());
//        }
//        calcInnerBounds();
//
//        init(INNER_BOUNDS.width, INNER_BOUNDS.height);
//
//        revalidate();
//        repaint();
//    }
    // </editor-fold>

    @Override
    public String toString() {
        return "StopWatch";
    }
}
