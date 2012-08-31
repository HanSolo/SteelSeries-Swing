package eu.hansolo.steelseries.extras;

/**
 * @author Gerrit Grunwald <han.solo at muenster.de>
 */
public class StopWatchBeanInfo extends java.beans.SimpleBeanInfo {

    @Override
    public java.awt.Image getIcon(final int ICON_TYPE) {
        switch (ICON_TYPE) {
            case ICON_COLOR_16x16:
                return loadImage("/eu/hansolo/steelseries/resources/StopWatch16.png");

            case ICON_COLOR_32x32:
                return loadImage("/eu/hansolo/steelseries/resources/StopWatch32.png");

            case ICON_MONO_16x16:
                return null;

            case ICON_MONO_32x32:
                return null;

            default:
                return null;
        }
    }
}
