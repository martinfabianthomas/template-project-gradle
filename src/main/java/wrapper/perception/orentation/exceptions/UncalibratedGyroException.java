package wrapper.perception.orentation.exceptions;

import wrapper.perception.exception.UncalibratedSensorException;

/**
 * Exception indicating that the Gyroscopic sensor was not explicitly calibrated or calibration failed. In both cases
 * accurate measurements cannot be guaranteed.
 */
public class UncalibratedGyroException extends UncalibratedSensorException {
    private final static String defaultMessage = "Gyroscopic sensor might not have been calibrated correctly.";
    private final static String angleMeasurementMessage = defaultMessage + " Measured angle: %.1f, expected 0.0.";
    private final static String bothMeasurementsMessage =
            defaultMessage + " Measured angle: %.1f and rate: %.1f. Expected 0.0 for both.";

    public UncalibratedGyroException() {
        this(defaultMessage);
    }

    public UncalibratedGyroException(float angleMeasurement) {
        this(String.format(angleMeasurementMessage, angleMeasurement));
    }

    public UncalibratedGyroException(float angleMeasurement, float rateMeasurement) {
        this(String.format(bothMeasurementsMessage, angleMeasurement, rateMeasurement));
    }

    public UncalibratedGyroException(String message)
    {
        super(message);
    }
}
