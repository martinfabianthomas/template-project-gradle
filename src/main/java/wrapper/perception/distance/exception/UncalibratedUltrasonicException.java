package wrapper.perception.distance.exception;

import wrapper.perception.exception.UncalibratedSensorException;

/**
 * Exception indicating that the Ultrasonic sensor was not explicitly calibrated.
 *
 * <p>
 *     Unlike the Gyro it only requires some time to start up and cannot fail calibration.
 * </p>
 */
public class UncalibratedUltrasonicException extends UncalibratedSensorException {
    private final static String defaultMessage = "Ultrasonic sensor was accessed before it was ready for use.";

    public UncalibratedUltrasonicException() {
        this(defaultMessage);
    }

    public UncalibratedUltrasonicException(String message)
    {
        super(message);
    }
}
