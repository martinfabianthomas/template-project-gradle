import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    public static void main(final String[] args) {
        var motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
        var motorRight = new EV3LargeRegulatedMotor(MotorPort.C);

        // Separate speeds for driving and turning can be used, but are not needed.
        int degreesPerSec = 360 / 2;
        motorLeft.setSpeed(degreesPerSec);
        motorRight.setSpeed(degreesPerSec);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping Motors");
            motorLeft.stop();
            motorRight.stop();
        }));

        log.info("Program start");

        // The robot turns one wheel forward and its other backward. Measure one 180 deg turn or multiple 360 deg
        // turns and divide measured time accordingly. The times measured per 180 deg rotation should be about:
        // One full rotation per second - 1s
        //          ... every 2 seconds - 2s
        //          ... every 4 seconds - 4s
        //          ... every 8 seconds - 8.5s

        // This value represents the measurement of 41.25 seconds for 20 180 deg turns.
        float secFor180deg = 41.25f / 20.0f;

        // From last exercise:
        int secFor2m = 20;

        // As in exercise 1
        motorLeft.forward();
        motorRight.forward();
        Delay.msDelay(secFor2m * 1000);

        // Turn as per measurement.
        motorRight.backward();
        Delay.msDelay((int) (secFor180deg * 1000));

        // Drive back as in exercise 1
        motorRight.forward();
        Delay.msDelay(secFor2m * 1000);

        System.exit(0);
    }
}