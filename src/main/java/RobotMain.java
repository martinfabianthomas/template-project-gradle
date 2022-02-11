import ev3dev.actuators.lego.motors.EV3MediumRegulatedMotor;
import ev3dev.sensors.ev3.EV3GyroSensor;
import ev3dev.sensors.ev3.EV3UltrasonicSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutionclass.Movement;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    private static SampleProvider gyroRotation;
    private static float[] rotationMeasurement;
    private static SampleProvider ultrasonicDistance;
    private static float[] distanceMeasurement;
    private static Movement movement;
    private static EV3MediumRegulatedMotor grabber;

    public static void main(final String[] args) {
        var gyroSensor = new EV3GyroSensor(SensorPort.S3);
        gyroRotation = gyroSensor.getAngleMode();
        rotationMeasurement = new float[gyroRotation.sampleSize()];

        var ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
        ultrasonicDistance = ultrasonicSensor.getDistanceMode();
        distanceMeasurement = new float[ultrasonicDistance.sampleSize()];

        // Calibration delay.
        Delay.msDelay(4000);

        movement = new Movement(MotorPort.B, MotorPort.C, gyroRotation, ultrasonicDistance);
        grabber = new EV3MediumRegulatedMotor(MotorPort.A);

        log.info("Program start");

        exercise1();
        exercise4();
        exercise5();

        // Start of exercise 6
        movement.turnRightBy(90);
        movement.moveToAbsoluteRotation(180);
        movement.moveUntilWall(15);

        movement.moveToAbsoluteRotation(180);
        // The following code slowly steps past the initial face of wall and then uses the same code to also get
        // the width of the wall right. The second part is optional and can also just be guessed.
        var wallLengthInSec = stepPastWall();
        // The robot repositions itself to find the width.
        movement.bothForwardFor(3);
        movement.turnLeftBy(90);

        stepPastWall();

        movement.bothForwardFor(wallLengthInSec);
    }

    private static void exercise1() {
        movement.twoMeterStraight();
    }

    private static void exercise4() {
        movement.turnRightBy(90);

        Delay.msDelay(200);
        movement.turnRightBy(90);
        grabber.rotate(-120);
        movement.turnLeftBy(180);
        grabber.rotate(120);

        movement.turnRightBy(90);
        // Improvement from exercise 5.
        movement.moveToAbsoluteRotation(90);
    }

    private static void exercise5() {
        movement.moveUntilWall(15);
    }

    private static void stepAlongWall(float secForward, int facingWallAngle) {
        movement.turnRightBy(90);
        // If this is not called here, the robot will slowly move further away from the wall.
        movement.moveToAbsoluteRotation(facingWallAngle + 90);

        movement.bothForwardFor(secForward);

        movement.turnLeftBy(90);
        // Another call here to ensure accurate measurements.
        movement.moveToAbsoluteRotation(facingWallAngle);
    }

    /**
     * Takes steps along the wall the robot is facing and returns an indication of the walls length.
     * @return number of seconds that were required to move past the wall.
     */
    private static float stepPastWall() {
        gyroRotation.fetchSample(rotationMeasurement, 0);
        var facingWallAngle = (int) rotationMeasurement[0];
        var nextToWall = true;
        var secondsInMotion = 0.0f;
        var secondsPerStep = 1.5f;

        while (nextToWall) {
            stepAlongWall(secondsPerStep, facingWallAngle);
            secondsInMotion += secondsPerStep;

            ultrasonicDistance.fetchSample(distanceMeasurement, 0);
            // The condition that is checked uses twice the argument passed to moveUntilWall.
            nextToWall = distanceMeasurement[0] < 20;
        }

        // Since there is extra space in the lane that can be used and the robot should not collide with the wall, an
        // extra step should be taken.
        stepAlongWall(1, facingWallAngle);
        secondsInMotion += 1.5f;

        // This function should leave the robot in the original orientation.
        movement.moveToAbsoluteRotation(facingWallAngle);

        return secondsInMotion;
    }
}
