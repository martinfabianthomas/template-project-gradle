package wrapper.perception.orentation;

public interface OrientationAngleListener {
    /**
     * Sends the new angle measurement in degrees to the listener.
     */
    void angleChanged(float angle);
}
