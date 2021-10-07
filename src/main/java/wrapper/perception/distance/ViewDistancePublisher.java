package wrapper.perception.distance;

public interface ViewDistancePublisher {
    void addListener(ViewDistanceListener distanceListener);

    void removeListener(ViewDistanceListener distanceListener);
}
