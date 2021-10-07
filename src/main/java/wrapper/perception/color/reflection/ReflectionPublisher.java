package wrapper.perception.color.reflection;

public interface ReflectionPublisher {
    void addListener(ReflectionListener reflectionListener);

    void removeListener(ReflectionListener reflectionListener);
}
