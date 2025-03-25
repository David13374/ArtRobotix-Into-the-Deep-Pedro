package opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import classes.PIDFArm;

@Config
@TeleOp(name = "ServoTester")
public class ServoTester extends LinearOpMode {

    public static double ClawIntakeInitPos = 0.3, ClawRotateInitPos = 0.63, ClawVerticalInitPos = 0.9, AxialServoIntakeInitPos = 0.1, ExtensionInitPos = 0, ClawOuttakeInitPos = 0.8, AxialServoOuttakeInitPos = 0.8, ArmServoInitPos = 0.77;
    public ServoImplEx ClawOuttake, AxialServoOuttake, ArmServo;
    public ServoImplEx ClawIntake, ClawRotate, ClawVertical, AxialServoIntake, ExtensionR, ExtensionL;
    public static boolean useLeft = true, useRight = true;
    public static double difference = 0.03;
    PIDFArm PIDF;

    public static double targetPos = 0;
    @Override
    public void runOpMode() {
        ClawIntake = hardwareMap.get(ServoImplEx.class, "ClawIntake");
        ClawOuttake = hardwareMap.get(ServoImplEx.class, "ClawOuttake");
        ClawRotate = hardwareMap.get(ServoImplEx.class, "ClawRotate");
        ClawVertical = hardwareMap.get(ServoImplEx.class, "ClawVertical");
        AxialServoIntake = hardwareMap.get(ServoImplEx.class, "AxialServoIntake");
        AxialServoOuttake = hardwareMap.get(ServoImplEx.class, "AxialServoOuttake");
        ArmServo = hardwareMap.get(ServoImplEx.class, "ArmServo");
        ExtensionL = hardwareMap.get(ServoImplEx.class, "ExtensionL");
        ExtensionR = hardwareMap.get(ServoImplEx.class, "ExtensionR");

        ExtensionL.setDirection(Servo.Direction.REVERSE);
        ClawRotate.setPwmRange(new PwmControl.PwmRange(500, 2500));
        ExtensionL.setPwmRange(new PwmControl.PwmRange(500, 2500));
        ExtensionR.setPwmRange(new PwmControl.PwmRange(500, 2500));

        PIDF = new PIDFArm(hardwareMap, true);

        waitForStart();

        while(opModeIsActive()) {
            ClawIntake.setPosition(ClawIntakeInitPos);
            ClawOuttake.setPosition(ClawOuttakeInitPos);
            ClawRotate.setPosition(ClawRotateInitPos);
            ClawVertical.setPosition(ClawVerticalInitPos);
            AxialServoIntake.setPosition(AxialServoIntakeInitPos);
            AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
            ArmServo.setPosition(ArmServoInitPos);
            if(useLeft)
                ExtensionL.setPosition(ExtensionInitPos + difference);
            if(useRight)
                ExtensionR.setPosition(ExtensionInitPos);
            PIDF.setTarget(targetPos);
            PIDF.update();
        }
    }
}
