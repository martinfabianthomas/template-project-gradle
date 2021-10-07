package program;

import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.movement.OrientationAwareMovement;
import wrapper.perception.distance.ViewDistance;
import wrapper.perception.distance.exception.UncalibratedUltrasonicException;

import java.sql.Struct;

/**
 * A program in which the robot "bounces" between two walls.
 */
public class PingPong implements RobotProgram {

    private static final Logger log = LoggerFactory.getLogger(PingPong.class);

    private final OrientationAwareMovement movement;
    private final ViewDistance distance;
    private final EV3TouchSensor userInput;
    private final int bounces;
    private final boolean bounce_limit;

    public PingPong(OrientationAwareMovement movement, ViewDistance distance, EV3TouchSensor userInput) {
        this.movement = movement;
        this.distance = distance;
        this.userInput = userInput;
        bounces = 0;
        bounce_limit = false;
    }

    public PingPong(OrientationAwareMovement movement, ViewDistance distance, EV3TouchSensor userInput,
                    int numberOfBounces) {
        this.movement = movement;
        this.distance = distance;
        this.userInput = userInput;
        bounces = numberOfBounces;
        bounce_limit = true;
    }

    @Override
    public int start() {
        log.info("Waiting for user input");

        while (!userInput.isPressed()) {
            Delay.msDelay(100);
        }

        // Avoid stopping immediately in do-while.
        Delay.msDelay(1000);

        log.info("Start");

        for (int bounce = 0; bounce < bounces && bounce_limit; bounce++) {
            movement.forward();

            float distanceMeasured;
            do {
                if (userInput.isPressed()) {
                    return 0;
                }

                try{
                    distanceMeasured = distance.getDistance();
                } catch (UncalibratedUltrasonicException exception) {
                    log.error("Someone did not set up my dependencies", exception);
                    return 1;
                }

                Delay.msDelay(100);
            } while (distanceMeasured > 20);

            log.info("Turning");
            movement.turnBy(180);
        }

        return 0;
    }
}
