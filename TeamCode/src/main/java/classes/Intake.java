package classes;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

@Config
public class Intake {
    ServoImplEx extendoL, extendoR, ClawIntake, ClawRotate, ClawVertical, AxialServoIntake;
    public static final double extendomin = 0.1, extendomax = 0.4;
    public static double currentPosExtendo = 0, realPoseExtendo, currentWristPos, realWristPos;
    public double extendedpos = 0.4, retractedpos = 0.05;
    public static final double AxialServoMultiplier = -0.01, ExtendoServoMultiplier = -0.008;
    public static double AxialServoIntakeUpPos = 0.45, AxialServoIntakeDownPos = 0.57, ClawVerticalGrabPos = 0;
    public static double openpos = 0.5, closepos = 0.1;
    public static double ClawRotateTransferPos   = 0.021, ClawVerticalTransferPos = 0.88, AxialServoIntakeTransferPos = 0.18;
    public static double ClawRotateInitPos = 0.63, ClawVerticalInitPos = 0.9, AxialServoIntakeInitPos = 0.1, ExtensionInitPos = 0;
    public static double difference = 0.03; 

    public enum wriststate {
        UP,
        DOWN
    }
    public enum extendoState {
        EXTENDED,
        RETRACTED
    }

    public extendoState state = extendoState.RETRACTED;
    public wriststate wristState = wriststate.DOWN;
    public Intake(HardwareMap hardwareMap) {
        ClawIntake = hardwareMap.get(ServoImplEx.class, "ClawIntake");
        ClawRotate = hardwareMap.get(ServoImplEx.class, "ClawRotate");
        ClawVertical = hardwareMap.get(ServoImplEx.class, "ClawVertical");
        AxialServoIntake = hardwareMap.get(ServoImplEx.class, "AxialServoIntake");
        extendoL = hardwareMap.get(ServoImplEx.class, "ExtensionL");
        extendoR = hardwareMap.get(ServoImplEx.class, "ExtensionR");

        extendoL.setDirection(Servo.Direction.REVERSE);
        ClawVertical.setPwmRange(new PwmControl.PwmRange(500, 2500));
        ClawRotate.setPwmRange(new PwmControl.PwmRange(500, 2500));
        extendoL.setPwmRange(new PwmControl.PwmRange(500, 2500));
        extendoR.setPwmRange(new PwmControl.PwmRange(500, 2500));

        ClawIntake.setPosition(openpos);
        ClawRotate.setPosition(ClawRotateInitPos);
        ClawVertical.setPosition(ClawVerticalInitPos);
        AxialServoIntake.setPosition(AxialServoIntakeInitPos);
        extendoL.setPosition(ExtensionInitPos + difference);
        extendoR.setPosition(ExtensionInitPos);
    }

    public void openIntakeClaw() { ClawIntake.setPosition(openpos); }
    public void closeIntakeClaw() {
        ClawIntake.setPosition(closepos);
    }
    public void setIntakeTransfer() {
        AxialServoIntake.setPosition(AxialServoIntakeTransferPos);
        ClawRotate.setPosition(ClawRotateTransferPos);
        ClawVertical.setPosition(ClawVerticalTransferPos);
    }
    public void setWristUp() {
        ClawVertical.setPosition(ClawVerticalGrabPos);
        AxialServoIntake.setPosition(AxialServoIntakeUpPos);
    }
    public void setWristDown() { AxialServoIntake.setPosition(AxialServoIntakeDownPos); }
    public void updateState() {
        if(state == extendoState.RETRACTED) {
            state = extendoState.EXTENDED;
            currentPosExtendo = extendedpos;
            wristState = wriststate.UP;
            currentWristPos = realWristPos = 0.61;
            ClawRotate.setPosition(currentWristPos);
            setExtendoPos(currentPosExtendo);
            setWristUp();
        }
        else {
            state = extendoState.RETRACTED;
            currentPosExtendo = retractedpos;
            setExtendoPos(currentPosExtendo);
            setIntakeTransfer();
        }
        realPoseExtendo = currentPosExtendo;
    }

    public void setExtendoPos(double pos) {
        extendoL.setPosition(pos + difference);
        extendoR.setPosition(pos);
    }
    public void setExtendoPos() {
        double pos = (extendedpos + retractedpos) / 2;
        extendoL.setPosition(pos + difference);
        extendoR.setPosition(pos);
    }

    public void updateWristState() {
        if(wristState == wriststate.UP) {
            setWristDown();
            wristState = wriststate.DOWN;
        }
        else {
            setWristUp();
            wristState = wriststate.UP;
        }
    }
    public void retract(boolean a) {
        if(state == extendoState.EXTENDED || a) {
            setIntakeTransfer();
            state = extendoState.RETRACTED;
            realPoseExtendo = currentPosExtendo = 0;
            extendoR.setPosition(realPoseExtendo);
            extendoL.setPosition(realPoseExtendo + difference);
        }
    }

    public void update(GamepadEx gamepad) {
        if(state == extendoState.EXTENDED) {

            currentWristPos += gamepad.getRightX() * AxialServoMultiplier;
            currentPosExtendo += gamepad.getRightY() * ExtendoServoMultiplier;

            currentPosExtendo = Math.min(currentPosExtendo, extendomax);
            currentPosExtendo = Math.max(currentPosExtendo, extendomin);

            if (Math.abs(realPoseExtendo - currentPosExtendo) > 0.005) {
                realPoseExtendo = currentPosExtendo;
                extendoR.setPosition(realPoseExtendo);
                extendoL.setPosition(realPoseExtendo + difference);
            }

            if (Math.abs(realWristPos - currentWristPos) > 0.005) {
                realWristPos = currentWristPos;
                ClawRotate.setPosition(realWristPos);
            }
        }
    }
}
