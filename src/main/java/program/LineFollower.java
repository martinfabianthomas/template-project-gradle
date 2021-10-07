package program;

import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.movement.Movement;
import wrapper.act.movement.OrientationAwareMovement;
import wrapper.perception.color.line.LineDetection;
import wrapper.perception.color.line.LineDetectionListener;
import wrapper.perception.distance.ViewDistance;
import wrapper.perception.exception.UncalibratedSensorException;

public class LineFollower implements RobotProgram, LineDetectionListener {

    private static final Logger log = LoggerFactory.getLogger(LineFollower.class);

    private final Movement movement;
    private final EV3TouchSensor userInput;

    private boolean running;

    public LineFollower(Movement movement, EV3TouchSensor userInput) {
        this.movement = movement;
        this.userInput = userInput;
        running = false;
    }

    @Override
    public int start() {
        log.info("Waiting for user input");

        while (!userInput.isPressed()) {
            Delay.msDelay(100);
        }

        log.info("Start");
        running = true;

        movement.curve(1, 0.5f);
        Delay.msDelay(1000);

        while (!userInput.isPressed()) {
            Delay.msDelay(100);
        }

        return 0;
    }

    @Override
    public void overLine() {
        if (running) {
            movement.curve(1, 0.5f);
        }
    }

    @Override
    public void offLine() {
        if (running) {
            movement.curve(0.5f, 1);
        }
    }
}
