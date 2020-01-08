package org.firstinspires.ftc.teamcode;

/*
Copyright (c) 2017 Team 12281 - Elgin Robotics
All rights reserved.
*/

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

    /* Configuration:
    *
    *  Wheels Module
    *      "frontLeftWheel"     - port 1    - front left driving motor
    *      "frontRightWheel"    - port 2    - front right driving motor
    *      "backLeftWheel"      - port 3    - back left driving motor
    *      "backRightWheel"     - port 4    - back right driving motor
    *
    *  Tools Module
    *      "motorCollector"     - port 1    - rotate the mineral collector
    *      "motorLift"          - port 2    - lifter for mineral collector
    *
    *  Servos Claws Module
    *      "clawLeftTop         - port 1    - servo to open/close left claw
    *      "clawLeftBottom      - port 2    - servo to open/close right claw
    *      "clawLeftTop         - port 3    - servo to open/close left claw
    *      "clawLeftBottom      - port 4    - servo to open/close right claw
    *
    *  Servo for Jewel Pusher
    *      "ballPusher          - port 5    - servo to lower/raise the ball pusher arm
    *
    *  Servos Relic Crane
    *      "servoRelicWrist"    - port 1    - servo to lift the relic crane
    *      "servoRelicJaw"      - port 2    - servo to lift the relic crane
    *
    *  Sensors
    *       "color_dist_sensor" - i2c sensor 0  - REV color/distance sensor
    *       "lower_limit"       - dio sensor 0  - REV touch switch
    *
    */

public class EB_MecanumRobot {

    private HardwareMap hwMap   =  null;
    private ElapsedTime period  = new ElapsedTime();

    // constants
    final double HIGH_POWER  =  0.9 ;
    final double MID_POWER   =  0.5 ;
    final double LOW_POWER   =  0.2 ;
    final int FORWARD        =  1 ;
    final int BACKWARD       =  -1 ;

    private final static double CRANE_ARM_POWER = 0.5;
    private final static double JAW_UP = 0.3;
    private final static double JAW_OVER = 5.4  ;
    private final static double JAW_DOWN = -1.2;
    private final static double JAW_OPEN = 1.8;
    private final static double JAW_CLOSED = -1.8   ;

    // constants
    private final static double TOP_LEFT_CLAW_OPENED = 0.15;
    private final static double BOTTOM_LEFT_CLAW_OPENED = 0.35;
    private final static double TOP_RIGHT_CLAW_OPENED = 0.80;
    private final static double BOTTOM_RIGHT_CLAW_OPENED = 0.80;
    private final static double TOP_LEFT_CLAW_CLOSED = -0.15;
    private final static double BOTTOM_LEFT_CLAW_CLOSED = -0.38;
    private final static double TOP_RIGHT_CLAW_CLOSED =  -0.80;
    private final static double BOTTOM_RIGHT_CLAW_CLOSED = -0.80;

    // Claw movement parameters
    final double CLAW_RETRACTED  = 0.0;
    final double CLAW_OPEN_LARGE = 0.56;
    final double CLAW_OPENED     = 0.62;     // 0.77 -> 95 and 25(reversed) out
    final double CLAW_CLOSED     = 0.80;      // 0.62 -> 75 and 45(reversed) out of 128

    // Jewel arm movement parameters
    final double ARM_UP   = 0.85 ;
    final double ARM_DOWN = 0.2;

    // motor definitions
    DcMotor frontLeftWheel = null;
    DcMotor frontRightWheel = null;
    DcMotor backLeftWheel = null;
    DcMotor backRightWheel = null;

    DcMotor  motorLift             = null;
    DcMotor  motorCollector        = null;

    /* servo definitions
    Servo servoClawLeftTop       = null;
    Servo servoClawLeftBottom    = null;
    Servo servoClawRightTop      = null;
    Servo servoClawRightBottom   = null;
    Servo servoBall              = null;
    Servo servoRelicWrist        = null;
    Servo servoRelicJaw          = null;
    */

    // sensors definitions
    //ColorSensor sensorRGB = null;
    //DigitalChannel lowerLiftLimit = null;

    // LED on the color sensor
    static final int BALL_LED = 5;

    // constructor
    public EB_MecanumRobot() {

    }

    // init standard hardware interfaces
    public void init(HardwareMap map) {

        hwMap = map;

        // Init Mecanum Wheels Motors
        // ==========================

        frontLeftWheel = hwMap.dcMotor.get("FrontWheelLeft");
        frontRightWheel = hwMap.dcMotor.get("FrontWheelRight");
        backLeftWheel = hwMap.dcMotor.get("BackWheelLeft");
        backRightWheel = hwMap.dcMotor.get("BackWheelRight");

        // reverse one front and one back motor as they are mounded 180 deg rotated
        frontLeftWheel.setDirection(DcMotor.Direction.REVERSE);
        backLeftWheel.setDirection(DcMotor.Direction.REVERSE);

        // set all motors to run without encoders.
        frontLeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // init motorLift and motorRelic
        motorLift = hwMap.dcMotor.get("liftMotor");
        motorCollector= hwMap.dcMotor.get("collectorMotor");

        motorLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // motorRelic.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorLift.setZeroPowerBehavior(BRAKE);
        motorCollector.setZeroPowerBehavior(BRAKE);

        // Init Servos
        // ===========

        /*
        // servos for glyph manipulating claws
        servoClawLeftTop = hwMap.servo.get("clawLeftUpper");
        servoClawLeftBottom = hwMap.servo.get("clawLeftLower");
        servoClawRightTop = hwMap.servo.get("clawRightUpper");
        servoClawRightBottom = hwMap.servo.get("clawRightLower");

        // set full range
        servoClawLeftTop.scaleRange(Servo.MIN_POSITION, Servo.MAX_POSITION);
        servoClawLeftBottom.scaleRange(Servo.MIN_POSITION, Servo.MAX_POSITION);
        servoClawRightTop.scaleRange(Servo.MIN_POSITION, Servo.MAX_POSITION);
        servoClawRightBottom.scaleRange(Servo.MIN_POSITION, Servo.MAX_POSITION);


        servoClawLeftBottom.setDirection(Servo.Direction.REVERSE);
        servoClawRightTop.setDirection(Servo.Direction.REVERSE);

        // start with both claws in the CLAW_OPEN_LARGE position
//        servoClawLeftTop.setPosition(CLAW_OPEN_LARGE);
//        servoClawLeftBottom.setPosition(CLAW_OPEN_LARGE);
//        servoClawRightTop.setPosition(CLAW_OPEN_LARGE);
//        servoClawRightBottom.setPosition(CLAW_OPEN_LARGE);

        // servo for the Jewel pushing arm
        servoBall = hwMap.servo.get("ballPusher");

        // make sure the ball servo uses the full range (190 degrees)
        servoBall.scaleRange(Servo.MIN_POSITION, Servo.MAX_POSITION);
        servoBall.setPosition(ARM_UP);


        // init color sensor - color sensor LED OFF shows robot was init-ed
        sensorRGB = hwMap.colorSensor.get("color_dist_sensor");
        sensorRGB.enableLed(false);     // -> This is clearly broken for REV Color-Distance Sensor

        // init the touch sensor is a DigitalChannel.
        // It is HIGH if the button is unpressed and pulls LOW if the button is pressed
        // When connected directly to the Rev Hub, the second pin gets connected to a odd number
        // digital channel (1, 3, 5, 7) while the first pin is unconnected (nothing on ch 0, 2, 4, 6)
        lowerLiftLimit = hwMap.digitalChannel.get("lower_limit");
        lowerLiftLimit.setMode(DigitalChannel.Mode.INPUT);

        // relic manipulation servos - connected to Modern Robotics servo controller
        servoRelicWrist = hwMap.servo.get("wtist");
        servoRelicJaw = hwMap.servo.get("jaw");

        // servoRelicWrist.setDirection(Servo.Direction.REVERSE);
        // servoRelicJaw.setDirection(Servo.Direction.REVERSE);

        servoRelicWrist.scaleRange(Servo.MIN_POSITION, Servo.MAX_POSITION);
        servoRelicJaw.scaleRange(Servo.MIN_POSITION, Servo.MAX_POSITION);
        */

        // Init Sensors
        // ============init color sensor - color sensor LED OFF shows robot was init-ed
        //sensorRGB = hwMap.colorSensor.get("color_dist_sensor");
        //sensorRGB.enableLed(false);     // -> This is clearly broken for REV Color-Distance Sensor

        // init the touch sensor is a DigitalChannel.
        // It is HIGH if the button is unpressed and pulls LOW if the button is pressed
        // When connected directly to the Rev Hub, the second pin gets connected to a odd number
        // digital channel (1, 3, 5, 7) while the first pin is unconnected (nothing on ch 0, 2, 4, 6)
        //lowerLiftLimit = hwMap.digitalChannel.get("lower_limit");
        //lowerLiftLimit.setMode(DigitalChannel.Mode.INPUT);
    }

    //
    // Mecanum Wheels driving routines
    //
    public void stop() {
        frontLeftWheel.setPower(0);
        frontRightWheel.setPower(0);
        backLeftWheel.setPower(0);
        backRightWheel.setPower(0);
        motorLift.setPower(0);
        motorCollector.setPower(0);
    }

    // drive forward: all spin forward
    public void forward(double power) {
        frontLeftWheel.setPower(FORWARD * power);
        frontRightWheel.setPower(FORWARD * power);
        backLeftWheel.setPower(FORWARD * power);
        backRightWheel.setPower(FORWARD * power);

    }

    // drive backward: all spin backward
    public void backward(double power) {
        frontLeftWheel.setPower(BACKWARD * power);
        frontRightWheel.setPower(BACKWARD * power);
        backLeftWheel.setPower(BACKWARD * power);
        backRightWheel.setPower(BACKWARD * power);
    }

    // drive sideways left:
    //  - front left & back right - spin backward
    //  - front right & back left - spin forward
    public void strafeLeft(double power) {
        frontLeftWheel.setPower(BACKWARD * power);
        frontRightWheel.setPower(FORWARD * power);
        backLeftWheel.setPower(FORWARD * power);
        backRightWheel.setPower(BACKWARD * power);
    }

    // drive sideways right:
    //  - front left & back right - spin forward
    //  - front right & back left - spin backward
    public void strafeRight(double power) {
        frontLeftWheel.setPower(FORWARD * power);
        frontRightWheel.setPower(BACKWARD * power);
        backLeftWheel.setPower(BACKWARD * power);
        backRightWheel.setPower(FORWARD * power);
    }

    public void liftup()
    {

        motorLift.setPower(-1);
    }

    public void liftdown(){

        motorLift.setPower(1);
    }

    // drive diagonally front left:
    //  - front right & back left - spin forward
    //  - front right & back left - coast
    public void diagFrontLeft(double power) {
        frontLeftWheel.setPower(0);
        frontRightWheel.setPower(FORWARD * power);
        backLeftWheel.setPower(FORWARD * power);
        backRightWheel.setPower(0);
    }

    // drive diagonally front right:
    //  - front left & back right - spin forward
    //  - front right & back left - coast
    public void diagFrontRight(double power) {
        frontLeftWheel.setPower(FORWARD * power);
        frontRightWheel.setPower(0);
        backLeftWheel.setPower(0);
        backRightWheel.setPower(FORWARD * power);
    }

    // drive diagonally back left:
    //  - front left & back right - spin forward
    //  - front right & back left - coast
    public void diagBackLeft(double power) {
        frontLeftWheel.setPower(BACKWARD * power);
        frontRightWheel.setPower(0);
        backLeftWheel.setPower(0);
        backRightWheel.setPower(BACKWARD * power);
    }

    // drive diagonally back right:
    //  - front left & back right - spin forward
    //  - front right & back left - coast
    public void diagBackRight(double power) {
        frontLeftWheel.setPower(0);
        frontRightWheel.setPower(BACKWARD * power);
        backLeftWheel.setPower(BACKWARD * power);
        backRightWheel.setPower(0);
    }

    // turn Clock Wise:
    //  - front left & back left - spin forward
    //  - front right & back right - spin backward
    public void turnCW(double power) {
        frontLeftWheel.setPower(FORWARD * power);
        frontRightWheel.setPower(BACKWARD * power);
        backLeftWheel.setPower(FORWARD * power);
        backRightWheel.setPower(BACKWARD * power);
    }

    // turn Counter Clock Wise right:
    //  - front left & back left - spin backward
    //  - front right & back right - spin forward
    public void turnCCW(double power) {
        frontLeftWheel.setPower(BACKWARD * power);
        frontRightWheel.setPower(FORWARD * power);
        backLeftWheel.setPower(BACKWARD * power);
        backRightWheel.setPower(FORWARD * power);
    }

    public void stickDrive(double frontLeftPower, double frontRightPower,
                           double backLeftPower, double backRightPower) {
        frontLeftWheel.setPower(frontLeftPower);
        frontRightWheel.setPower(frontRightPower);
        backLeftWheel.setPower(backLeftPower);
        backRightWheel.setPower(backRightPower);

    }

    //
    // Glyph operating routines
    //

    /*
    public void closeTopClaws() {
        servoClawLeftTop.setPosition(TOP_LEFT_CLAW_CLOSED);
        servoClawRightTop.setPosition(TOP_RIGHT_CLAW_CLOSED);
    }

    public void openBottomClaws() {
        servoClawLeftBottom.setPosition(BOTTOM_LEFT_CLAW_OPENED);
        servoClawRightBottom.setPosition(BOTTOM_RIGHT_CLAW_OPENED);
    }

    public void closeBottonmClaws(){
        servoClawLeftBottom.setPosition(BOTTOM_LEFT_CLAW_CLOSED);
        servoClawRightBottom.setPosition(BOTTOM_RIGHT_CLAW_CLOSED);
    }

    public void openTopClaws(){
        servoClawRightTop.setPosition(TOP_RIGHT_CLAW_OPENED);
        servoClawLeftTop.setPosition(TOP_LEFT_CLAW_OPENED);
    }
    */
    public void activateCollector() {
        //int position = motorRelic.getCurrentPosition();
        motorCollector.setPower(0.7);
    }

    public void rerotateCollector(){

        motorCollector.setPower(-0.7);

    }
    /*

    public void jawUp() {
        servoRelicWrist.setPosition(JAW_UP);
    }

    public void jawDown() {
        servoRelicWrist.setPosition(JAW_DOWN);
    }

    public void jawOpen() {
        servoRelicJaw.setPosition(JAW_OPEN);
    }

    public void jawClose() {
        servoRelicJaw.setPosition(JAW_CLOSED);

    }

    public void jawOver(){
        servoRelicWrist.setPosition(JAW_OVER);

    }
    */
    //
    // sleep control
    //
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
