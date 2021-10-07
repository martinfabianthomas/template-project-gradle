import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RobotMain {

    private static final Logger log = LoggerFactory.getLogger(RobotMain.class);

    private static final Map<Integer, String> colorNames = Map.of(
            0, "none",
            1, "black",
            2, "blue",
            3, "green",
            4, "yellow",
            5, "red",
            6, "white",
            7, "brown"
    );

    public static void main(final String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down");
        }));

        log.info("Program start");

        System.exit(0);
    }
}
