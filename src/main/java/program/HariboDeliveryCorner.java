package programms;

import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.grabber.BarGrabber;
import wrapper.act.movement.Movement;
import wrapper.act.movement.OrientationAwareMovement;
import wrapper.perception.color.line.LineDetectionListener;
import wrapper.perception.distance.ViewDistance;
import wrapper.perception.distance.exception.UncalibratedUltrasonicException;

public class HariboDeliveryCorner implements RobotProgram, LineDetectionListener {

    private static final Logger log = LoggerFactory.getLogger(HariboDeliveryCorner.class);

    private final OrientationAwareMovement movement;
    private final BarGrabber grabber;
    private final ViewDistance distance;
    private final EV3TouchSensor userInput;

    private boolean running;
    private boolean reachedCornerOnce;

    private long systemMillisStart;
    private long millisSpentBeforeCorner;
    private long systemMillisCorner;
    private long millisSpentAfterCorner;

    public HariboDeliveryCorner(OrientationAwareMovement movement, BarGrabber grabber, ViewDistance distance, EV3TouchSensor userInput) {
        this.movement = movement;
        this.grabber = grabber;
        this.distance = distance;
        this.userInput = userInput;
    }

    @Override
    public int start() {
        log.info("Waiting for user input");

        while (!userInput.isPressed()) {
            Delay.msDelay(100);
        }

        log.info("Start");
        running = true;

        movement.forward();
        systemMillisStart = System.currentTimeMillis();
        Delay.msDelay(1000);

        while (!reachedCornerOnce) {
            if (userInput.isPressed()) {
                return 0;
            }
            Delay.msDelay(100);
        }

        movement.forward();
        systemMillisCorner = System.currentTimeMillis();

        try {
            while (distance.getDistance() > 10) {
                if (userInput.isPressed()) {
                    return 0;
                }
                Delay.msDelay(100);
            }
        } catch (UncalibratedUltrasonicException exception) {
            log.error("Someone did not set up my dependencies", exception);
            return 1;
        }

        Delay.msDelay(500);
        movement.stop();
        millisSpentAfterCorner = System.currentTimeMillis() - systemMillisCorner;
        grabber.rotateBy(-60);

        movement.backward();
        Delay.msDelay(millisSpentAfterCorner);
        movement.turnBy(90);
        movement.backward();
        Delay.msDelay(millisSpentBeforeCorner);
        movement.stop();

        return 0;
    }

    @Override
    public void overLine() {
        if (running && !reachedCornerOnce) {
            millisSpentBeforeCorner = System.currentTimeMillis() - systemMillisStart;
            movement.turnBy(-90);
            reachedCornerOnce = true;
        }
    }

    @Override
    public void offLine() {
        // ignore
    }
}
