/*
 */
package eu.hansolo.steelseries.tools;

import java.text.DecimalFormat;


/**
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public enum NumberFormat {

    AUTO("0"),
    STANDARD("0"),
    FRACTIONAL("0.0#"),
    SCIENTIFIC("0.##E0"),
    PERCENTAGE("##0.0%");
    
    private final DecimalFormat DF;

    private NumberFormat(final String FORMAT_STRING) {
        DF = new DecimalFormat(FORMAT_STRING);
    }

    public String format(final Number NUMBER) {
        return DF.format(NUMBER);
    }
}
