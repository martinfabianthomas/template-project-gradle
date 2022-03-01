package codesnippets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lejos.hardware.port.MotorPort;
import lejos.utility.Delay;
import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor;
// import ev3dev.actuators.lego.motors.EV3MediumRegulatedMotor;

public final class Motor {

    private static final Logger log = LoggerFactory.getLogger(Motor.class);

    private Motor() {
    }

    public static void example() {
        log.info("Motor example");

        // var motor = new EV3MediumRegulatedMotor(MotorPort.A);
        var motor = new EV3LargeRegulatedMotor(MotorPort.B);

        motor.setSpeed(360 / 2); // 360° per 2s
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Stopping motor");
            motor.stop();
        }));

        motor.forward();
        Delay.msDelay(2_000); // 2s

        motor.stop();
        Delay.msDelay(2_000); // 2s

        motor.backward();
        Delay.msDelay(2_000); // 2s

        motor.rotate(90);
    }
}
