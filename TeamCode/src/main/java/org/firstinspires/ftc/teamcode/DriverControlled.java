package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Driver Controlled")
public class DriverControlled extends OpMode {

    //Link to Robot class
    private Robot r = new Robot(telemetry);

    //ElapsedTime classes
    private final ElapsedTime relicToggle = new ElapsedTime();
    private final ElapsedTime preciseToggle = new ElapsedTime();

    /* OpMode Variables */

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

    //DriveCode Method Constants
    private final int front = 1, back = -1, right = 1, left = -1;

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

    public void start() {

        //Move Pivot to front of robot
        pivotPos = 0.82;

        //Close Glyph claws
        LCPos = 0;
        RCPos = 1;

        //Raise Glyph Arm slightly so claws can close
        GAPower = 0.25;
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

        //Relic Servos
        r.clamp.setPosition(clampPos);
        r.pivot.setPosition(pivotPos / 8.75);   //Gets divided because we only want 1/8.75th of the servo's rotation

        //Glyph Servos
        r.LC.setPosition(LCPos);
        r.RC.setPosition(RCPos);

        //Keep Jewel Servo Upright
        r.jewel.setPosition(r.JEWEL_REST);

        /* Send Telemetry */

        //OpMode Data Telemetry
        r.telemetry("Relic Mode", relic);
        r.telemetry("Precision Mode", precise);

        //Controller Telemetry
        r.telemetry("LS X|Y:", moveX() + "|" + moveY());
        r.telemetry("RSX:", turnX());

        //Servo Position Telemetry
        r.telemetry("Clamp:", clampPos);
        r.telemetry("Pivot:", pivotPos);
        r.telemetry("Claw L|R:", LCPos + "|" + RCPos);

        //Motor Power Telemetry
        r.telemetry("FL:", FLPower);
        r.telemetry("FR:", FRPower);
        r.telemetry("BR:", BRPower);
        r.telemetry("BL:", BLPower);
        r.telemetry("Slide:", RSPower);
        r.telemetry("Arm:", GAPower);
    }

    private void sleep(int millis) {
        r.timer.reset();
        while (r.timer.milliseconds() < millis)
            Thread.yield();
    }

    private double driveCode(int end, int side) {
        double move = end * moveX() + side * moveY();
        double turn = turnX();
        return Range.clip(move * driveSpeed + turn * turnSpeed, -1, 1);
    }

    private double round(double input, double place) {
        double multiplier = 1 / place;
        return ((int) (input * multiplier)) / multiplier;
    }

    private boolean inRange(double var) {
        return var <= 1 && var >= 0;
    }

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