package wrapper.act.grabber;

import ev3dev.actuators.lego.motors.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.enums.StopCommand;

/**
 * A grabbing extension to the driving base as recommended by LEGO
 */
public class BarGrabber {

    private static final Logger log = LoggerFactory.getLogger(BarGrabber.class);

    private static final int defaultRotationSpeed = 75;
    private static final StopCommand defaultStopCommand = StopCommand.HOLD;

    private final EV3MediumRegulatedMotor motor;

    public BarGrabber(Port mediumMotorPort) {
        motor = new EV3MediumRegulatedMotor(mediumMotorPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping bar-grabber motor");
            stop(StopCommand.COAST);
        }));
    }

    /**
     * Starts lifting the bar.
     */
    public void up() {
        up(defaultRotationSpeed);
    }

    public void up(int speed) {
        motor.setSpeed(speed);
        motor.forward();
    }


    /**
     * Starts lowering the bar.
     */
    public void down() {
        down(defaultRotationSpeed);
    }

    public void down(int speed) {
        motor.setSpeed(speed);
        motor.backward();
    }

    /**
     * Rotates the Bar by an exact amount and stops.
     *
     * <p>
     *     Function only returns after completion.
     * </p>
     * @param degrees Bar is rotated by this amount. Positive -> up, negative -> down.
     */
    public void rotateBy(int degrees) {
        rotateBy(degrees, defaultRotationSpeed);
    }

    public void rotateBy(int degrees, int speed) {
        motor.setSpeed(speed);
        motor.rotate(degrees);
    }

    /**
     * Stops the current motion.
     */
    public void stop() {
        stop(defaultStopCommand);
    }

    public void stop(StopCommand command) {
        switch (command) {
            case HOLD:
                motor.hold();
                break;
            case BRAKE:
                motor.brake();
                break;
            case COAST:
                motor.coast();
                break;
        }
    }
}
