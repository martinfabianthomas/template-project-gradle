package wrapper.perception.color.reflection;

import lejos.robotics.SampleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.perception.distance.UltrasonicPollingTask;
import wrapper.perception.observerpattern.SensorPollingTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectionPollingTask extends SensorPollingTask implements ReflectionPublisher {

    private static final Logger log = LoggerFactory.getLogger(UltrasonicPollingTask.class);

    private final float minReflectionChange;
    private final List<ReflectionListener> listenerList;

    private float lastUpdatedReflection;

    /**
     * Creates an ReflectionPollingTask that sends updates to the registered listeners if the measurements retrieved
     * from the color sensor in reflection mode change.
     *
     * @param colorPort SampleProvider of the color sensor.
     * @param minUpdateDelay Will not send updates more frequently than this value in ms.
     * @param minReflectionChange Will not send updates if the value changed less than this value (from 0 to 100).
     */
    public ReflectionPollingTask(SampleProvider colorPort, int minUpdateDelay, float minReflectionChange) {
        super(colorPort, minUpdateDelay);
        this.minReflectionChange = minReflectionChange;

        lastUpdatedReflection = 0;

        List<ReflectionListener> list = new ArrayList<>();
        listenerList = Collections.synchronizedList(list);
    }

    @Override
    protected boolean valuesJustifyUpdate(float[] measuredValues) {
        float distanceMeasured = measuredValues[0];
        return Math.abs(distanceMeasured - lastUpdatedReflection) > minReflectionChange;
    }

    @Override
    protected void notify(float[] measuredValues) {
        float reflectionMeasured = measuredValues[0];

        try {
            for (ReflectionListener listener : listenerList) {
                listener.strengthOfReflectionChanged(reflectionMeasured);
            }
        } catch (Exception e) {
            log.error("Exception was thrown by an ReflectionListener. Automatic ultrasonic polling will stop now.", e);
            throw e;
        }

        lastUpdatedReflection = reflectionMeasured;
    }

    @Override
    public void addListener(ReflectionListener reflectionListener) {
        listenerList.add(reflectionListener);
    }

    @Override
    public void removeListener(ReflectionListener reflectionListener) {
        listenerList.remove(reflectionListener);
    }
}
