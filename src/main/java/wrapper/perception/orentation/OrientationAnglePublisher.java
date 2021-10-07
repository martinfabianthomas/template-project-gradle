package wrapper.perception.orentation;

public interface OrientationAnglePublisher {
    void addListener(OrientationAngleListener angleListener);

    void removeListener(OrientationAngleListener angleListener);
}
