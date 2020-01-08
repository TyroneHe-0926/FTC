package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
Sensors
        *       "color_dist_sensor" - i2c sensor 0  - REV color/distance sensor
        *       "lower_limit"       - dio sensor 0  - REV touch switch
*/

public class Sensors {

    private HardwareMap hwMap = null;
    private ElapsedTime period = new ElapsedTime();

    public static int Red_LED = 5;

    public Sensors(){

    }

    public void init(HardwareMap hwMap) {

        ColorSensor sensorRGB = null;
        DigitalChannel lowerLiftLimit = null;

        sensorRGB = hwMap.colorSensor.get("color_dist_sensor");
        sensorRGB.enableLed(false);

        lowerLiftLimit = hwMap.digitalChannel.get("lower limit");
        lowerLiftLimit.setMode(DigitalChannel.Mode.INPUT);


    }

    void waitForTick(long periodMs) {

        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // Reset the cycle clock for the next pass.
        period.reset();
    }

}
