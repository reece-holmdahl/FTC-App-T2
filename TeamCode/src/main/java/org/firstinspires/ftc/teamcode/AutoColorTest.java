package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Color Test")
public class AutoColorTest extends LinearOpMode {

    //Link to Robot class
    private Robot r = new Robot(telemetry);

    //Team selector booleans
    private boolean red;
    private boolean blue;

    //Turning variables
    double turnSpeed = 0.3;
    long turnMillis = 500;

    public void runOpMode() {
        r.map(hardwareMap);
        while (!opModeIsActive()) {

            //Send team info to telemetry
            telemetry.addData("Red", red);
            telemetry.addData("Blue", blue);

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
                blue = false;
            }

            //Select team blue if you press the blue button (x) on the controller
            if (gamepad1.x) {
                blue = true;
                red = false;
            }

            //Yield thread so OpMode doesn't crash
            idle();
        }
        waitForStart();
        r.jewel.setPosition(1);
        sleep(1000);
        while (opModeIsActive()) {

            //Send color init data to telemetry
            telemetry.addData("R", r.color.getNormalizedColors().red);
            telemetry.addData("G", r.color.getNormalizedColors().green);
            telemetry.addData("B", r.color.getNormalizedColors().blue);
            telemetry.addData("A", r.color.getNormalizedColors().alpha);
            telemetry.addData("Red var", red && !seeRed());
            telemetry.addData("Blue var", blue && seeRed());
            telemetry.addData("See Red", seeRed());

            //Update telemetry
            telemetry.update();
        }
    }

    private void goRight() {
        setMotors(turnSpeed);
        sleep(turnMillis);
        setMotors(0);
    }

    private void goLeft() {
        setMotors(-turnSpeed);
        sleep(turnMillis);
        setMotors(0);
    }

    private void setMotors(double power) {
        r.FL.setPower(power);
        r.FR.setPower(power);
        r.BR.setPower(power);
        r.BL.setPower(power);
    }

    private boolean seeRed() {
        return red() > blue();
    }

    private float red() {
        return r.color.getNormalizedColors().red;
    }

    private float blue() {
        return r.color.getNormalizedColors().blue;
    }
}