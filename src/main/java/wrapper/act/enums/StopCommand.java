package wrapper.act.enums;

/**
 * How the Motor will be stopped.
 *
 * <p>
 *     (Turning a stopped LEGO motor externally by hand is probably fine, but should not be necessary)
 * </p>
 */
public enum StopCommand {
    /**
     * The rotation of the motor will be monitored and any external rotation will cause the motor tu turn in the
     * opposite direction.
     */
    HOLD,
    /**
     * The motor will show significant resistance to external rotation, but if it gets turned it will not rotate back.
     */
    BRAKE,
    /**
     * The Motor looses power, but can rotate freely
     */
    COAST
}
