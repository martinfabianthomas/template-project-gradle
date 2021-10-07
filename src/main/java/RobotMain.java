import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import program.*;
import wrapper.act.grabber.BarGrabber;
import wrapper.act.movement.OrientationAwareMovement;
import wrapper.perception.color.line.LineDetection;
import wrapper.perception.distance.ViewDistance;
import wrapper.perception.exception.UncalibratedSensorException;
import wrapper.perception.orentation.Orientation;

import java.util.ArrayList;
import java.util.List;
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

    public static void main(final String[] args) throws UncalibratedSensorException {
        log.info("Configuring Motors ...");

        final OrientationAwareMovement movement = new OrientationAwareMovement(MotorPort.B, MotorPort.C);
        final BarGrabber grabber = new BarGrabber(MotorPort.A);

        log.info("Configuring Sensors ...");

        final Orientation orientation = new Orientation(SensorPort.S3);
        orientation.calibrate();
        orientation.addListener(movement);

        final ViewDistance distance = new ViewDistance(SensorPort.S2);
        distance.calibrate();

        final LineDetection lineDetection = new LineDetection(SensorPort.S1);
        lineDetection.calibrate();

        final EV3TouchSensor userInput = new EV3TouchSensor(SensorPort.S4);

        // Example Programs

        List<RobotProgram> examplePrograms = new ArrayList<>();

        // 1: Move around a corner using a white Mark on the Ground and pull a Haribo Box to beginning.
        HariboDeliveryCorner haribo = new HariboDeliveryCorner(movement, grabber, distance, userInput);
        lineDetection.addListener(haribo);
        examplePrograms.add(haribo);

        // 2: Follow a black on white line.
        LineFollower lineFollower = new LineFollower(movement, userInput);
        lineDetection.addListener(lineFollower);
        examplePrograms.add(lineFollower);

        // 3: Make a map of the robots surroundings using the ultrasonic sensor.
        examplePrograms.add(new MapSurrounding(movement, orientation, distance,
                userInput, 500, 500, 10));

        // 4: Bounce back and forth between two walls.
        examplePrograms.add(new PingPong(movement, distance, userInput));

        log.info("Program start");

        int status = examplePrograms.get(4).start();

        System.exit(status);
    }
}
