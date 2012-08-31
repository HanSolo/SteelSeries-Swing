package eu.hansolo.steelseries.tools;

/**
 *
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public class DataPoint {

    private final long TIMESTAMP;
    private final double VALUE;

    public DataPoint(final long TIMESTAMP, final double VALUE) {
        this.TIMESTAMP = TIMESTAMP;
        this.VALUE = VALUE;
    }

    public long getTimeStamp() {
        return this.TIMESTAMP;
    }

    public double getValue() {
        return this.VALUE;
    }
}
