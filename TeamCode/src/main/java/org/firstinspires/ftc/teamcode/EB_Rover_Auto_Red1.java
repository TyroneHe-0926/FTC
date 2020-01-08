package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "EB_Rover_Auto_Red1")
//@Disabled

public class EB_Rover_Auto_Red1 extends LinearOpMode {

    /* Hardware initialization*/
    EB_MecanumRobot robot = new EB_MecanumRobot();
    private ElapsedTime runtime = new ElapsedTime();

    public static final int fwdspeed = 1;
    public static final int revspeed = -1;

    @Override

    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        telemetry.addData("Satus", "Ready to run");
        telemetry.update();

        waitForStart();

        //go forward for 3 seocnds
        robot.frontLeftWheel.setPower(fwdspeed);
        robot.frontRightWheel.setPower(fwdspeed);
        robot.backLeftWheel.setPower(fwdspeed);
        robot.backRightWheel.setPower(fwdspeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 3.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }

        //turn left for 3 seconds
        robot.frontLeftWheel.setPower(revspeed);
        robot.frontRightWheel.setPower(fwdspeed);
        robot.backLeftWheel.setPower(revspeed);
        robot.backRightWheel.setPower(fwdspeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 3.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }

        //fowrad for 4 seconds
        robot.frontLeftWheel.setPower(fwdspeed);
        robot.frontRightWheel.setPower(fwdspeed);
        robot.backLeftWheel.setPower(fwdspeed);
        robot.backRightWheel.setPower(fwdspeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 4.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }

        //get the team marker out
        robot.motorCollector.setPower(fwdspeed);
        while (opModeIsActive() && (runtime.seconds() < 2.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        robot.motorLift.setPower(revspeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 3.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }
        robot.motorCollector.setPower(revspeed);
        while (opModeIsActive() && (runtime.seconds() < 3.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }

        //reverse for 6 seconds
        robot.frontLeftWheel.setPower(revspeed);
        robot.frontRightWheel.setPower(revspeed);
        robot.backLeftWheel.setPower(revspeed);
        robot.backRightWheel.setPower(revspeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 6.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }


        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);

    }
}