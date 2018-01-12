package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Robot {

    /* Classes */

    //ElapsedTime Class
    ElapsedTime t = new ElapsedTime();

    //Telemetry Class
    Telemetry telemetry = null;

    /* Motors */

    //Drivetrain Motors
    DcMotor FL = null;
    DcMotor FR = null;
    DcMotor BR = null;
    DcMotor BL = null;

    //Relic Motor
    DcMotor relicSlide = null;

    //Glyph Motor
    DcMotor glyphArm = null;

    /* Servos */

    //Relic Servos
    Servo clamp = null;
    Servo pivot = null;

    //Glyph Servos
    Servo LC = null;
    Servo RC = null;

    //Jewel Servo
    Servo jewel = null;

    /* Sensors */

    //Jewel Color Sensor
    ColorSensor color = null;

    //REV Robotics IMU
    BNO055IMU gyro = null;

    /* Servo Init Positions */

    //Relic Servo Positions
    final double CLAMP_REST = 1;
    final double PIVOT_REST = 0;
    final double PIVOT_START = 0.032;

    //Glyph Servo Positions
    final double LC_REST = 1;
    final double RC_REST = 0;

    //Jewel Servo Position
    final double JEWEL_REST = 1;

    /**
     * The map method hardware maps all the robot's hardware devices and uses the HardwareMap class
     * inherited from the OpMode or LinearOpMode classes to map them without errors.
     * @param map The HardwareMap class inherited from OpMode or LinearOpMode
     */

    void map(HardwareMap map, Telemetry telemetry) {

        PerpetualExistenceThread p = new PerpetualExistenceThread();
        p.run();

        //Save Telemetry
        this.telemetry = telemetry;

        /* Motors */

        //Drivetrain Motors
        FL = map.dcMotor.get("front left");
        FR = map.dcMotor.get("front right");
        BR = map.dcMotor.get("back right");
        BL = map.dcMotor.get("back left");

        //Relic Motor
        relicSlide = map.dcMotor.get("slide");

        //Glyph Motor
        glyphArm = map.dcMotor.get("arm");

        /* Servos */

        //Relic Servos
        clamp = map.servo.get("clamp");
        pivot = map.servo.get("pivot");

        //Glyph Servos
        LC = map.servo.get("left claw");
        RC = map.servo.get("right claw");

        //Jewel Servo
        jewel = map.servo.get("jewel");

        /* Sensors */

        //Jewel Color Sensor
        color = map.colorSensor.get("color");

        //REV Robotics IMU
        gyro = map.get(BNO055IMU.class, "imu");

        /* Device Settings */
        settings();
    }

    /**
     * The settings method is here to provide a distinct separation between the hardware mapping of
     * the devices and their parameters.  It is separate so I don't have to sort through my init
     * code to change a motor's direction.
     */

    private void settings() {

        /* Motor Settings */

        //Drivetrain Motor Settings
        FL.setDirection(DcMotorSimple.Direction.FORWARD);
        FR.setDirection(DcMotorSimple.Direction.FORWARD);
        BR.setDirection(DcMotorSimple.Direction.FORWARD);
        BL.setDirection(DcMotorSimple.Direction.FORWARD);

        //Relic Motor Settings
        relicSlide.setDirection(DcMotorSimple.Direction.REVERSE);
        relicSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Glyph Motor Settings
        glyphArm.setDirection(DcMotorSimple.Direction.REVERSE);
        glyphArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        if (PerpetualExistenceThread.didAuton) {
            glyphArm.setPower(-0.15);
            wait(1250);
            glyphArm.setPower(0);
        }

        /* Servo Settings */

        //Clamp Settings
        clamp.setDirection(Servo.Direction.REVERSE);
        clamp.scaleRange(0.5, 1);
        clamp.setPosition(CLAMP_REST);

        //Pivot Settings
        pivot.setDirection(Servo.Direction.REVERSE);
        pivot.setPosition(PIVOT_REST);

        //Left Claw Settings
        LC.setDirection(Servo.Direction.FORWARD);
        LC.scaleRange(0.15, 0.8);
        LC.setPosition(LC_REST);

        //Right Claw Settings
        RC.setDirection(Servo.Direction.FORWARD);
        RC.scaleRange(0.2, 0.85);
        RC.setPosition(RC_REST);

        //Jewel Servo Settings
        jewel.setDirection(Servo.Direction.FORWARD);
        //jewel.scaleRange(0, 0.5);
        jewel.setPosition(JEWEL_REST);
    }

    /**
     * The useGyro method sets parameters to and initializes the REV Robotics IMU on the robot.  The
     * device is permanently hooked up, but not always enabled.  Example: This method is useful in
     * autonomous when using the gyro is helpful.
     */

    void useGyro() {

        //Gyro Settings
        BNO055IMU.Parameters gyroParams = new BNO055IMU.Parameters();
        gyroParams.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        gyroParams.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        gyroParams.calibrationDataFile = "BNO055IMUCalibration.json";

        //Initialize and Calibrate IMU
        gyro.initialize(gyroParams);
    }

    void telemetry(String tag, Object data) {
        telemetry.addData(tag, data.toString());
    }

    void wait(int millis) {
        t.reset();
        while(t.milliseconds() < millis) {
            Thread.yield();
        }
    }
}