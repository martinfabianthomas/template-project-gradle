package wrapper.perception.observerpattern;

import lejos.robotics.SampleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.perception.orentation.GyroPollingTask;

import java.util.TimerTask;

/**
 * Enables use of the Observer Pattern. If scheduled at fixed rate using a Timer, it allows notification of the
 * listeners even for continuously changing values.
 */
public abstract class SensorPollingTask extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(SensorPollingTask.class);

    private final int minUpdateDelay;
    private final SampleProvider sampler;
    private final float[] sample;

    private long lastUpdateMillis;

    public SensorPollingTask(SampleProvider sensorSampler, int minUpdateDelay) {
        this.minUpdateDelay = minUpdateDelay;
        sampler = sensorSampler;
        sample = new float[sampler.sampleSize()];

        lastUpdateMillis = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (System.currentTimeMillis() - lastUpdateMillis < minUpdateDelay) {
            return;
        }

        sampler.fetchSample(sample, 0);
        if (!valuesJustifyUpdate(sample)) {
            return;
        }

        notify(sample);

        lastUpdateMillis = System.currentTimeMillis();
    }

    protected abstract boolean valuesJustifyUpdate(float[] measuredValues);

    protected abstract void notify(float[] measuredValues);
}
