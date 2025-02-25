package constants;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import dev.frozenmilk.dairy.cachinghardware.CachingDcMotorEx;

public class robotInit {
    public DcMotorEx leftFront, leftBack, rightFront, rightBack;
    public ServoImplEx ClawOuttake, AxialServoOuttake, ArmServo;
    public ServoImplEx ClawIntake, ClawRotate, ClawVertical, AxialServoIntake, ExtensionR, ExtensionL;

    HardwareMap hardwareMap;

    public static enum init {
        MOTORS,
        INTAKE,
        OUTTAKE,
        SERVO,
        ALL,
    }

    public robotInit(init a, HardwareMap hmap) {
        hardwareMap = hmap;
        switch(a) {
            case MOTORS:
                initMotors();
                break;
            case INTAKE:
                initIntake();
                break;
            case OUTTAKE:
                initOuttake();
                break;
            case SERVO:
                initIntake();
                initOuttake();
                break;
            case ALL:
                initMotors();
                initOuttake();
                initIntake();
                break;
        }
    }

    public void initMotors() {
        leftFront = new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, "leftFront"));
        leftBack = new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, "leftRear"));
        rightFront = new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, "rightFront"));
        rightBack = new CachingDcMotorEx(hardwareMap.get(DcMotorEx.class, "rightRear"));

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
    }

    public void initIntake() {
        ClawIntake = hardwareMap.get(ServoImplEx.class, "ClawIntake");
        ClawRotate = hardwareMap.get(ServoImplEx.class, "ClawRotate");
        ClawVertical = hardwareMap.get(ServoImplEx.class, "ClawVertical");
        AxialServoIntake = hardwareMap.get(ServoImplEx.class, "AxialServoIntake");
        ExtensionL = hardwareMap.get(ServoImplEx.class, "ExtensionL");
        ExtensionR = hardwareMap.get(ServoImplEx.class, "ExtensionR");

        ExtensionL.setDirection(Servo.Direction.REVERSE);
        ClawRotate.setPwmRange(new PwmControl.PwmRange(500, 2500));

        ClawIntake.setPosition(constants.openpos);
        ClawRotate.setPosition(constants.ClawRotateInitPos);
        ClawVertical.setPosition(constants.ClawVerticalInitPos);
        AxialServoIntake.setPosition(constants.AxialServoIntakeInitPos);
        ExtensionL.setPosition(constants.ExtensionInitPos);
        ExtensionR.setPosition(constants.ExtensionInitPos);
    }
    public void initOuttake() {

        ClawOuttake = hardwareMap.get(ServoImplEx.class, "ClawOuttake");
        AxialServoOuttake = hardwareMap.get(ServoImplEx.class, "AxialServoOuttake");
        ArmServo = hardwareMap.get(ServoImplEx.class, "ArmServo");

        ClawOuttake.setPosition(constants.ClawOuttakeInitPos);
        AxialServoOuttake.setPosition(constants.AxialServoOuttakeInitPos);
        ArmServo.setPosition(constants.ArmServoInitPos);
    }
}
