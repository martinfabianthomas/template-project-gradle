package solutionclass;

import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
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

    public Movement(Port leftPort, Port rightPort) {
        // All copied from last exercises RobotMain.main()
        motorLeft = new EV3LargeRegulatedMotor(leftPort);
        motorRight = new EV3LargeRegulatedMotor(rightPort);
        defaultDegreesPerSec = 360 / 2;
        secFor180deg = 0.97f * (41.25f / 20.0f);
        secFor2m = 21;

        motorLeft.setSpeed(defaultDegreesPerSec);
        motorRight.setSpeed(defaultDegreesPerSec);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping movement motors");
            motorLeft.stop();
            motorRight.stop();
        }));
    }

    public void bothForward() {
        motorLeft.forward();
        motorRight.forward();
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
        bothForward();
        Delay.msDelay(secFor2m * 1000);
        bothStop();
        Delay.msDelay(500);
    }
}
