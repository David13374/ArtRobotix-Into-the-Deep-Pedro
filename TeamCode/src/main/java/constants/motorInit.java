package constants;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class motorInit {
    public DcMotorEx leftFront, leftBack, rightFront, rightBack;
    public ServoImplEx ClawOuttake, AxialServoOuttake, ArmServo;
    public ServoImplEx ClawIntake, ClawRotate, ClawVertical, AxialServoIntake, ExtensionR, ExtensionL;

    public motorInit(HardwareMap hardwareMap) {
        leftFront = hardwareMap.get(DcMotorEx.class, "leftFront");
        leftBack = hardwareMap.get(DcMotorEx.class, "leftBack");
        rightFront = hardwareMap.get(DcMotorEx.class, "rightFront");
        rightBack = hardwareMap.get(DcMotorEx.class, "rightBack");

        leftFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftBack.setDirection(DcMotorEx.Direction.REVERSE);
        rightFront.setDirection(DcMotorEx.Direction.FORWARD);
        rightBack.setDirection(DcMotorEx.Direction.FORWARD);

        leftBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

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

        ClawIntake.setPosition(constants.ClawIntakeInitPos);
        ClawOuttake.setPosition(constants.ClawOuttakeInitPos);
        ClawRotate.setPosition(constants.ClawRotateInitPos);
        ClawVertical.setPosition(constants.ClawVerticalInitPos);
        AxialServoIntake.setPosition(constants.AxialServoIntakeInitPos);
        AxialServoOuttake.setPosition(constants.AxialServoOuttakeInitPos);
        ArmServo.setPosition(constants.ArmServoInitPos);
        ExtensionL.setPosition(constants.ExtensionInitPos);
        ExtensionR.setPosition(constants.ExtensionInitPos);
    }
}
