/*
 * Copyright (c) 2012, Gerrit Grunwald
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
package eu.hansolo.steelseries.tools;

import java.awt.geom.Rectangle2D;

public class CustomGaugeType
{
    final public double FREE_AREA_ANGLE;
    final public double ROTATION_OFFSET;
    final public double TICKMARK_OFFSET;
    final public double TICKLABEL_ORIENTATION_CHANGE_ANGLE;
    final public double ANGLE_RANGE;
    final public double ORIGIN_CORRECTION;
    final public double APEX_ANGLE;
    final public double BARGRAPH_OFFSET;
    final public PostPosition[] POST_POSITIONS;
    final public Rectangle2D LCD_FACTORS;
    
    public CustomGaugeType(double FREE_AREA_ANGLE, double ROTATION_OFFSET, double TICKMARK_OFFSET, double TICKLABEL_ORIENTATION_CHANGE_ANGLE, double ANGLE_RANGE, double ORIGIN_CORRECTION, double APEX_ANGLE, double BARGRAPH_OFFSET, Rectangle2D LCD_FACTORS, PostPosition... POST_POSITIONS)
    {
        super();
        this.FREE_AREA_ANGLE = FREE_AREA_ANGLE;
        this.ROTATION_OFFSET = ROTATION_OFFSET;
        this.TICKMARK_OFFSET = TICKMARK_OFFSET;
        this.TICKLABEL_ORIENTATION_CHANGE_ANGLE = TICKLABEL_ORIENTATION_CHANGE_ANGLE;
        this.ANGLE_RANGE = ANGLE_RANGE;
        this.ORIGIN_CORRECTION = ORIGIN_CORRECTION;
        this.APEX_ANGLE = APEX_ANGLE;
        this.BARGRAPH_OFFSET = BARGRAPH_OFFSET;
        this.LCD_FACTORS = LCD_FACTORS;
        this.POST_POSITIONS = POST_POSITIONS;
    }
    
    /**
     * Create a custom gauge type with the given range starting at the given offset.
     * 
     * @param RANGE the range in degree.
     * @param OFFSET the offset in degree.
     * @param POST_POSITIONS
     */
    public CustomGaugeType(double RANGE, double OFFSET, PostPosition... POST_POSITIONS)
    {
        this.FREE_AREA_ANGLE = 2 * Math.PI - Math.toRadians(RANGE);
        this.ROTATION_OFFSET = Math.toRadians(OFFSET);
        this.TICKMARK_OFFSET = 0;
        this.TICKLABEL_ORIENTATION_CHANGE_ANGLE = Math.PI / 2;
        this.ANGLE_RANGE = Math.toRadians(RANGE);
        //Compute the origin correction
        final double offset = OFFSET % 360;
        if (offset <= 45 || offset >= 270)
        {
            this.ORIGIN_CORRECTION = (-OFFSET + 90) % 360;
        }
        else
        {
            this.ORIGIN_CORRECTION = (-OFFSET - 270) % 360;
        }
        this.APEX_ANGLE = RANGE;
        this.BARGRAPH_OFFSET = 0;
        this.LCD_FACTORS = new Rectangle2D.Double(0.4, 0.55, 0.4, 0.15);
        this.POST_POSITIONS = POST_POSITIONS;
    }
}
