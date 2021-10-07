package wrapper.perception.color.line;

public interface LineDetectionPublisher {
    void addListener(LineDetectionListener lineDetectionListener);

    void removeListener(LineDetectionListener lineDetectionListener);
}
