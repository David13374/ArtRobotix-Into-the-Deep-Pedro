package classes;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class Outtake {

    ServoImplEx ClawOuttake, AxialServoOuttake, ArmServo;
    public static double AxialServoOuttakeBasketPos = 0.15, ArmServoBasketPos = 0.6;
    public static double AxialServoOuttakeSpecimenPos = 0.15, ArmServoSpecimenPos = 0.6;
    public static double AxialServoOuttakeTransferPos = 0.99, ArmServoTransferPos = 0.82;
    public static double ClawOuttakeInitPos = 0.8, AxialServoOuttakeInitPos = 0.8, ArmServoInitPos = 0.77;
    public static double AxialServoOuttakeWallPos, ArmServoWallPos;

    public Outtake(HardwareMap hardwareMap) {
        ClawOuttake = hardwareMap.get(ServoImplEx.class, "ClawOuttake");
        AxialServoOuttake = hardwareMap.get(ServoImplEx.class, "AxialServoOuttake");
        ArmServo = hardwareMap.get(ServoImplEx.class, "ArmServo");

        ClawOuttake.setPosition(ClawOuttakeInitPos);
        AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
        ArmServo.setPosition(ArmServoInitPos);
    }

    public void setBasketPos() {
        AxialServoOuttake.setPosition(Outtake.AxialServoOuttakeBasketPos);
        ArmServo.setPosition(Outtake.ArmServoBasketPos);
    }
    public void setSpecimenPos() {
        AxialServoOuttake.setPosition(Outtake.AxialServoOuttakeSpecimenPos);
        ArmServo.setPosition(Outtake.ArmServoSpecimenPos);
    }
    public void setOuttakeTransfer() {
        AxialServoOuttake.setPosition(Outtake.AxialServoOuttakeTransferPos);
        ArmServo.setPosition(Outtake.ArmServoTransferPos);
    }
    public void resetOuttake() {
        AxialServoOuttake.setPosition(Outtake.AxialServoOuttakeInitPos);
        ArmServo.setPosition(Outtake.ArmServoInitPos);
    }
    public void openOuttakeClaw() {
        ClawOuttake.setPosition(Intake.openpos);
    }
    public void closeOuttakeClaw() {
        ClawOuttake.setPosition(Intake.closepos);
    }

    public void setWallPos() {
        AxialServoOuttake.setPosition(AxialServoOuttakeWallPos);
        ArmServo.setPosition(ArmServoWallPos);
    }

}
