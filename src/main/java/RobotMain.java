import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    public static void main(final String[] args) {
        var motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
        var motorRight = new EV3LargeRegulatedMotor(MotorPort.C);

        int degreesPerSec = 360 / 2;
        motorLeft.setSpeed(degreesPerSec);
        motorRight.setSpeed(degreesPerSec);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping Motors");
            motorLeft.stop();
            motorRight.stop();
        }));

        log.info("Program start");

        // The robot is positioned slightly after the start of the 2m, like it would on the board. It is moved forward
        // until its tip is less than 10cm from the end of the 2m. Times measured should be:
        // 10s for a speed of one full rotation per second.
        // (on carpet about 4% - 5% more time, but the board does not have carpet)

        // Value for motor speed of 360/2 deg/s
        int secFor2m = 20;

        motorLeft.forward();
        motorRight.forward();

        Delay.msDelay(secFor2m * 1000);

        motorLeft.stop();
        motorRight.stop();

        System.exit(0);
    }
}
