package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Autonomous")
public class AutoPeriod extends LinearOpMode {

    //Link to Robot class
    private final Robot r = new Robot(telemetry);

    //Team selector booleans
    private boolean red;

    //Turning variables
    private final double turnSpeed = 0.3;
    private final int turnMillis = 300;

    /**
     * The runOpMode method is used in LinearOpMode to plan out the program. All code put before
     * the waitForStart method is ran during the initialization phase and all code after is either
     * ran on start, or looped during the OpMode if specified to.
     */

    public void runOpMode() {
        r.map(hardwareMap);
        while (!opModeIsActive()) {

            //Send team info to telemetry
            telemetry.addData("Red", red);
            telemetry.addData("Blue", !red);

            //Send color init data to telemetry
            telemetry.addData("R", r.color.getNormalizedColors().red);
            telemetry.addData("G", r.color.getNormalizedColors().green);
            telemetry.addData("B", r.color.getNormalizedColors().blue);
            telemetry.addData("A", r.color.getNormalizedColors().alpha);

            //Update telemetry
            telemetry.update();

            //Select team red if you press the red button (b) on the controller
            if (gamepad1.b) {
                red = true;
            }

            //Select team blue if you press the blue button (x) on the controller
            if (gamepad1.x) {
                red = false;
            }

            //Yield thread so OpMode doesn't crash
            idle();
        }
        waitForStart();
        r.jewel.setPosition(1);
        sleep(2000);

        //Check if situation is right to hit side of color sensor, or do the opposite.
        if (red && !seeRed() || !red && seeRed()) {
            goRight();
        } else {
            goLeft();
        }
    }

    /**
     * The goRight method is very simple, it turns the robot to the right.
     */

    private void goRight() {
        setMotors(-turnSpeed);
        sleep(turnMillis);
        setMotors(0);
    }

    /**
     * The goLeft method is also very simple, it turns the robot to the left instead.
     */

    private void goLeft() {
        setMotors(turnSpeed);
        sleep(turnMillis);
        setMotors(0);
    }

    /**
     * The setMotors method sets all Drivetrain motors to a certain power specified.
     * @param power The power to set all Drivetrain motors
     */

    private void setMotors(double power) {
        r.FL.setPower(power);
        r.FR.setPower(power);
        r.BR.setPower(power);
        r.BL.setPower(power);
    }

    /**
     * The seeRed method returns true if the color sensor sees more red than blue, and false if
     * otherwise.
     * @return  Returns true or false if it sees red or not
     */

    private boolean seeRed() {
        return red() > blue();
    }

    /*
     * The two doubles below are easier ways to access the red and blue values from the color
     * sensor.
     */

    private double red() {
        return r.color.getNormalizedColors().red;
    }

    private double blue() {
        return r.color.getNormalizedColors().blue;
    }
}