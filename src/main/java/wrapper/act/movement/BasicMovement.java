package wrapper.act.movement;

import lejos.hardware.port.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Moves the entire robot by moving both wheel motors. Functions forward(), turnClockwise(), ... assume the robot is
 * build and positioned as per the LEGO instructions.
 */
public class BasicMovement extends Movement {

    private static final Logger log = LoggerFactory.getLogger(BasicMovement.class);

    public BasicMovement(Port leftPort, Port rightPort) {
        super(leftPort, rightPort);
    }
}
