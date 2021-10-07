package wrapper.perception.orentation;

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
public class GyroPollingTask extends SensorPollingTask implements OrientationAnglePublisher {

    private static final Logger log = LoggerFactory.getLogger(GyroPollingTask.class);

    private final float minAngleChange;
    private final List<OrientationAngleListener> listenerList;

    private float lastUpdatedAngle;

    /**
     * Creates a GyroPollingTask that sends updates to the registered listeners if the measurements retrieved
     * from the gyroscopic sensor change.
     *
     * @param gyroSampler SampleProvider of the gyroscopic sensor in angle mode.
     * @param minUpdateDelay Will not send updates more frequently than this value in ms.
     * @param minAngleChange Will not send updates if the value changed less than this value in degrees.
     */
    public GyroPollingTask(SampleProvider gyroSampler, int minUpdateDelay, float minAngleChange) {
        super(gyroSampler, minUpdateDelay);
        this.minAngleChange = minAngleChange;

        lastUpdatedAngle = 0;

        List<OrientationAngleListener> list = new ArrayList<>();
        listenerList = Collections.synchronizedList(list);
    }

    @Override
    protected boolean valuesJustifyUpdate(float[] measuredValues) {
        float angleMeasured = measuredValues[0];
        return Math.abs(angleMeasured - lastUpdatedAngle) > minAngleChange;
    }

    @Override
    protected void notify(float[] measuredValues) {
        float angleMeasured = measuredValues[0];

        try {
            for (OrientationAngleListener listener : listenerList) {
                listener.angleChanged(angleMeasured);
            }
        } catch (Exception e) {
            log.error("Exception was thrown by an OrientationAngleListener. Automatic gyro polling will stop now.", e);
            throw e;
        }

        lastUpdatedAngle = angleMeasured;
    }

    @Override
    public void addListener(OrientationAngleListener angleListener) {
        listenerList.add(angleListener);
    }

    @Override
    public void removeListener(OrientationAngleListener angleListener) {
        listenerList.remove(angleListener);
    }
}
