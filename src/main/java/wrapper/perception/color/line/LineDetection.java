package wrapper.perception.color.line;

import ev3dev.sensors.ev3.EV3ColorSensor;
import lejos.hardware.port.Port;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.perception.color.reflection.ReflectionListener;
import wrapper.perception.color.reflection.ReflectionPollingTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

public class LineDetection implements LineDetectionPublisher, ReflectionListener {

    private static final Logger log = LoggerFactory.getLogger(LineDetection.class);

    private final SampleProvider colorSampler;
    private final float[] colorSample;
    private final ReflectionPollingTask pollingTask;

    private final int lineReflection;
    private final int backgroundReflection;
    private final boolean blackOnWhite;
    private final List<LineDetectionListener> listenerList;

    private boolean lastUpdateOnLine;

    /**
     * Creates an uncalibrated object for following a black on white line with default values for line and
     * background reflection.
     *
     * @param colorPort Port of the color / light sensor.
     */
    public LineDetection(Port colorPort) {
        this(colorPort, 15, 40);
    }

    /**
     * Creates an uncalibrated object for following a black on white / white on black line.
     *
     * <p>
     *     Assumes the robot starts on the line.
     * </p>
     *
     * @param colorPort Port of the color / light sensor.
     * @param lineReflection Upper / lower bound for reflection value on the line.
     * @param backgroundReflection Upper / lower bound for reflection value off the line.
     */
    public LineDetection(Port colorPort, int lineReflection, int backgroundReflection) {
        EV3ColorSensor color = new EV3ColorSensor(colorPort);
        colorSampler = color.getRedMode();
        colorSample = new float[colorSampler.sampleSize()];
        pollingTask = new ReflectionPollingTask(colorSampler, 100, 5);
        pollingTask.addListener(this);

        this.lineReflection = lineReflection;
        this.backgroundReflection = backgroundReflection;
        blackOnWhite = lineReflection < backgroundReflection;
        lastUpdateOnLine = true;

        List<LineDetectionListener> list = new ArrayList<>();
        listenerList = Collections.synchronizedList(list);
    }

    /**
     * Ensures the Color sensor is sending correct values. Calibration takes up to 3 seconds. Also starts the automatic
     * notification of LineFollowingListener.
     */
    public void calibrate() {
        // Color sensor starts off only sending only 0.0 in all modes. From my testing it takes up to 2 seconds,
        // using 2.5 to be safe. If any value other than 0.0 is received the calibration is finished.
        int msElapsed = 0;
        int msPerIteration = 100;
        colorSampler.fetchSample(colorSample, 0);
        while (colorSample[0] == 0.0 && msElapsed < 2500) {
            Delay.msDelay(msPerIteration);
            msElapsed += msPerIteration;
            colorSampler.fetchSample(colorSample, 0);
        }

        Timer timer = new Timer("color-polling");

        // Sensor updates with frequency of 1kHz.
        timer.scheduleAtFixedRate(pollingTask, 100L, 1L);
    }

    private boolean shouldSwitchOnLine(float reflection) {
        if (blackOnWhite) {
            return reflection < lineReflection;
        } else {
            return reflection > lineReflection;
        }
    }

    private boolean shouldSwitchOffLine(float reflection) {
        if (blackOnWhite) {
            return reflection > backgroundReflection;
        } else {
            return reflection < backgroundReflection;
        }
    }

    @Override
    public void strengthOfReflectionChanged(float reflection) {
        if (lastUpdateOnLine && shouldSwitchOffLine(reflection)) {
            notify(false);
        } else if (!lastUpdateOnLine && shouldSwitchOnLine(reflection)) {
            notify(true);
        }
    }

    private void notify(boolean onLine) {
        try {
            for (LineDetectionListener listener : listenerList) {
                if (onLine) {
                    listener.overLine();
                } else {
                    listener.offLine();
                }
            }
        } catch (Exception e) {
            log.error("Exception was thrown by a ReflectionListener. Automatic color polling will stop now.", e);
            throw e;
        }

        lastUpdateOnLine = onLine;
    }

    @Override
    public void addListener(LineDetectionListener lineDetectionListener) {
        listenerList.add(lineDetectionListener);
    }

    @Override
    public void removeListener(LineDetectionListener lineDetectionListener) {
        listenerList.remove(lineDetectionListener);
    }
}
