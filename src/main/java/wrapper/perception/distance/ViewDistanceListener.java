package wrapper.perception.distance;

public interface ViewDistanceListener {
    /**
     * Sends the new distance measurement in cm to the listener.
     */
    void distanceChanged(float distance);
}
