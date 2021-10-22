package wrapper.act.movement;

import lejos.hardware.port.Port;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.perception.orentation.OrientationAngleListener;

/**
 * Moves the entire robot by moving both wheel motors. Functions forward(), turnClockwise(), ... assume the robot is
 * build and positioned as per the LEGO instructions.
 *
 * <p>
 *     Also offers the ability to turn by exact amount.
 * </p>
 */
public class OrientationAwareMovement extends Movement implements OrientationAngleListener {

    private static final Logger log = LoggerFactory.getLogger(OrientationAwareMovement.class);

    private static final int defaultTurnSpeed = 50;

    private boolean turning;
    private float currentAngle;

    private float startingAngle;
    private float commandAngle;


    public OrientationAwareMovement(Port leftPort, Port rightPort) {
        super(leftPort, rightPort);

        currentAngle = 0.0f;
        turning = false;
    }

    @Override
    public void angleChanged(float angle) {
        currentAngle = angle;

        if (!turning) {
            return;
        }

        if (commandAngle < 0) {
            if (currentAngle > startingAngle + commandAngle) {
                return;
            }
        } else {
            if (currentAngle < startingAngle + commandAngle) {
                return;
            }
        }

        stop();

        turning = false;
    }

    /**
     * Rotates the robot by an exact amount.
     *
     * <p>
     *     Function only returns after completion.
     * </p>
     *
     * @param degrees Robot is rotated by this amount. Non-mathematical notation:
     *                Positive -> clockwise (looking from above).
     */
    public void turnBy(float degrees) {
        turnBy(degrees, defaultTurnSpeed);
    }

    public void turnBy(float degrees, int speed) {
        if (degrees == 0.0f) {
            return;
        }

        commandAngle = degrees;

        startingAngle = currentAngle;
        turning = true;

        if (degrees < 0) {
            turnCounterclockwise(speed);
        } else {
            turnClockwise(speed);
        }

        // just like the ev3dev-lang-java implementation
        while (turning) {
            Delay.msDelay(10);
        }
    }

    /**
     * Returns true while a turnBy() call is ongoing.
     */
    public boolean isTurning() {
        return turning;
    }
}
