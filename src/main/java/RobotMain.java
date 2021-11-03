import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import ev3dev.sensors.Battery;
import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        // The robot is positioned an the very start of the 2m and is moved forward until its tip is approx. 5cm from
        // the end of the 2m. Times measured should be:
        // One full rotation per second - 10s - 11s
        //          ... every 2 seconds - 20s - 22s
        //          ... every 4 seconds - 38s - 42s
        //          ... every 8 seconds - 72s - 76s
        // (on carpet about 4% - 5% more time, but the board does not have carpet)

        // Value for motor speed of 360/2 deg/s
        int secFor2m = 21;

        motorLeft.forward();
        motorRight.forward();

        Delay.msDelay(secFor2m * 1000);

        motorLeft.stop();
        motorRight.stop();

        System.exit(0);
    }
}
