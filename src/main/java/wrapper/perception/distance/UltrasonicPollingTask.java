package wrapper.perception.distance;

import lejos.robotics.SampleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.perception.observerpattern.SensorPollingTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements SensorPollingTask for the OrientationAnglePublisher interface.
 */
public class UltrasonicPollingTask extends SensorPollingTask implements ViewDistancePublisher {

    private static final Logger log = LoggerFactory.getLogger(UltrasonicPollingTask.class);

    private final float minDistChange;
    private final List<ViewDistanceListener> listenerList;

    private float lastUpdatedDist;

    /**
     * Creates an UltrasonicPollingTask that sends updates to the registered listeners if the measurements retrieved
     * from the ultrasonic sensor change.
     *
     * @param ultrasonicSampler SampleProvider of the ultrasonic sensor.
     * @param minUpdateDelay Will not send updates more frequently than this value in ms.
     * @param minDistChange Will not send updates if the value changed less than this value in cm.
     */
    public UltrasonicPollingTask(SampleProvider ultrasonicSampler, int minUpdateDelay, float minDistChange) {
        super(ultrasonicSampler, minUpdateDelay);
        this.minDistChange = minDistChange;

        lastUpdatedDist = 0;

        List<ViewDistanceListener> list = new ArrayList<>();
        listenerList = Collections.synchronizedList(list);
    }

    @Override
    protected boolean valuesJustifyUpdate(float[] measuredValues) {
        float distanceMeasured = measuredValues[0];
        return Math.abs(distanceMeasured - lastUpdatedDist) > minDistChange;
    }

    @Override
    protected void notify(float[] measuredValues) {
        float distanceMeasured = measuredValues[0];

        try {
            for (ViewDistanceListener listener : listenerList) {
                listener.distanceChanged(distanceMeasured);
            }
        } catch (Exception e) {
            log.error("Exception was thrown by an OrientationAngleListener. " +
                    "Automatic ultrasonic polling will stop now.", e);
            throw e;
        }

        lastUpdatedDist = distanceMeasured;
    }

    @Override
    public void addListener(ViewDistanceListener angleListener) {
        listenerList.add(angleListener);
    }

    @Override
    public void removeListener(ViewDistanceListener angleListener) {
        listenerList.remove(angleListener);
    }
}
