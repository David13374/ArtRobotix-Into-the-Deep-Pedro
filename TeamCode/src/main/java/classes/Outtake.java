package classes;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@Config
public class Outtake {

    ServoImplEx ClawOuttake, AxialServoOuttake, ArmServo;
    public static double AxialServoOuttakeBasketPos = 0.15, ArmServoBasketPos = 0.6;
    public static double AxialServoOuttakeSpecimenPos = 0.15, ArmServoSpecimenPos = 0.6;
    public static double AxialServoOuttakeTransferPos = 0.99, ArmServoTransferPos = 0.82;
    public static double ClawOuttakeInitPos = 0.8, AxialServoOuttakeInitPos = 0.8, ArmServoInitPos = 0.77;
    public static double AxialServoOuttakeWallPos = 0.17, ArmServoWallPos = 0.45;
    public static final double openpos = 0.5, closepos = 0;

    public Outtake(HardwareMap hardwareMap) {
        ClawOuttake = hardwareMap.get(ServoImplEx.class, "ClawOuttake");
        AxialServoOuttake = hardwareMap.get(ServoImplEx.class, "AxialServoOuttake");
        ArmServo = hardwareMap.get(ServoImplEx.class, "ArmServo");

        ClawOuttake.setPosition(openpos);
        AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
        ArmServo.setPosition(ArmServoInitPos);
    }

    public void setBasketPos() {
        AxialServoOuttake.setPosition(AxialServoOuttakeBasketPos);
        ArmServo.setPosition(ArmServoBasketPos);
    }
    public void setSpecimenPos() {
        AxialServoOuttake.setPosition(AxialServoOuttakeSpecimenPos);
        ArmServo.setPosition(ArmServoSpecimenPos);
    }
    public void setOuttakeTransfer() {
        AxialServoOuttake.setPosition(AxialServoOuttakeTransferPos);
        ArmServo.setPosition(ArmServoTransferPos);
    }
    public void resetOuttake() {
        AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
        ArmServo.setPosition(ArmServoInitPos);
    }
    public void openOuttakeClaw() {
        ClawOuttake.setPosition(openpos);
    }
    public void closeOuttakeClaw() {
        ClawOuttake.setPosition(closepos);
    }

    public void setWallPos() {
        AxialServoOuttake.setPosition(AxialServoOuttakeWallPos);
        ArmServo.setPosition(ArmServoWallPos);
    }

}
