package constants;

import com.qualcomm.robotcore.hardware.HardwareMap;

import constants.motorInit;
public class functions {

    motorInit r;
    public static double AxialServoOuttakeBasketPos = 0.15, ArmServoBasketPos = 0.6;
    public static double AxialServoOuttakeSpecimenPos = 0.15, ArmServoSpecimenPos = 0.6;
    public static double AxialServoIntakeUpPos = 0.45, AxialServoIntakeDownPos = 0.6, ClawVerticalGrabPos = 0;
    public static final double openpos = 0.5, closepos = 0;
    public static double ClawRotateTransferPos = 0.021, ClawVerticalTransferPos = 0.95, AxialServoIntakeTransferPos = 0.15, AxialServoOuttakeTransferPos = 0.99, ArmServoTransferPos = 0.82;
    public static double ClawIntakeInitPos = 0.3, ClawRotateInitPos = 0.63, ClawVerticalInitPos = 0.9, AxialServoIntakeInitPos = 0.1, ExtensionInitPos = 0, ClawOuttakeInitPos = 0.8, AxialServoOuttakeInitPos = 0.8, ArmServoInitPos = 0.77;
    public void openIntakeClaw() { r.ClawIntake.setPosition(openpos); }
    public void closeIntakeClaw() {
        r.ClawIntake.setPosition(closepos);
    }
    public void openOuttakeClaw() {
        r.ClawOuttake.setPosition(openpos);
    }
    public void closeOuttakeClaw() {
        r.ClawOuttake.setPosition(closepos);
    }

    public void setIntakeTransfer() {
        r.AxialServoIntake.setPosition(AxialServoIntakeTransferPos);
        r.ClawRotate.setPosition(ClawRotateTransferPos);
        r.ClawVertical.setPosition(ClawVerticalTransferPos);
    }
    public void setOuttakeTransfer() {
        r.AxialServoOuttake.setPosition(AxialServoOuttakeTransferPos);
        r.ArmServo.setPosition(ArmServoTransferPos);
    }
    public void setWristUp() {
        r.ClawVertical.setPosition(ClawVerticalGrabPos);
        r.AxialServoIntake.setPosition(AxialServoIntakeUpPos);
    }
    public void setWristDown() { r.AxialServoIntake.setPosition(AxialServoIntakeDownPos); }
    public void setBasketPos() {
        r.AxialServoOuttake.setPosition(AxialServoOuttakeBasketPos);
        r.ArmServo.setPosition(ArmServoBasketPos);
    }
    public void setSpecimenPos() {
        r.AxialServoOuttake.setPosition(AxialServoOuttakeSpecimenPos);
        r.ArmServo.setPosition(ArmServoSpecimenPos);
    }
    public void resetOuttake() {
        r.AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
        r.ArmServo.setPosition(ArmServoInitPos);
    }

    public void initRobot() {
        r.ClawIntake.setPosition(openpos);
        r.ClawOuttake.setPosition(openpos);
        r.ClawRotate.setPosition(ClawRotateInitPos);
        r.ClawVertical.setPosition(ClawVerticalInitPos);
        r.AxialServoIntake.setPosition(AxialServoIntakeInitPos);
        r.AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
        r.ArmServo.setPosition(ArmServoInitPos);
    }
}
