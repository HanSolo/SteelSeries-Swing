package eu.hansolo.steelseries.tools;

import java.awt.Color;
import java.awt.Paint;


/**
 * <p/>
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public class DesignSet {

    private final FrameDesign FRAME_DESIGN;
    private final FrameEffect FRAME_EFFECT;
    private final Paint OUTER_FRAME_COLOR;
    private final Paint INNER_FRAME_COLOR;
    private final BackgroundColor BACKGROUND_COLOR;    
    private final Color TEXTURE_COLOR;
    private final ColorDef COLOR;
    private final LedColor LED_COLOR;
    private final LedColor USER_LED_COLOR;
    private final LcdColor LCD_COLOR;
    private final Color GLOW_COLOR;
    private final KnobStyle KNOB_STYLE;

    private DesignSet(Builder builder) {
        // private Constructor can only be called from Builder
        FRAME_DESIGN = builder.frameDesign;
        FRAME_EFFECT = builder.frameEffect;
        OUTER_FRAME_COLOR = builder.outerFrameColor;
        INNER_FRAME_COLOR = builder.innerFrameColor;
        BACKGROUND_COLOR = builder.backgroundColor;        
        TEXTURE_COLOR = builder.textureColor;
        COLOR = builder.color;
        LED_COLOR = builder.ledColor;
        USER_LED_COLOR = builder.userLedColor;
        LCD_COLOR = builder.lcdColor;
        GLOW_COLOR = builder.glowColor;
        KNOB_STYLE = builder.knobStyle;
    }

    public FrameDesign getFrameDesign() {
        return FRAME_DESIGN;
    }

    public FrameEffect getFrameEffect() {
        return FRAME_EFFECT;
    }
    
    public Paint getOuterFrameColor() {
        return OUTER_FRAME_COLOR;
    }
    
    public Paint getInnerFrameColor() {
        return INNER_FRAME_COLOR;
    }
    
    public BackgroundColor getBackgroundColor() {
        return BACKGROUND_COLOR;
    }
    
    public Color getTextureColor() {
        return TEXTURE_COLOR;
    }
    
    public ColorDef getColor() {
        return COLOR;
    }

    public LedColor getLedColor() {
        return LED_COLOR;
    }

    public LedColor getUserLedColor() {
        return USER_LED_COLOR;
    }

    public LcdColor getLcdColor() {
        return LCD_COLOR;
    }

    public Color getGlowColor() {
        return GLOW_COLOR;
    }
    
    public KnobStyle getKnobStyle() {
        return KNOB_STYLE;
    }
    
    public static class Builder {
        // mandatory parameter
        private FrameDesign frameDesign = null;
        private FrameEffect frameEffect = null;
        private Paint outerFrameColor = null;
        private Paint innerFrameColor = null;
        private BackgroundColor backgroundColor = null;        
        private Color textureColor = null;
        private ColorDef color = null;
        private LedColor ledColor = null;
        private LedColor userLedColor = null;
        private LcdColor lcdColor = null;
        private Color glowColor = null;
        private KnobStyle knobStyle = null;

        public Builder() {
        }

        public Builder frameDesign(FrameDesign frameDesign) {
            this.frameDesign = frameDesign;
            return this;
        }

        public Builder frameEffect(FrameEffect frameEffect) {
            this.frameEffect = frameEffect;
            return this;
        }
        
        public Builder outerFrameColor(Paint outerFrameColor) {
            this.outerFrameColor = outerFrameColor;
            return this;
        }
        
        public Builder innerFrameColor(Paint innerFrameColor) {
            this.innerFrameColor = innerFrameColor;
            return this;
        }
        
        public Builder backgroundColor(BackgroundColor backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }
               
        public Builder textureColor(Color textureColor) {
            this.textureColor = textureColor;
            return this;
        }
        
        public Builder color(ColorDef color) {
            this.color = color;
            return this;
        }

        public Builder ledColor(LedColor ledColor) {
            this.ledColor = ledColor;
            return this;
        }

        public Builder userLedColor(LedColor userLedColor) {
            this.userLedColor = userLedColor;
            return this;
        }

        public Builder lcdColor(LcdColor lcdColor) {
            this.lcdColor = lcdColor;
            return this;
        }
        
        public Builder glowColor(Color glowColor) {
            this.glowColor = glowColor;
            return this;
        }

        public Builder knobStyle(KnobStyle knobStyle) {
            this.knobStyle = knobStyle;
            return this;
        }
        
        public DesignSet build() {
            return new DesignSet(this);
        }
    }

}
