package wrapper.perception.distance;

import ev3dev.sensors.ev3.EV3UltrasonicSensor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.enums.StopCommand;
import wrapper.perception.distance.exception.UncalibratedUltrasonicException;
import wrapper.perception.orentation.OrientationAngleListener;
import wrapper.perception.orentation.exceptions.UncalibratedGyroException;

import java.util.Timer;

public class ViewDistance implements ViewDistancePublisher {

    private static final Logger log = LoggerFactory.getLogger(ViewDistance.class);

    private final EV3UltrasonicSensor ultrasonic;
    private final SampleProvider distanceSampler;
    private final float[] distanceSample;
    private final UltrasonicPollingTask pollingTask;

    private boolean isCalibrated;

    /**
     * Creates an uncalibrated Orientation object.
     *
     * @param ultrasonicPort Port of the ultrasonic sensor.
     */
    public ViewDistance(Port ultrasonicPort) {
        ultrasonic = new EV3UltrasonicSensor(ultrasonicPort);
        distanceSampler = ultrasonic.getDistanceMode();
        distanceSample = new float[distanceSampler.sampleSize()];
        pollingTask = new UltrasonicPollingTask(distanceSampler, 100, 1);

        isCalibrated = false;
    }

    /**
     * Ensures the Ultrasonic sensor is sending correct values. Calibration takes up to 3 seconds. Also starts the automatic
     * notification of ViewDistanceListener.
     */
    public void calibrate() {
        isCalibrated = false;

        // Ultrasonic sensor starts off only sending only 0.0. From my testing it takes up to 2 seconds,
        // using 2.5 to be safe. If any value other than 0.0 is received the calibration is finished.
        int msElapsed = 0;
        int msPerIteration = 100;
        distanceSampler.fetchSample(distanceSample, 0);
        while (distanceSample[0] == 0.0 && msElapsed < 2500) {
            Delay.msDelay(msPerIteration);
            msElapsed += msPerIteration;
            distanceSampler.fetchSample(distanceSample, 0);
        }

        Timer timer = new Timer("ultrasonic-polling");

        // Sensor updates with frequency of 1kHz.
        timer.scheduleAtFixedRate(pollingTask, 100L, 1L);

        isCalibrated = true;
    }

    private void explicitCalibrationCheck() throws UncalibratedUltrasonicException {
        if (!isCalibrated) {
            UncalibratedUltrasonicException calibrationError = new UncalibratedUltrasonicException();
            log.error("Ultrasonic measurements were accessed before calibration was called explicitly.",
                    calibrationError);
            throw calibrationError;
        }
    }

    /**
     * Returns the current distance from the ultrasonic sensor to the closes object in a straight line in cm.
     *
     * @throws UncalibratedUltrasonicException if this function is called before calibration.
     */
    public float getDistance() throws UncalibratedUltrasonicException {
        explicitCalibrationCheck();

        distanceSampler.fetchSample(distanceSample, 0);
        return distanceSample[0];
    }

    @Override
    public void addListener(ViewDistanceListener distanceListener) {
        pollingTask.addListener(distanceListener);
    }

    @Override
    public void removeListener(ViewDistanceListener distanceListener) {
        pollingTask.removeListener(distanceListener);
    }
}
