import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    public static void main(final String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down");
        }));

        log.info("Program start");

        System.exit(0);
    }
}
