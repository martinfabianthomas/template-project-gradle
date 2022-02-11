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
        // Creating a separate class is not necessary, but makes the code more readable.
        var movement = new Movement(MotorPort.B, MotorPort.C);

        // This part was given
        var gyroSensor = new EV3GyroSensor(SensorPort.S3);
        var gyroRotation = gyroSensor.getAngleMode();
        var rotationMeasurement = new float[gyroRotation.sampleSize()];
        // Calibration delay for the gyro.
        Delay.msDelay(4000);

        log.info("Program start");

        // As in exercise 1
        movement.twoMeterStraight();

        // Turn according to gyro.
        movement.turnRight();
        // The following code naturally turns the robot too much. There are two valid solutions:
        // 1. finding an appropriate correction term through trial and error
        // 2. correcting the robots movement on the way back (harder)
        while (rotationMeasurement[0] < 180) {
            gyroRotation.fetchSample(rotationMeasurement, 0);
            Delay.msDelay(10);
        }
        // From some tests it looks like stopping the motors here limits the amount by which it overshoots the 180 deg.
        //movement.bothStop();
        //Delay.msDelay(500);

        // 2. Solution: Similar behaviour to movement.twoMeterStraight, but once per second the robot might turn
        // to correct its angle.
        for (int sec = 0; sec < movement.secFor2m; sec++) {
            movement.bothForward();
            Delay.msDelay(1000);

            gyroRotation.fetchSample(rotationMeasurement, 0);
            if (rotationMeasurement[0] < 180) {
                movement.turnRight();
            } else if (rotationMeasurement[0] > 180) {
                movement.turnLeft();
            } else {
                continue;
            }

            Delay.msDelay(100);
        }

        System.exit(0);
    }
}
