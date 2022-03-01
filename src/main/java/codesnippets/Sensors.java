package codesnippets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ev3dev.sensors.ev3.EV3GyroSensor;
import ev3dev.sensors.ev3.EV3UltrasonicSensor;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

public final class Sensors {

    private static final Logger log = LoggerFactory.getLogger(Sensors.class);

    private Sensors() {
    }

    public static void gyroExample() {
        log.info("Gyro example");

        var gyroSensor = new EV3GyroSensor(SensorPort.S3);
        var gyroRotation = gyroSensor.getAngleMode();
        var rotationMeasurement = new float[gyroRotation.sampleSize()]; // sampleSize == 1
        // Calibration delay for the gyro.
        Delay.msDelay(4_000);

        for (int i = 0; i < 4; i++) {
            gyroRotation.fetchSample(rotationMeasurement, 0);
            log.info(String.format("current angle is %6.1f deg", rotationMeasurement[0]));
            Delay.msDelay(2_000);
        }
    }

    public static void ultrasoundExample() {
        log.info("Ultrasonic example");

        var ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
        var ultrasonicDistance = ultrasonicSensor.getDistanceMode();
        var distanceMeasurement = new float[ultrasonicDistance.sampleSize()]; // sampleSize == 1
        // Calibration delay for the ultrasonic.
        Delay.msDelay(4_000);

        for (int i = 0; i < 4; i++) {
            ultrasonicDistance.fetchSample(distanceMeasurement, 0);
            log.info(String.format("closest object is %5.1f cm away", distanceMeasurement[0]));
            Delay.msDelay(2_000);
        }
    }
}
