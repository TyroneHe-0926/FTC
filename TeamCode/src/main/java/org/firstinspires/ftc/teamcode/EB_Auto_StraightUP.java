package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "EB_Staight")
//@Disabled

public class EB_Auto_StraightUP extends LinearOpMode {

    /* Hardware initialization*/
    EB_MecanumRobot robot = new EB_MecanumRobot();
    private ElapsedTime runtime = new ElapsedTime();

    public static final double fwdspeed = 0.8;
    public static final double revspeed = -0.8;

    @Override

    public void runOpMode() throws InterruptedException {

        robot.init(hardwareMap);

        telemetry.addData("Satus", "Ready to run");
        telemetry.update();

        waitForStart();

        //go forward for 2.5 seconds
        robot.frontLeftWheel.setPower(revspeed);
        robot.frontRightWheel.setPower(revspeed);
        robot.backLeftWheel.setPower(revspeed);
        robot.backRightWheel.setPower(revspeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 2.5)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }


        robot.motorCollector.setPower(fwdspeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < 1.0)) {
            telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
            telemetry.update();
        }

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);

    }
}