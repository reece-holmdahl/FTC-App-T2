package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Driver Controlled Thread Experiment")
public class DriverControlledThreadExp extends OpMode {

    //Link to Robot class
    private Robot r = new Robot(telemetry);

    //ElapsedTime classes
    private final ElapsedTime relicToggle = new ElapsedTime();
    private final ElapsedTime preciseToggle = new ElapsedTime();

    /* OpMode Variables */

    //Thread Control Boolean
    private boolean threadOn;

    //Drivetrain Motor Power Variables
    private double FLPower = 0;
    private double FRPower = 0;
    private double BRPower = 0;
    private double BLPower = 0;

    //Drivetrain Variables
    private double driveSpeed = 0.85;
    private double turnSpeed = 0.4;
    private boolean relic = false;
    private boolean precise = false;
    private ZeroPowerBehavior ZPB = ZeroPowerBehavior.FLOAT;

    //Relic Motor Power Variable
    private double RSPower = 0;

    //Glyph Motor Power Variable
    private double GAPower = 0;

    //Relic Servo Position Variables
    private double clampPos = r.CLAMP_REST;
    private double pivotPos = r.PIVOT_REST;

    //Glyph Servo Position Variables
    private double LCPos = r.LC_REST;
    private double RCPos = r.RC_REST;

    /* OpMode Constants */

    //Servo Speed Constants
    private final double clampSpeed = 0.035;
    private final double pivotSpeed = 0.005;
    private final double clawSpeed = 0.0175;

    /**
     * The init method is run once when the INIT button is pressed on the DS phone.  It is used to
     * define and map all hardware devices in the OpMode.
     */

    public void init() {

        //Hardware Map Devices in Robot Class
        r.map(hardwareMap);

        //Decrease Joystick Deadzone
        gamepad1.setJoystickDeadzone(0.05f);
    }

    /**
     * The start method is ran once when the play button is pressed on the DS phone. In this case it
     * is used to move parts of our robot around when the driver controlled period starts to avoid
     * getting moving parts caught in gears or interfering with its controls.
     */

    public void start() {

        //Start Power and Position Control Thread
        threadMode(true);

        //Extend Relic Slide so Pivot can go to front of robot
        RSPower = 1;
        sleep(600);
        RSPower = 0;

        //Move Pivot to front of robot
        pivotPos = 0.82;

        //Close Glyph claws
        LCPos = 0;
        RCPos = 1;

        //Raise Glyph Arm slightly so claws can close
        GAPower = 0.3;
        sleep(400);
        GAPower = 0;
    }

    /**
     * The loop method is ran repeatedly after the driver pressed the play button on the DS phone.
     * It is used to provide the main functionality of the robot during the driver controlled period
     * or "TeleOp."
     */

    public void loop() {

        /* Update Motor Power */

        //Drivetrain driveCode Method Constants
        final int front = 1, back = -1, right = 1, left = -1;

        //Drivetrain Motor Power and Relic Mode Config
        if (!relic) {
            FLPower = driveCode(front, left);
            FRPower = driveCode(front, right);
            BRPower = driveCode(back, right);
            BLPower = driveCode(back, left);
        } else {
            FLPower = driveCode(back, left);
            FRPower = driveCode(front, left);
            BRPower = driveCode(front, right);
            BLPower = driveCode(back, right);
        }

        //Relic Mode
        if (gamepad1.start && relicToggle.milliseconds() > 400) {
            relic = !relic;
            relicToggle.reset();
        }

        //Precision Mode
        if (gamepad1.right_stick_button && preciseToggle.milliseconds() > 400) {
            precise = !precise;
            preciseToggle.reset();
        }

        if (!precise) {
            driveSpeed = 0.85;
            turnSpeed = 0.4;
        } else {
            driveSpeed = 0.25;
            turnSpeed = 0.2;
        }

        //Drift Compensation
        if (moveX() + moveY() == 0 && ZPB != ZeroPowerBehavior.BRAKE)
            ZPB = ZeroPowerBehavior.BRAKE;
        if (moveX() + moveY() != 0 && ZPB != ZeroPowerBehavior.FLOAT)
            ZPB = ZeroPowerBehavior.FLOAT;

        //Set Motor ZeroPowerBehavior Accordingly
        r.FL.setZeroPowerBehavior(ZPB);
        r.FR.setZeroPowerBehavior(ZPB);
        r.BR.setZeroPowerBehavior(ZPB);
        r.BL.setZeroPowerBehavior(ZPB);

        //Relic Motor Power
        if (gamepad1.dpad_right)
            RSPower = 1;
        else if (gamepad1.dpad_left)
            RSPower = -0.25;
        else
            RSPower = 0;

        //Glyph Motor Power
        if (gamepad1.dpad_up)
            GAPower = 0.5;
        else if (gamepad1.dpad_down)
            GAPower = -0.15;
        else
            GAPower = 0;

        /* Update Servo Positions */

        //Clamp Servo Position
        if (inRange(clampPos)) {
            if (gamepad1.b)
                clampPos += clampSpeed;
            else if (gamepad1.a)
                clampPos -= clampSpeed;
        }

        //Pivot Servo Position
        if (inRange(pivotPos)) {
            if (gamepad1.x)
                pivotPos += pivotSpeed;
            else if (gamepad1.y)
                pivotPos -= pivotSpeed * 0.25;
        }

        //Claw Servo Positions
        if (inRange(LCPos) && inRange(RCPos)) {
            if (gamepad1.left_bumper) {
                LCPos += clawSpeed;
                RCPos -= clawSpeed;
            } else if (gamepad1.right_bumper) {
                LCPos -= clawSpeed;
                RCPos += clawSpeed;
            }
        }

        /* Keep Servos in Range */

        //Clamp Servo
        if (clampPos > 1)
            clampPos = 1;
        if (clampPos < 0)
            clampPos = 0;

        //Pivot Servo
        if (pivotPos > 1)
            pivotPos = 1;
        if (pivotPos < 0)
            pivotPos = 0;

        //Left Claw Servo
        if (LCPos > 1)
            LCPos = 1;
        if (LCPos < 0)
            LCPos = 0;

        //Right Claw Servo
        if (RCPos > 1)
            RCPos = 1;
        if (RCPos < 0)
            RCPos = 0;
    }

    /**
     * The loopThread method is ran during the loop method, and is actually identical to it but has
     * another purpose. In this case, I use this extra thread to set the power and control the
     * position of motors and servos, and use the main loop method to control each position. Setting
     * up a separate thread for powering motors and one for finding their positions is a safer
     * alternative than using no power and position variable.
     */

    private void loopThread() {

        /* Set Power to Motors */

        //Drivetrain Motors
        r.FL.setPower(FLPower);
        r.FR.setPower(FRPower);
        r.BR.setPower(BRPower);
        r.BL.setPower(BLPower);

        //Relic Motor
        r.relicSlide.setPower(RSPower);

        //Glyph Arm
        r.glyphArm.setPower(GAPower);

        /* Set Servo Positions */

        //Clamp Servo
        r.clamp.setPosition(clampPos);

        /* Pivot Servo
         * The pivot servo's position is being divided by 8.75 because with REV Expansion Hubs the
         * servo can be rotated ~8.75 times and we only need one full rotation.
         */
        r.pivot.setPosition(pivotPos / 8.75);

        //Glyph Servos
        r.LC.setPosition(LCPos);
        r.RC.setPosition(RCPos);

        //Keep Jewel Servo Upright
        r.jewel.setPosition(r.JEWEL_REST);
    }

    /**
     * The logThread method is here to constantly update telemetry regardless of any temporary
     * pauses or errors in the main loop thread.
     */

    private void logThread() {

        /* Send Telemetry */

        //OpMode Data Telemetry
        r.log("Relic Mode", relic);
        r.log("Precision Mode", precise);

        //Controller Telemetry
        r.log("LS X|Y:", moveX() + "|" + moveY());
        r.log("RSX:", turnX());

        //Servo Position Telemetry
        r.log("Clamp:", clampPos);
        r.log("Pivot:", pivotPos);
        r.log("Claw L|R:", LCPos + "|" + RCPos);

        //Motor Power Telemetry
        r.log("FL:", FLPower);
        r.log("FR:", FRPower);
        r.log("BR:", BRPower);
        r.log("BL:", BLPower);
        r.log("Slide:", RSPower);
        r.log("Arm:", GAPower);
    }

    /**
     * The stop method is ran once after the driver controlled period has ended and is usually used
     * for post OpMode actions like resetting the robot or killing motors.
     */

    public void stop() {

        //Stop Power and Position Control Thread
        threadMode(false);

        //Kill Motors
        killMotors();
    }

    /**
     * The threadMode method controls to status of the thread that handles calculation and telemetry
     * so we can enable/disable it when necessary. It uses a detection system and a variable so the
     * thread doesn't throw any errors or kill the OpMode via InterruptedException.
     * @param mode  The status of activity desired for the thread, true (on) or false (off)
     */

    private void threadMode(boolean mode) {
        if (mode) {
            if (!thread.isAlive())
                thread.run();
            threadOn = true;
        } else {
            if (!thread.isInterrupted())
                thread.interrupt();
            threadOn = false;
        }
    }

    //Creates the thread to handle calculations and telemetry
    private Thread thread = new Thread(new Runnable() {
        public void run() {
            while (threadOn) {
                loopThread();
                logThread();
            }
        }
    });

    /**
     * The killMotors method is just a simple way to ensure all motors safely deactivate after the
     * OpMode is complete, even if the thread stays active. By any chance the thread stays active,
     * the power variables controlling the motors will be set to 0.
     */

    private void killMotors() {

        /* Kill Motors */

        //Drivetrain Motors
        r.FL.setPower(0);
        FLPower = 0;
        r.FR.setPower(0);
        FRPower = 0;
        r.BR.setPower(0);
        BRPower = 0;
        r.BL.setPower(0);
        BLPower = 0;

        //Relic Motor
        r.relicSlide.setPower(0);

        //Glyph Arm
        r.glyphArm.setPower(0);
    }

    /**
     * The sleep method waits a certain amount of milliseconds before the next action occurs. In
     * order to avoid a InterruptedException, the thread is yielded so no other actions occur while
     * the method is waiting.
     * @param millis    The amount of milliseconds to pause action for.
     */

    private void sleep(int millis) {
        r.timer.reset();
        while (r.timer.milliseconds() < millis)
            Thread.yield();
    }

    /**
     * The driveCode method is the formula that allows holonomic drive to be controlled with two
     * joysticks. Specify the end (front, back) and side (left, right) of the robot and be returned
     * a value of power based on where the left joystick is.
     * @param end   The end of the robot the motor is on (front, back)
     * @param side  The side of the robot the motor is on (left, right)
     * @return      The power a motor in said position should be
     */

    private double driveCode(int end, int side) {
        double move = end * moveX() + side * moveY();
        double turn = turnX();
        return Range.clip(move * driveSpeed + turn * turnSpeed, -1, 1);
    }

    /**
     * The round method is very simple, it rounds a number inputted to the decimal place specified.
     * @param input The input to be rounded
     * @param place The place the input should be rounded to (0.01 or hundredths, 0.1 or tenths)
     * @return      The input after being rounded
     */

    private double round(double input, double place) {
        double multiplier = 1 / place;
        return ((int) (input * multiplier)) / multiplier;
    }

    /**
     * The inRange method returns if a variable specified is between 0 and 1, this method is used
     * for keeping servo variables within their maximum ranges.
     * @param var   The servo position variable to check
     * @return      The boolean (true or false) specifying if the servo variable is within 0 and 1
     */

    private boolean inRange(double var) {
        return var <= 1 && var >= 0;
    }

    /*
     * All doubles below are simply just renamed controller variables rounded to various decimal
     * places for less error on the driver's end.
     */

    private double moveX() {
        return round(gamepad1.left_stick_x, 0.05);
    }

    private double moveY() {
        return round(gamepad1.left_stick_y, 0.05);
    }

    private double turnX() {
        return round(gamepad1.right_stick_x, 0.01);
    }
}