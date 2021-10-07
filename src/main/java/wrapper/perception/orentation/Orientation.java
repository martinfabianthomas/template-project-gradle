package wrapper.perception.orentation;

import ev3dev.sensors.ev3.EV3GyroSensor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.perception.orentation.exceptions.UncalibratedGyroException;

import java.util.Timer;

/**
 * Supplies information about the robots orientation using the Gyroscopic sensor.
 */
public class Orientation implements OrientationAnglePublisher {

    private static final Logger log = LoggerFactory.getLogger(Orientation.class);

    private final EV3GyroSensor gyro;
    private float[] gyroSample;
    private GyroPollingTask pollingTask;
    private SampleProvider gyroSampler;

    private boolean isCalibrated;

    /**
     * Creates an uncalibrated Orientation object.
     */
    public Orientation(Port gyroPort) {
        gyro = new EV3GyroSensor(gyroPort);

        isCalibrated = false;
    }

    /**
     * Ensures the Gyro gets correctly calibrated. Calibration takes about 3 seconds. Also starts the automatic
     * notification of OrientationAngleListener.
     *
     * @throws UncalibratedGyroException if the robot was moved during calibration.
     */
    public void calibrate() throws UncalibratedGyroException {
        isCalibrated = false;

        gyro.reset();
        // Unlike the other sensors this sample provider needs to be assigned here / again
        // or it only returns values form 0 to 255.
        gyroSampler = gyro.getAngleMode();
        gyroSample = new float[gyroSampler.sampleSize()];
        pollingTask = new GyroPollingTask(gyroSampler, 100, 5);

        // Gyro sensor needs some time to calibrate. From my testing it takes up to 2 seconds, using 2.5 to be safe.
        // (Robot should be completely still at this time)
        Delay.msDelay(2500);

        gyroSampler.fetchSample(gyroSample, 0);
        float angleSample = gyroSample[0];

        // Verify correct calibration.
        if (angleSample != 0.0) {
            UncalibratedGyroException calibrationError = new UncalibratedGyroException(angleSample);
            log.error("Gyro was moved during calibration.", calibrationError);
            throw calibrationError;
        }

        Timer timer = new Timer("gyro-polling");

        // Sensor updates with frequency of 1kHz.
        timer.scheduleAtFixedRate(pollingTask, 100L, 1L);

        isCalibrated = true;
    }

    private void explicitCalibrationCheck() throws UncalibratedGyroException {
        if (!isCalibrated) {
            UncalibratedGyroException calibrationError = new UncalibratedGyroException();
            log.error("Gyro measurements were accessed before calibration was called explicitly.", calibrationError);
            throw calibrationError;
        }
    }

    /**
     * Returns the current angle of the robot relative to how it was oriented during calibration.
     *
     * @throws UncalibratedGyroException if this function is called before calibration.
     */
    public float getAngle() throws UncalibratedGyroException {
        explicitCalibrationCheck();

        gyroSampler.fetchSample(gyroSample, 0);
        return gyroSample[0];
    }

    @Override
    public void addListener(OrientationAngleListener angleListener) {
        pollingTask.addListener(angleListener);
    }

    @Override
    public void removeListener(OrientationAngleListener angleListener) {
        pollingTask.removeListener(angleListener);
    }
}
