import ev3dev.actuators.lego.motors.EV3MediumRegulatedMotor;
import ev3dev.sensors.ev3.EV3GyroSensor;
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
        // Calibration delay for the gyro.
        Delay.msDelay(4000);

        var movement = new Movement(MotorPort.B, MotorPort.C, gyroRotation);
        var grabber = new EV3MediumRegulatedMotor(MotorPort.A);

        log.info("Program start");

        // As in exercise 1
        movement.twoMeterStraight();

        // Turn according to gyro.
        movement.turnRightBy(90);
        Delay.msDelay(200);

        // Move obstacle. Adding extra reach to the grabber is likely needed.
        movement.turnRightBy(90);
        // Note also that this call, unlike forward + Delay, can bring the whole program to a halt, if the arm is not
        // able to move 120 deg due to its initial position.
        grabber.rotate(-120);
        movement.turnLeftBy(180);
        grabber.rotate(120);
        movement.turnRightBy(90);

        // Move forward a little.
        movement.bothForward();
        Delay.msDelay(3000);
        movement.bothStop();
    }
}
