import ev3dev.actuators.lego.motors.EV3MediumRegulatedMotor;
import ev3dev.sensors.ev3.EV3GyroSensor;
import ev3dev.sensors.ev3.EV3UltrasonicSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import solutionclass.Movement;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    public static void main(final String[] args) {
        var gyroSensor = new EV3GyroSensor(SensorPort.S3);
        var gyroRotation = gyroSensor.getAngleMode();
        var rotationMeasurement = new float[gyroRotation.sampleSize()];

        var ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S2);
        var ultrasonicDistance = ultrasonicSensor.getDistanceMode();
        var distanceMeasurement = new float[ultrasonicDistance.sampleSize()];

        // Calibration delay for the ultrasonic.
        Delay.msDelay(4000);

        var movement = new Movement(MotorPort.B, MotorPort.C, gyroRotation);
        var grabber = new EV3MediumRegulatedMotor(MotorPort.A);

        log.info("Program start");

        // As in exercise 1
        movement.twoMeterStraight();

        // As is exercise 4
        movement.turnRightBy(90);
        Delay.msDelay(200);
        movement.turnRightBy(90);
        grabber.rotate(-120);
        movement.turnLeftBy(180);
        grabber.rotate(120);
        movement.turnRightBy(90);

        // After turning right thrice and left only once, the robot will overshoot the 90 deg towards the positive.
        // There are many solutions for this, like in exercise 3. Some are: a correction term, saving the error from a
        // turnLeftBy / turnRightBy call and accounting for it next time, correcting on the forwards like in exercise 3
        // or like in the following code, which is mostly the same as in exercise 3, but without moving forward.
        while (rotationMeasurement[0] != 90) {
            gyroRotation.fetchSample(rotationMeasurement, 0);
            if (rotationMeasurement[0] < 90) {
                movement.turnRight();
            } else if (rotationMeasurement[0] > 90) {
                movement.turnLeft();
            }

            Delay.msDelay(100);
            movement.bothStop();
            Delay.msDelay(100);
        }

        // Move until wall. This part is simple now.
        movement.bothForward();
        do {
            ultrasonicDistance.fetchSample(distanceMeasurement, 0);
            Delay.msDelay(10);
        } while (distanceMeasurement[0] > 15);
        movement.bothStop();
    }
}
