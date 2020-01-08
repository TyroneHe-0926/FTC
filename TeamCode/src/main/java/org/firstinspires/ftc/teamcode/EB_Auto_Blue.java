/*
Copyright (c) 2017 Team 12281 - Elgin Robotics
All rights reserved.
*/
package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;


/*
 * This OpMode is the Autonomous Mode program, when the robot starts in Blue-01 position
 * as described by our Engineering notebook
 */

@Autonomous(name = "EB Auto Blue", group = "Autonomous")
@Disabled
public class EB_Auto_Blue extends LinearOpMode {

    private EB_MecanumRobot robot  = new EB_MecanumRobot();
//    private final EB_EmptyHw robot               = new EB_EmptyHw();
    private WheelMovement wheelMove = new WheelMovement();      //starts with 0 and 0

    private final int FWD = -1 ;
    private final int BWD = 1 ;

    // public static final String TAG = "Vuforia VuMark Sample";

    OpenGLMatrix lastLocation = null;
    private VuforiaLocalizer vuforia;

    private class WheelMovement {

        private int leftMove, rightMove;

        public WheelMovement(){
            leftMove = 0;
            rightMove = 0;
        }

        public void setWheelMove(int leftIn, int rightIn) {
            leftMove = leftIn;
            rightMove = rightIn;
        }

        public void addWheelMove(int leftIn, int rightIn) {
            leftMove += leftIn;
            rightMove += rightIn;
        }
        public WheelMovement getWheelMove(){
            return this;
        }
    }

    @Override
    public void runOpMode() {

        // init our robot
        robot.init(hardwareMap);

        //for now hardcode the scenario
        // TODO use some kind of a selection mechanism (Switches) to set thisScenario
        EB_Hardware.AutoScenario thisScenario = EB_Hardware.AutoScenario.BLUE_01;

        // wait 0.5 sec, then turn the color sensor LED ON to show Automomous operation is executing
        // (LED was never off, as LED OFF does not work for REV)
//        robot.waitForTick(500);
//        robot.sensorRGB.enableLed(true);

        // relativeLayout will be used to Jewel color to discard
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout",
                "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        waitForStart();

        while (opModeIsActive()) {

            // 1. Start by reading the Vision Targets and figure your destination
            RelicRecoveryVuMark mark = decodeTargets();
            robot.waitForTick(500);

            // 2. Continue by catching the Glyph positioned in front of the robot
            //  with the claws and lifting it a bit above the ground
            //liftGlyph();

            // 3. Continue by pushing the correct Jewel away
            // Robot starts from coordinates 0,0 and wheel rotation 0, 0.
            // After pushing the Jewel, the robot has moved its wheels with the values recorded
            // in wheelMove object
            operateSideArm(relativeLayout, thisScenario);

            // 4. Drive to destination
            //driveToCryptobox(mark, thisScenario);

            // 5. Drop the Glyph
            //robot.servoClawLeft1.setPosition(robot.CLAW_OPENED);
            //robot.servoClawRight1.setPosition(robot.CLAW_OPENED);

            // 6. Give it a nudge - move a bit backwards, then forward again to push the Glyph better
            //nudgeGlyph();

            // 7. Park the robot - go a bit back and then forward steering in the right direction
            //parkRobot();

            // 8. Wait for the time to run out
            break;
        }
    }

    // Grip and lift one Glyph
    private void liftGlyph() {

        final double LIFT_POWER = 0.5 ;

        // open the claws large
        robot.servoClawLeftBottom.setPosition(robot.CLAW_OPEN_LARGE);
        robot.servoClawRightBottom.setPosition(robot.CLAW_OPEN_LARGE);
//        showInfo("Status:  ", "CLAW_OPEN_LARGE");
//        robot.waitForTick(2000);

        // lower the lift all the way
//        int loopCount = 0;
//        while(robot.lowerLiftLimit.getState() == true){
//            robot.motorLift.setPower(-1 * LIFT_POWER);
//            robot.waitForTick(20);
//            loopCount++;
//
////            showInfo("Status:  ", "Lift going DOWN");
//
//            if (loopCount >= 100) {
//                // waited 2 seconds. Let's move on. Select something random (or BWD)
////                showInfo("Status:  ", "Error: Lift Timeout");
//                break;
//            }
//        }
//        robot.motorLift.setPower(0.0);

        // close the claws
        robot.servoClawLeftBottom.setPosition(robot.CLAW_CLOSED);
        robot.servoClawRightBottom.setPosition(robot.CLAW_CLOSED);
//        showInfo("Status:  ", "CLAW_CLOSED");
        robot.waitForTick(200);

        // lift the glyph off the ground
        robot.motorLift.setPower(LIFT_POWER);
//        showInfo("Status:  ", "lift off the ground");

        robot.waitForTick(300);
        robot.motorLift.setPower(0.0);

//        showInfo("Status:  ", "liftGlyph() - Done");

    }

    // in Blue01 we are BLUE so we'll have to push the RED jewel
    private void operateSideArm(final View relativeLayout, EB_Hardware.AutoScenario thisScenario) {

        float hsvValues[] = {0F, 0F, 0F};
        final float values[] = hsvValues;
        final double SCALE_FACTOR = 255;

        int loopCount = 0;
        // lower the arm
        robot.servoBall.setPosition(robot.ARM_DOWN);
        // since .isBusy does not work for servos, we'll just wait a while (experiment)
        robot.waitForTick(200);

        while (true) {

            // give it some time to settle
            robot.waitForTick(50);

            // Now measure the color
            Color.RGBToHSV((int) (robot.sensorRGB.red() * SCALE_FACTOR),
                    (int) (robot.sensorRGB.green() * SCALE_FACTOR),
                    (int) (robot.sensorRGB.blue() * SCALE_FACTOR),
                    hsvValues);

            // change the background color to match the color detected by the RGB sensor
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
                }
            });

            telemetry.addData("Hue:        ", hsvValues[0]);
            telemetry.addData("Saturation: ", hsvValues[1]);
            telemetry.addData("Value:      ", hsvValues[2]);
            telemetry.update();

            // if saturation < 0.5 is too dark to read the color
            if (hsvValues[1] < 0.5) {
                telemetry.addData("Saturation to Low: ", hsvValues[1]);
                telemetry.update();
                // but we'll just ignore this and just try to read the color anyway
            }

            if ((hsvValues[0] > 210) && (hsvValues[0] < 275)) {
                // sensor is facing forward and we found BLUE
                // if thisScenario is BLUE_01 or BLUE_02 than kick backwards to push RED BALL
                if ((thisScenario == EB_Hardware.AutoScenario.BLUE_01) || (thisScenario == EB_Hardware.AutoScenario.BLUE_02)) {
                    pushJewel(BWD);
                } else if ((thisScenario == EB_Hardware.AutoScenario.RED_01) || (thisScenario == EB_Hardware.AutoScenario.RED_02)) {
                    // if thisScenario is RED_01 or RED_02 than kick forward to push RED BALL
                    pushJewel(FWD);
                }

                break;

            } else if ((hsvValues[0] > 340) || (hsvValues[0] < 20)) {
                // sensor is facing forward and we found RED
                // if thisScenario is BLUE_01 or BLUE_02 than kick forward to push RED BALL
                if ((thisScenario == EB_Hardware.AutoScenario.BLUE_01) || (thisScenario == EB_Hardware.AutoScenario.BLUE_02)) {
                    pushJewel(FWD);
                } else if ((thisScenario == EB_Hardware.AutoScenario.RED_01) || (thisScenario == EB_Hardware.AutoScenario.RED_02)) {
                    // if thisScenario is RED_01 or RED_02 than kick forward to push RED BALL
                    pushJewel(BWD);
                }

                break;

            } else {
                // trouble: could not find any expected color
                telemetry.addData("Could not find Red or Blue: ", hsvValues[0]);
                telemetry.update();
            }

            // increment the loopCount to use it for timeout
            loopCount++;

            if (loopCount >= 100) {
                // waited 5 seconds. Let's move on. Select something random (or BWD)
                pushJewel(BWD);
                break;
            }

        }

        // show that routine ended, by turning LED off
        robot.sensorRGB.enableLed(false);

        // Set the panel background back to the default color
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
        });
    }

    private void pushJewel(int direction){
        final double POWER = 0.4 ;

        // set driving mode
        if (direction == FWD) {
            robot.forward(POWER);
        } else {
            robot.backward(POWER);
        }

        // let it go for 2.5 secs
        robot.waitForTick(2500);


        robot.stop();

        // raise the arm and turn the light off
        robot.servoBall.setPosition(robot.ARM_UP);
        robot.waitForTick(400);
        robot.sensorRGB.enableLed(false);

    }

    private RelicRecoveryVuMark decodeTargets () {

        // init vuforia
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        // OR...  Do Not Activate the Camera Monitor View, to save power
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = "AcqfT7j/////AAAAGQpAOvP+fUhPi3n0G8bh3A8PzAnpuZsYPMYr9FysGR" +
                "HpwBn2jbImaUf0vcz27ChRwQt5+nMlQgL8d9j0NLCDru6mYlhIQDFG4xrwCeJ7LZ8DI5AfjMO7to1dtjM" +
                "MCcWQzFJ+KRN8UjmMuSN7R9NxLOHqhjjQzlv4MHzBqtJ1p6DFbqcquz5zS8qxOIXdUArXjKiN8kVsOrJs" +
                "mP66oOirSt0IMnSK1ii5RQiMadFTFnbD3mAsM2HeY872mXqctRNOH3vpJk/qHGbpLbJWchHJX/wmb+3N6" +
                "SISwDV9om9lAHBZJ8iLnoPBSEIKfzD9ExgElqKhRl25bULxzp1HVX/N69eCC3xBOByRucEvczcNtCfJ";

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary

        // now start tracking
        relicTrackables.activate();
        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);

        int loopCount = 0;
        while (vuMark == RelicRecoveryVuMark.UNKNOWN) {

            telemetry.addData("VuMark", "not visible");

            // Add code to move the phone searching for the images
            // Otherwise we'll be stuck in here till time runs out

            // wait a bit
            robot.waitForTick(50);

            // check if anything changed
            vuMark = RelicRecoveryVuMark.from(relicTemplate);

            //
            loopCount++;

            if (loopCount >= 100) {
                // waited 5 seconds. Let's move on. Whatever the vuMark value is just exit the loop
                break;
            }
        }

        // exited while but it might be just time-out
        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
            // found it!
            telemetry.addData("VuMark visible: ", vuMark);
            telemetry.update();
        }

        return vuMark;
    }

}
