package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import classes.HzMonitor;
import classes.PIDFArm;
@TeleOp
@Config
public class PIDFArmTest extends LinearOpMode {
    PIDFArm pidfArm;
    ServoImplEx armServo;
    HzMonitor hz;
    public static double target = 0;
    @Override
    public void runOpMode() {
        pidfArm = new PIDFArm(hardwareMap, true);
        armServo = hardwareMap.get(ServoImplEx.class, "ArmServo");
        armServo.setPosition(0.77);
        hz=new HzMonitor();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        waitForStart();
        while (opModeIsActive()) {
            double fps = hz.update();
            pidfArm.setTarget(target);
            pidfArm.update();
            telemetry.addData("currentPos", pidfArm.getArmPosL());
            telemetry.addData("targetPos", target);
            telemetry.addData("power", pidfArm.getPower());
            telemetry.addData("fps", fps);
            telemetry.update();
        }
    }
}
