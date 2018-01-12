package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Test Color Sensor")
public class TestColor extends LinearOpMode {

    private Robot r = new Robot();
    /* Colors
    final int RED_MIN = 40;
    final int GREEN_MIN = 40;
    final int BLUE_MIN = 40;
    final int ALPHA_MIN = 40;
    */

    public void runOpMode() {
        r.map(hardwareMap, telemetry);
        waitForStart();
        PerpetualExistenceThread.didAuton = false;
        onStart();
        PerpetualExistenceThread.didAuton = true;
        while (opModeIsActive()) {
            r.telemetry("Red", r.color.red());
            r.telemetry("Green", r.color.green());
            r.telemetry("Blue", r.color.blue());
            r.telemetry("Alpha", r.color.alpha());
            r.telemetry("ARGB", r.color.argb());
            idle();
        }
    }

    private void onStart() {
        r.pivot.setPosition(0.032);
        r.wait(1000);
        r.glyphArm.setPower(0.45);
        r.LC.setPosition(0);
        r.RC.setPosition(1);
        r.wait(1500);
        r.glyphArm.setPower(0);
    }
}
