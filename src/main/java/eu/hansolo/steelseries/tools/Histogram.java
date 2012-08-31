package eu.hansolo.steelseries.tools;

import static java.lang.Math.round;


/**
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public class Histogram {

    private final float MIN_VALUE; // the minimum value of the gauge dial    
    private final float MINOR_TICKMARK_SPACING;  // the distance between two minor tickmarks
    private final double[] FREQUENCE;   // frequence[i] = # occurences of value i
    private double max;            // max frequency of any value
    private int n;

    // Create a new histogram. 
    public Histogram(final double MIN_VALUE, final double MAX_VALUE, final double MINOR_TICKMARK_SPACING) {
        this.MIN_VALUE = (float) MIN_VALUE;
        this.MINOR_TICKMARK_SPACING = (float) MINOR_TICKMARK_SPACING;
        n = (int) ((MAX_VALUE - MIN_VALUE) / MINOR_TICKMARK_SPACING);

        FREQUENCE = new double[n];
    }

    // Add one occurrence of the value i. 
    public void addDataPoint(final int INDEX) {
        FREQUENCE[INDEX > n - 1 ? n - 1 : INDEX]++;
        max = FREQUENCE[INDEX] > max ? FREQUENCE[INDEX] : max;
    }

    public void addDataPoint(final double VALUE) {
        int index = round((float) VALUE / MINOR_TICKMARK_SPACING) - round(MIN_VALUE / MINOR_TICKMARK_SPACING);
        addDataPoint(index);
    }

    public double[] getData() {
        return FREQUENCE.clone();
    }
}
