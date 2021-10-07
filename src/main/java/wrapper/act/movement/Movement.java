package wrapper.act.movement;

import wrapper.act.enums.StopCommand;
import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Movement {

    private static final Logger log = LoggerFactory.getLogger(Movement.class);

    private static final int defaultDriveSpeed = 100;
    private static final StopCommand defaultStopCommand = StopCommand.HOLD;

    private final EV3LargeRegulatedMotor motorLeft;
    private final EV3LargeRegulatedMotor motorRight;

    public Movement(Port leftPort, Port rightPort) {
        motorLeft = new EV3LargeRegulatedMotor(leftPort);
        motorRight = new EV3LargeRegulatedMotor(rightPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping movement motors");
            stop(StopCommand.COAST);
        }));
    }

    /**
     * Starts moving the robot forward.
     */
    public void forward() {
        forward(defaultDriveSpeed);
    }

    public void forward(int speed) {
        motorLeft.setSpeed(speed);
        motorRight.setSpeed(speed);

        motorLeft.forward();
        motorRight.forward();
    }

    /**
     * Starts moving the robot backward.
     */
    public void backward() {
        backward(defaultDriveSpeed);
    }

    public void backward(int speed) {
        motorLeft.setSpeed(speed);
        motorRight.setSpeed(speed);

        motorLeft.backward();
        motorRight.backward();
    }

    /**
     * Starts rotating the robot in a clockwise direction (looking from above).
     */
    public void turnClockwise() {
        turnClockwise(defaultDriveSpeed);
    }

    public void turnClockwise(int speed) {
        motorLeft.setSpeed(speed);
        motorRight.setSpeed(speed);

        motorLeft.forward();
        motorRight.backward();
    }

    /**
     * Starts driving a curve based on different speeds in the motors.
     *
     * @param leftSpeed percentage of default speed to use for left motor.
     * @param rightSpeed percentage of default speed to use for right motor.
     */
    public void curve(float leftSpeed, float rightSpeed) {
        curve(leftSpeed, rightSpeed, defaultDriveSpeed);
    }

    public void curve(float leftSpeed, float rightSpeed, int speed) {
        motorLeft.setSpeed((int) (leftSpeed * speed));
        motorRight.setSpeed((int) (rightSpeed * speed));

        motorLeft.forward();
        motorRight.forward();
    }

    /**
     * Starts driving a curve based on different speeds in the motors.
     *
     * @param leftSpeed percentage of default speed to use for left motor.
     * @param rightSpeed percentage of default speed to use for right motor.
     */
    public void curveBackward(float leftSpeed, float rightSpeed) {
        curveBackward(leftSpeed, rightSpeed, defaultDriveSpeed);
    }

    public void curveBackward(float leftSpeed, float rightSpeed, int speed) {
        motorLeft.setSpeed((int) (leftSpeed * speed));
        motorRight.setSpeed((int) (rightSpeed * speed));

        motorLeft.backward();
        motorRight.backward();
    }

    /**
     * Starts rotating the robot in a counterclockwise direction (looking from above).
     */
    public void turnCounterclockwise() {
        turnCounterclockwise(defaultDriveSpeed);
    }

    public void turnCounterclockwise(int speed) {
        motorLeft.setSpeed(speed);
        motorRight.setSpeed(speed);

        motorLeft.backward();
        motorRight.forward();
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
                motorLeft.hold();
                motorRight.hold();
                break;
            case BRAKE:
                motorLeft.brake();
                motorRight.brake();
                break;
            case COAST:
                motorLeft.coast();
                motorRight.coast();
                break;
        }
    }
}
