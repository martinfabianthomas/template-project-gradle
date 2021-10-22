package program;

import ev3dev.sensors.Battery;
import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.grabber.BarGrabber;
import wrapper.act.movement.OrientationAwareMovement;
import wrapper.perception.color.line.LineDetectionListener;
import wrapper.perception.distance.ViewDistance;
import wrapper.perception.exception.UncalibratedSensorException;
import wrapper.perception.orentation.Orientation;

public class FastSelfTest implements RobotProgram, LineDetectionListener {

    private static final Logger log = LoggerFactory.getLogger(FastSelfTest.class);

    private final OrientationAwareMovement movement;
    private final BarGrabber grabber;
    private final ViewDistance distance;
    private final Orientation orientation;
    private final Battery battery;
    private final EV3TouchSensor userInput;
    private final int numberOfSensorPrints;

    private boolean robotOverLine;

    public FastSelfTest(OrientationAwareMovement movement, BarGrabber grabber, ViewDistance distance,
                        Orientation orientation, Battery battery, EV3TouchSensor userInput, int numberOfSensorPrints) {
        this.movement = movement;
        this.grabber = grabber;
        this.distance = distance;
        this.orientation = orientation;
        this.battery = battery;
        this.userInput = userInput;
        this.numberOfSensorPrints = numberOfSensorPrints;
    }

    @Override
    public int start() {
        try {
            logAllSensors();
        } catch (UncalibratedSensorException exception) {
            log.error("Error while reading sensor value", exception);
            return 1;
        }

        log.info("Motor test");

        for (int i = 0; i < 3; i++) {
            grabber.rotateBy(-90, 125);
            Delay.msDelay(50);
            grabber.rotateBy(90, 125);
            Delay.msDelay(50);
        }

        for (int i = 0; i < 4; i++) {
            movement.forward(200);
            Delay.msDelay(2000);
            movement.turnBy(90, 100);
        }

        log.info("=====================");

        for (int print = 1; print < numberOfSensorPrints; print++) {
            try {
                logAllSensors();
            } catch (UncalibratedSensorException exception) {
                log.error("Error while reading sensor value", exception);
                return 1;
            }

            Delay.msDelay(5000);
        }

        return 0;
    }

    @Override
    public void overLine() {
        robotOverLine = true;
    }

    @Override
    public void offLine() {
        robotOverLine = false;
    }

    private void logAllSensors() throws UncalibratedSensorException {
        float distanceMeasured = distance.getDistance();
        String distanceDisplayed = (distanceMeasured == Float.POSITIVE_INFINITY ? ">255" : "" + distanceMeasured);
        String touchDisplayed = (userInput.isPressed() ? "pressed" : "not pressed");
        String lineSurface = (robotOverLine ? "dark" : "white");

        log.info(String.format("Ultrasonic: %scm", distanceDisplayed));
        log.info(String.format("Gyro: %.0f degrees", orientation.getAngle()));
        log.info(String.format("Touch: %s", touchDisplayed));
        log.info(String.format("Color (line detection): looking at a %s surface", lineSurface));
        log.info(String.format("Battery: %.1fV", battery.getVoltage()));
        log.info("=====================");
    }
}
