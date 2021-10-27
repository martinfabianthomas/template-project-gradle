import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
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
        var userInput = new EV3TouchSensor(SensorPort.S4);

        int degreesPerSec = 360 / 2;
        motorLeft.setSpeed(degreesPerSec);
        motorRight.setSpeed(degreesPerSec);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping Motors");
            motorLeft.stop();
            motorRight.stop();
        }));

        log.info("Program start");
        while(!userInput.isPressed()) {
            Delay.msDelay(50);
        }

        // Measured times for 2m distance:
        //                               normal | when on carpet
        // One full rotation per second - 11.58 | 12.45
        //          ... every 2 seconds - 23.14 | 24.73
        //          ... every 4 seconds - 47.28 | 49.46

        // Educated guess for cm -> sec conversion at speed 360 / 2:
        float cmToSecFactor = 25.0f / 200.0f;

        // Optional improvement
        //cmToSecRatio *= 0.94;
        // Optional improvement for carpet
        //cmToSecRatio *= 0.98;

        motorLeft.forward();
        motorRight.forward();

        // Move slightly less than 2m forward:
        // Robot is approximately 20cm long (without measuring tape 10cm is a very conservative guess)
        // We can safely subtract the 20cm mentioned in the exercise without getting to close to edge, since
        // both robot length and speed underestimate how close the robot actually gets.
        Delay.msDelay((int) ((200 - 20 - 10) * cmToSecFactor * 1000));

        System.exit(0);
    }
}
