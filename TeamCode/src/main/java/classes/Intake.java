package classes;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import constants.functions;
import constants.robotInit;

public class Intake extends functions {

    robotInit r;
    ServoImplEx extendoL, extendoR, ClawIntake, ClawRotate, ClawVertical, AxialServoIntake;
    public static final double extendomin = 0.15, extendomax = 0.4;
    public static double currentPosExtendo = 0, realPoseExtendo, currentWristPos, realWristPos;
    public double extendedpos = 0.4, retractedpos = 0;
    public static final double AxialServoMultiplier = -0.01, ExtendoServoMultiplier = -0.008;


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
    public Intake(HardwareMap hmap) {
        r = new robotInit(robotInit.init.INTAKE, hmap);
        extendoL = r.ExtensionL;
        extendoR = r.ExtensionR;
        ClawIntake = r.ClawIntake;
        ClawRotate = r.ClawRotate;
        ClawVertical = r.ClawVertical;
        AxialServoIntake = r.AxialServoIntake;
    }

    public void updateState() {
        if(state == extendoState.RETRACTED) {
            state = extendoState.EXTENDED;
            currentPosExtendo = extendedpos;
            wristState = wriststate.UP;
            currentWristPos = realWristPos = 0.61;
            setWristUp();
        }
        else {
            state = extendoState.RETRACTED;
            currentPosExtendo = retractedpos;
            setIntakeTransfer();
        }
        realPoseExtendo = currentPosExtendo;
    }

    public void setExtendoPos(double pos) {
        extendoL.setPosition(pos);
        extendoR.setPosition(pos);
    }
    public void setExtendoPos() {
        double pos = (extendedpos + retractedpos) / 2;
        extendoL.setPosition(pos);
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
    public void retract() {
        if(state == extendoState.EXTENDED) {
            setIntakeTransfer();
            state = extendoState.RETRACTED;
            currentPosExtendo = retractedpos;
            realPoseExtendo = currentPosExtendo;
            extendoR.setPosition(realPoseExtendo);
            extendoL.setPosition(realPoseExtendo);
        }
    }

    public void update(GamepadEx gamepad) {

        currentWristPos = gamepad.getRightY() * ExtendoServoMultiplier;
        currentPosExtendo += gamepad.getRightY() * ExtendoServoMultiplier;

        if(Math.abs(realPoseExtendo - currentPosExtendo) > 0.005) {
            realPoseExtendo = currentPosExtendo;
            extendoR.setPosition(realPoseExtendo);
            extendoL.setPosition(realPoseExtendo);
        }

        if(Math.abs(realWristPos - currentWristPos) > 0.005) {
            realWristPos = currentWristPos;
            ClawRotate.setPosition(realWristPos);
        }
    }
}
