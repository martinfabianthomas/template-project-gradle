import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesnippets.Motor;
import codesnippets.Sensors;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    public static void main(final String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down");
        }));

        log.info("Program start");
        Motor.example();
        Sensors.gyroExample();
        Sensors.ultrasoundExample();

        System.exit(0);
    }
}
