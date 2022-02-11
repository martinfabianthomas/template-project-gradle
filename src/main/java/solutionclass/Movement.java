package solutionclass;

import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Movement {

    private static final Logger log = LoggerFactory.getLogger(Movement.class);

    public final EV3LargeRegulatedMotor motorLeft;
    public final EV3LargeRegulatedMotor motorRight;

    public final int defaultDegreesPerSec;
    public final float secFor180deg;
    public final int secFor2m;

    private final SampleProvider gyroRotation;
    private final float[] rotationMeasurement;
    private final SampleProvider ultrasonicDistance;
    private final float[] distanceMeasurement;

    public Movement(Port leftPort, Port rightPort, SampleProvider gyroRotation, SampleProvider ultrasonicDistance) {
        motorLeft = new EV3LargeRegulatedMotor(leftPort);
        motorRight = new EV3LargeRegulatedMotor(rightPort);
        defaultDegreesPerSec = 360 / 2;
        secFor180deg = 41.25f / 20.0f;
        secFor2m = 20;

        motorLeft.setSpeed(defaultDegreesPerSec);
        motorRight.setSpeed(defaultDegreesPerSec);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping movement motors");
            motorLeft.stop();
            motorRight.stop();
        }));

        this.gyroRotation = gyroRotation;
        rotationMeasurement = new float[gyroRotation.sampleSize()];
        this.ultrasonicDistance = ultrasonicDistance;
        distanceMeasurement = new float[ultrasonicDistance.sampleSize()];
    }

    public void bothForward() {
        motorLeft.forward();
        motorRight.forward();
    }

    public void bothForwardFor(float sec) {
        // Added for exercise 6, but could have also been here from the start.
        bothForward();
        Delay.msDelay((int) (sec * 1000L));
        bothStop();
        Delay.msDelay(500);
    }

    public void bothStop() {
        motorLeft.stop();
        motorRight.stop();
    }

    public void turnLeft() {
        motorLeft.backward();
        motorRight.forward();
    }

    public void turnRight() {
        motorLeft.forward();
        motorRight.backward();
    }

    /**
     * Solution to exercise 1
     */
    public void twoMeterStraight() {
        bothForwardFor(secFor2m);
    }

    /**
     * Modified from solution to exercise 2.
     * Robot turns by the given angle to the right.
     */
    public void turnRightBy(int angle) {
        // The robots rotation prior to this call should be accounted for now.
        gyroRotation.fetchSample(rotationMeasurement, 0);
        var startingAngle = rotationMeasurement[0];

        turnRight();

        while (rotationMeasurement[0] - startingAngle < angle) {
            gyroRotation.fetchSample(rotationMeasurement, 0);
            Delay.msDelay(10);
        }

        bothStop();
        Delay.msDelay(500);
    }

    /**
     * Robot turns by the given angle to the left.
     */
    public void turnLeftBy(int angle) {
        // The robots rotation prior to this call should be accounted for now.
        gyroRotation.fetchSample(rotationMeasurement, 0);
        var startingAngle = rotationMeasurement[0];

        turnLeft();

        // For a rotation to the left the angle gets smaller.
        while (rotationMeasurement[0] - startingAngle > -angle) {
            gyroRotation.fetchSample(rotationMeasurement, 0);
            Delay.msDelay(10);
        }

        bothStop();
        Delay.msDelay(500);
    }

    /**
     * After this call the robots gyro is guaranteed to measure the given value.
     */
    public void moveToAbsoluteRotation(int angle) {
        // There are many ways of refactoring the last exercise into this class, depending on how it was solved.
        // This is the fastest as hardly any code needs to be altered.

        // switched to do-while as the robots rotation prior to this call has to be accounted for, unlike before.
        do {
            gyroRotation.fetchSample(rotationMeasurement, 0);
            if (rotationMeasurement[0] < angle) {
                turnRight();
            } else if (rotationMeasurement[0] > angle) {
                turnLeft();

                // This and the lower delay after this if, avoids endless seeming back-and-forth movement by the robot.
                Delay.msDelay(20);
            }

            Delay.msDelay(50);
            bothStop();
            Delay.msDelay(100);
        } while (rotationMeasurement[0] != angle);
    }

    public void moveUntilWall(int distance) {
        // Copied from last exercise.
        bothForward();
        do {
            ultrasonicDistance.fetchSample(distanceMeasurement, 0);
            Delay.msDelay(10);
        } while (distanceMeasurement[0] > distance);
        bothStop();
        Delay.msDelay(500);
    }
}
