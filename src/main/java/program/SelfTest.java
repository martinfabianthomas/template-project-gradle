package program;

import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.grabber.BarGrabber;
import wrapper.act.movement.OrientationAwareMovement;
import wrapper.perception.color.line.LineDetectionListener;
import wrapper.perception.distance.ViewDistance;
import wrapper.perception.distance.ViewDistanceListener;
import wrapper.perception.orentation.Orientation;
import wrapper.perception.orentation.OrientationAngleListener;
import wrapper.perception.orentation.exceptions.UncalibratedGyroException;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This Program uses every sensor and motor once to check if it is working as intended.
 * May also be used as a reference for the usage of the given classes.
 */
public class SelfTest implements RobotProgram, LineDetectionListener, ViewDistanceListener, OrientationAngleListener {

    private static final Logger log = LoggerFactory.getLogger(SelfTest.class);
    private static final int overLineTrigger = 0;
    private static final int offLineTrigger = 1;
    private static final int distanceTrigger = 2;
    private static final int angleTrigger = 3;
    private static final int finishedListening= 4;

    private final OrientationAwareMovement movement;
    private final BarGrabber grabber;
    private final Orientation orientation;
    private final EV3TouchSensor userInput;

    private final Map<Integer, AtomicBoolean> listeningTo;

    private float angleBeforeRotationTest;

    public SelfTest(OrientationAwareMovement movement, BarGrabber grabber, Orientation orientation, EV3TouchSensor userInput) {
        this.movement = movement;
        this.grabber = grabber;
        this.orientation = orientation;
        this.userInput = userInput;

        listeningTo = Map.of(
                overLineTrigger, new AtomicBoolean(),
                offLineTrigger, new AtomicBoolean(),
                distanceTrigger, new AtomicBoolean(),
                angleTrigger, new AtomicBoolean(),
                finishedListening, new AtomicBoolean()
        );

        angleBeforeRotationTest = 0;
    }

    @Override
    public int start() {
        log.info("Self test ready to start");
        log.info("Waiting for user input");

        while (!userInput.isPressed()) {
            Delay.msDelay(100);
        }

        // Avoid stopping immediately in any of the whiles.
        Delay.msDelay(1000);

        log.info("Ultrasonic: forward until wall");
        movement.forward();
        listeningTo.get(distanceTrigger).set(true);

        // Only continue with execution here after all the asynchronous calls have finished.
        while (!listeningTo.get(finishedListening).get()) {
            Delay.msDelay(100);
        }

        log.info("Grabber-Motor: wave to celebrate");
        for (int i = 0; i < 6; i++) {
            grabber.rotateBy(-60, 100);
            Delay.msDelay(50);
            grabber.rotateBy(60, 100);
            Delay.msDelay(50);
        }

        log.info("End of self test");

        return 0;
    }

    @Override
    public void overLine() {
        // if listeningTo overLineTrigger then set it to false and don't return.
        // if not listeningTo overLineTrigger then set it to false (no change) and return.
        if (!listeningTo.get(overLineTrigger).getAndSet(false)) {
            return;
        }

        try {
            angleBeforeRotationTest = orientation.getAngle();
        } catch (UncalibratedGyroException exception) {
            log.error("Gyro: error while reading value", exception);
            listeningTo.get(finishedListening).set(true);
            return;
        }

        log.info("Gyro: waiting for user to rotate 180 degrees.");
        log.info(String.format("Currently at %.0f degrees. +/- 180 means > %.0f or < %.0f degrees.",
                angleBeforeRotationTest, angleBeforeRotationTest + 180, angleBeforeRotationTest - 180));

        movement.stop();
        listeningTo.get(angleTrigger).set(true);
    }

    @Override
    public void offLine() {
        // like overLine
        if (!listeningTo.get(offLineTrigger).getAndSet(false)) {
            return;
        }

        log.info("Color (reflection): now forward until off white paper");
        listeningTo.get(overLineTrigger).set(true);
    }

    @Override
    public void distanceChanged(float distance) {
        // like overLine
        if (distance > 20 || !listeningTo.get(distanceTrigger).getAndSet(false)) {
            return;
        }

        log.info("Wheel-Motors and Gyro: turn 90 degrees clockwise");
        movement.turnBy(90);

        log.info("Color (reflection): forward until on white paper");
        movement.forward();
        listeningTo.get(offLineTrigger).set(true);
    }

    @Override
    public void angleChanged(float angle) {
        // like overLine
        if (Math.abs(angle - angleBeforeRotationTest) < 180 || !listeningTo.get(angleTrigger).getAndSet(false)) {
            return;
        }

        log.info(String.format("Reached %.0f degrees", angle));
        listeningTo.get(finishedListening).set(true);
    }
}
