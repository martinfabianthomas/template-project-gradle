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

import java.util.Map;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    private static final Map<Integer, String> colorNames = Map.of(
            0, "none",
            1, "black",
            2, "blue",
            3, "green",
            4, "yellow",
            5, "red",
            6, "white",
            7, "brown"
    );

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

        // Signature changed.
        movement = new Movement(MotorPort.B, MotorPort.C, gyroRotation, ultrasonicDistance);
        grabber = new EV3MediumRegulatedMotor(MotorPort.A);

        log.info("Program start");

        // This step could also have been done earlier or later than this exercise.
        exercise1();
        exercise4();
        exercise5();
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
        movement.moveUntilWall(20);
    }
}
