/*
Copyright (c) 2017 Team 12281 - Elgin Robotics
All rights reserved.
*/

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This OpMode uses the common EB_Hardware/MecanumDrive hardware description classes to define the devices on the robot.
 * The code is structured as a LinearOpMode
 *
 * This particular OpMode drives the Mecanum Wheels using the D-Pad and the A, B, A, Y buttons.
 */

@TeleOp(name="Mecanum D-Pad Drive", group="EB-Drive")
// @Disabled
public class EB_MecanumDPadDrive_Rover extends LinearOpMode {

    private EB_MecanumRobot mecanumRobot  = new EB_MecanumRobot();
    private ElapsedTime runtime     = new ElapsedTime();

    final double power = 0.8;
    final double power2 = 0.8;

    @Override
    public void runOpMode() {

        // The init() method of the HardwareElginBot class does all the work here
        mecanumRobot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Mecanum D-Pad Driver");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());

            // drive using the digital pad
            if (gamepad1.dpad_up) {
                // drive forward
                mecanumRobot.forward(power);

            } else if (gamepad1.dpad_down) {
                // drive backward
                mecanumRobot.backward(power);

            } else if (gamepad1.dpad_left) {
                // strife left
                mecanumRobot.strafeLeft(power);

            } else if (gamepad1.dpad_right) {
                // strife right
                mecanumRobot.strafeRight(power);

            } else if(gamepad1.right_bumper) {
                // turn Clock Wise
                mecanumRobot.turnCW(power2);

            } else if(gamepad1.left_bumper) {
                // turn Counter Clock Wise
                mecanumRobot.turnCCW(power2);

            } else if(gamepad1.left_trigger >= 0.3){

                mecanumRobot.activateCollector();

            } else if(gamepad1.right_trigger >= 0.3){

                mecanumRobot.rerotateCollector();

            } else if(gamepad1.y){

                mecanumRobot.liftup();

            }else if(gamepad1.a){

                mecanumRobot.liftdown();

            }

            else
            {
                mecanumRobot.stop();
            }

            // Send telemetry messages
            telemetry.addData("Power: ", "%.2f", power);
            telemetry.update();

            // Pause for metronome tick.  40 msec each cycle = update 25 times a second.
            mecanumRobot.waitForTick(40);
        }
    }
}
