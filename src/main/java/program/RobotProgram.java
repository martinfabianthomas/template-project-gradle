package programms;

import wrapper.perception.exception.UncalibratedSensorException;

public interface RobotProgram {
    /**
     * Starts the program.
     *
     * @return Error code.
     */
    int start();
}
