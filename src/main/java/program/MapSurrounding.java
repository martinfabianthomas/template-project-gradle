package program;

import ev3dev.sensors.ev3.EV3TouchSensor;
import lejos.utility.Delay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wrapper.act.movement.Movement;
import wrapper.perception.distance.ViewDistance;
import wrapper.perception.exception.UncalibratedSensorException;
import wrapper.perception.orentation.Orientation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A program that creates a PNG-file of the robots surroundings using the ultrasonic sensor.
 */
public class MapSurrounding implements RobotProgram {

    private static final Logger log = LoggerFactory.getLogger(MapSurrounding.class);

    private final Movement movement;
    private final Orientation orientation;
    private final ViewDistance distance;
    private final EV3TouchSensor userInput;
    private final int pictureWidth;
    private final int pictureHeight;
    private final int numberOfMeasurements;

    public MapSurrounding(Movement movement, Orientation orientation, ViewDistance distance, EV3TouchSensor userInput,
                          int pictureWidth, int pictureHeight, int numberOfMeasurements) {
        this.movement = movement;
        this.orientation = orientation;
        this.distance = distance;
        this.userInput = userInput;
        this.pictureHeight = pictureHeight;
        this.pictureWidth = pictureWidth;
        this.numberOfMeasurements = numberOfMeasurements;
    }

    @Override
    public int start() {
        log.info("Waiting for user input");

        while (!userInput.isPressed()) {
            Delay.msDelay(100);
        }

        // Avoid stopping immediately in while.
        Delay.msDelay(1000);

        log.info("Start");

        BufferedImage image = new BufferedImage(pictureWidth, pictureHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        graphics.setPaint(Color.white);
        graphics.fillRect(0, 0, pictureWidth, pictureHeight);
        graphics.setPaint(Color.black);
        graphics.setStroke(new BasicStroke(3));

        while (!userInput.isPressed()) {
            double averageAngle = 0;
            double averageDistance = 0;

            try {
                for (int measurement = 0; measurement < numberOfMeasurements; measurement++) {
                    averageAngle += Math.toRadians(orientation.getAngle());
                    averageDistance += distance.getDistance();
                    Delay.msDelay(50);
                }
            } catch (UncalibratedSensorException exception) {
                log.error("Someone did not set up my dependencies", exception);
                return 1;
            }

            averageAngle = averageAngle / numberOfMeasurements;
            averageDistance = averageDistance / numberOfMeasurements;

            int x = pictureWidth / 2 + (int) (Math.cos(averageAngle) * averageDistance);
            int y = pictureHeight / 2 + (int) (Math.sin(averageAngle) * averageDistance);
            x = Math.min(Math.max(x, 0), pictureWidth);
            y = Math.min(Math.max(y, 0), pictureWidth);
            graphics.drawLine(x, y, x, y);

            movement.turnCounterclockwise();
            Delay.msDelay(200);
            movement.stop();
            Delay.msDelay(200);
        }

        try {
            ImageIO.write(image, "PNG", new File("environment.png"));
        } catch (IOException exception) {
            log.error("Could not save picture.", exception);
            return 1;
        }

        return 0;
    }
}
