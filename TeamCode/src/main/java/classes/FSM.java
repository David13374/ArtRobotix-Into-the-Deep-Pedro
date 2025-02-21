package classes;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.concurrent.TimeUnit;

import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import constants.motorInit;

@Config
public class FSM {

    public static final int tolerance = 10;
    public PIDFArm PIDF;
    boolean extended = false, clawstate = true;

    ServoImplEx ClawOuttake, AxialServoOuttake, ArmServo;

    ServoImplEx ClawIntake, ClawRotate, ClawVertical, AxialServoIntake, ExtensionR, ExtensionL;

    public static double ClawIntakeInitPos = 0.3, ClawRotateInitPos = 0.63, ClawVerticalInitPos = 0.9, AxialServoIntakeInitPos = 0.1, ExtensionInitPos = 0, ClawOuttakeInitPos = 0.8, AxialServoOuttakeInitPos = 0.8, ArmServoInitPos = 0.77;
    public static double ClawRotateTransferPos = 0.021, ClawVerticalTransferPos = 0.95, AxialServoIntakeTransferPos = 0.15;

    public static double AxialServoOuttakeTransferPos = 0.99, ArmServoTransferPos = 0.82;
    public static double AxialServoOuttakeBasketPos = 0.15, ArmServoBasketPos = 0.6;
    public static double LowSpecimenPos = 0, HighSpecimenPos = 420, LowBasketPos = 550, HighBasketPos = 1100;
    public static double AxialServoOuttakeSpecimenPos = 0.15, ArmServoSpecimenPos = 0.6;

    public double extendedpos = 0.3, retractedpos = 0;

    enum robotState {
        READY,
        MOVING,
        WAITINGINPUT,
        SLIDERSMOVING,
        RETURNING,
        RESET
    }

    public void resetSliders() {
        PIDF.resetSliders();
    }

    enum GoingToWhere {
        HighSpecimen,
        LowSpecimen,
        No,
    }

    ElapsedTime transfert, timer2;
    robotState currentState = robotState.READY;

    public void init() {
        ClawIntake.setPosition(ClawIntakeInitPos);
        ClawOuttake.setPosition(ClawOuttakeInitPos);
        ClawRotate.setPosition(ClawRotateInitPos);
        ClawVertical.setPosition(ClawVerticalInitPos);
        AxialServoIntake.setPosition(AxialServoIntakeInitPos);
        AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
        ArmServo.setPosition(ArmServoInitPos);
        ExtensionL.setPosition(ExtensionInitPos);
        ExtensionR.setPosition(ExtensionInitPos);
        PIDF.setTarget(0);
        currentState = robotState.READY;
    }

    public static final double openpos = 0.5, closepos = 0;
    motorInit r;
    public FSM(HardwareMap hardwareMap) {
        PIDF = new PIDFArm(hardwareMap, true);
        r = new motorInit(hardwareMap);
        ClawIntake = r.ClawIntake;
        ClawOuttake = r.ClawOuttake;
        ClawRotate = r.ClawRotate;
        ClawVertical = r.ClawVertical;
        AxialServoIntake = r.AxialServoIntake;
        AxialServoOuttake = r.AxialServoOuttake;
        ArmServo = r. ArmServo;
        ExtensionL =r.ExtensionL;
        ExtensionR = r.ExtensionR;

        transfert = new ElapsedTime();
        timer2 = new ElapsedTime();
        timer2.reset();
    }

    public robotState returnState() {
        return currentState;
    }

    public boolean isExtended() {
        return extended;
    }

    public boolean ClawState() {
        return clawstate;
    }

    public double ExtendoValue() {
        return extendedpos2;
    }

    public double getTimer() {
        return timer2.time();
    }

    GamepadKeys.Button //extendbutton = GamepadKeys.Button.A,
            extendbuttonspecimen = GamepadKeys.Button.LEFT_BUMPER,
            transferbutton = GamepadKeys.Button.DPAD_DOWN,
            specimenhighbutton = GamepadKeys.Button.DPAD_UP,
            specimenlowbutton = GamepadKeys.Button.DPAD_RIGHT,
            highbasketbutton = GamepadKeys.Button.Y,
            lowbasketbutton = GamepadKeys.Button.X,
            specimenbutton = GamepadKeys.Button.RIGHT_BUMPER,
            wristupbutton = GamepadKeys.Button.X,
            ReleaseButton = GamepadKeys.Button.LEFT_BUMPER,
            ResetTransferButton = GamepadKeys.Button.DPAD_LEFT,
            retractButton = GamepadKeys.Button.LEFT_BUMPER;
    GamepadKeys.Trigger openclaw = GamepadKeys.Trigger.RIGHT_TRIGGER,
            closeclaw = GamepadKeys.Trigger.LEFT_TRIGGER;
    public static double reqTime = 0.1, reqTime2 = 1, timeDoneTotal = 1.55, SpecimenTime = 1, ServoTime = 1, RotatingTime = 0, TimeReqFromBasket = 1;

    public static double highBasketTime = 0.05, lowBasketTime = 0.03;
    public static double ReqTimeToReturn = 0.05, ReqTimeToReturnFromHighB = 0, ReqTimeToReturnFromLowB = 0, ReqTimeToReturnFromHighS = 0.3, ReqTimeToReturnFromLowS = 0.1, ReqTimeToPlaceSpecimen = 3;
    GoingToWhere GoingToSpecimen = GoingToWhere.No;
    public static double ArmServoPos = 0.45, AxialServoOuttakePos = 0.3;
    public static double TicksToRiseSpecimen = 125;
    public static double clawOpenTime = 0, timeToRaise = 0.1;
    public static double AxialServoIntakeUpPos = 0.45, AxialServoIntakeDownPos = 0.6, ClawVerticalGrabPos = 0;
    public static double PosReq = 420, reqTimeA;
    public static final double difference = 0.01;
    double extendedpos2 = extendedpos;

    public static final double AxialServoMultiplier = -0.01, ExtendoServoMultiplier = -0.008;
    static double currenetwristpos;
    public static double resetTime = 0.05;
    public static final double extendomin = 0.15, extendomax = 0.4;

    static boolean wristup = true, pressedButton = false, specimenButton = false, PIDFdiff = false, reset = false;

    public void update(GamepadEx driver2gamepad, GamepadEx driver1gamepad) {
        switch (currentState) {
            case READY:
                if (driver1gamepad.wasJustPressed(extendbuttonspecimen)) {
                    if (extended) {
                        AxialServoIntake.setPosition(AxialServoIntakeTransferPos);
                        if (specimenButton) {
                            ClawRotate.setPosition(0.01);
                        } else
                            ClawRotate.setPosition(ClawRotateTransferPos);
                        ClawVertical.setPosition(ClawVerticalTransferPos);
                        ExtensionL.setPosition(retractedpos);
                        ExtensionR.setPosition(retractedpos + difference);
                        wristup = true;
                    } else {
                        specimenButton = driver1gamepad.wasJustPressed(extendbuttonspecimen);
                        currenetwristpos = 0.61;
                        ExtensionL.setPosition(extendedpos);
                        ExtensionR.setPosition(extendedpos + difference);
                    }
                    extendedpos2 = extendedpos;
                    extended = !extended;
                }

                if (extended) {
                    if (driver1gamepad.wasJustPressed(wristupbutton))
                        wristup = !wristup;

                    if (wristup) {
                        ClawVertical.setPosition(ClawVerticalGrabPos);
                        AxialServoIntake.setPosition(AxialServoIntakeUpPos);
                    } else {
                        ClawVertical.setPosition(ClawVerticalGrabPos);
                        AxialServoIntake.setPosition(AxialServoIntakeDownPos);
                    }
                    currenetwristpos += driver1gamepad.getRightX() * AxialServoMultiplier;
                    if (currenetwristpos < 0)
                        currenetwristpos = 0;
                    if (currenetwristpos > 1)
                        currenetwristpos = 1;
                    ClawRotate.setPosition(currenetwristpos);
                    extendedpos2 += driver1gamepad.getRightY() * ExtendoServoMultiplier;
                    if (extendedpos2 < extendomin)
                        extendedpos2 = extendomin;
                    if (extendedpos2 > extendomax)
                        extendedpos2 = extendomax;
                    ExtensionL.setPosition(extendedpos2);
                    ExtensionR.setPosition(extendedpos2 + difference);
                }
                if (driver2gamepad.getTrigger(openclaw) != 0)
                    ClawIntake.setPosition(openpos);
                if (driver2gamepad.getTrigger(closeclaw) != 0)
                    ClawIntake.setPosition(closepos);
                if (driver2gamepad.wasJustPressed(transferbutton)) {
                    PIDFdiff = false;
                    reset = false;
                    if (extended) {
                        extended = false;
                        wristup = true;
                        ExtensionL.setPosition(retractedpos);
                        ExtensionR.setPosition(retractedpos + difference);
                    }
                    if (specimenButton) {
                        specimenButton = false;
                        ClawRotate.setPosition(0.01);
                    } else
                        ClawRotate.setPosition(ClawRotateTransferPos);
                    pressedButton = false;
                    AxialServoIntake.setPosition(AxialServoIntakeTransferPos);
                    //ClawRotate.setPosition(ClawRotateTransferPos);
                    ClawOuttake.setPosition(openpos);
                    ClawVertical.setPosition(ClawVerticalTransferPos);
                    transfert.reset();
                    transfert.startTime();
                    currentState = robotState.MOVING;
                }
                GoingToSpecimen = GoingToWhere.No;
                break;
            case MOVING:
                if (transfert.time(TimeUnit.SECONDS) > reqTime) {
                    AxialServoOuttake.setPosition(AxialServoOuttakeTransferPos);
                    ArmServo.setPosition(ArmServoTransferPos);
                    ClawOuttake.setPosition(openpos);
                }

                if (transfert.time(TimeUnit.SECONDS) > reqTime2) {
                    ClawOuttake.setPosition(closepos);
                }

                if (transfert.time(TimeUnit.SECONDS) > timeDoneTotal) {
                    ClawIntake.setPosition(openpos);
                    currentState = robotState.WAITINGINPUT;
                }
                break;
            case WAITINGINPUT:
                if(driver2gamepad.wasJustPressed(ResetTransferButton)) {
                    reset = true;
                    ReqTimeToReturn = resetTime;
                    AxialServoOuttake.setPosition(AxialServoOuttakeBasketPos);
                    ArmServo.setPosition(ArmServoBasketPos);
                    transfert.reset();
                    transfert.startTime();
                    currentState = robotState.SLIDERSMOVING;
                }
                if (driver2gamepad.wasJustPressed(specimenhighbutton)) {
                    PosReq = HighSpecimenPos;
                    AxialServoOuttake.setPosition(AxialServoOuttakeSpecimenPos);
                    ArmServo.setPosition(ArmServoSpecimenPos);
                    GoingToSpecimen = GoingToWhere.HighSpecimen;
                    ReqTimeToReturn = ReqTimeToReturnFromHighS;
                    transfert.reset();
                    transfert.startTime();
                    currentState = robotState.SLIDERSMOVING;
                }
                if (driver2gamepad.wasJustPressed(specimenlowbutton)) {
                    PosReq = LowSpecimenPos;
                    AxialServoOuttake.setPosition(AxialServoOuttakeSpecimenPos);
                    ArmServo.setPosition(ArmServoSpecimenPos);
                    GoingToSpecimen = GoingToWhere.LowSpecimen;
                    PIDF.setTarget(LowBasketPos);
                    ReqTimeToReturn = ReqTimeToReturnFromLowS;
                    currentState = robotState.SLIDERSMOVING;
                    transfert.reset();
                    transfert.startTime();
                }
                if (driver2gamepad.wasJustPressed(highbasketbutton)) {
                    ReqTimeToReturn = ReqTimeToReturnFromHighB;
                    PIDF.setTarget(HighBasketPos);
                    currentState = robotState.SLIDERSMOVING;
                    reqTimeA = highBasketTime;
                    transfert.reset();
                    transfert.startTime();
                }
                if (driver2gamepad.wasJustPressed(lowbasketbutton)) {
                    ReqTimeToReturn = ReqTimeToReturnFromLowB;
                    PIDF.setTarget(LowBasketPos);
                    currentState = robotState.SLIDERSMOVING;
                    reqTimeA = lowBasketTime;
                    transfert.reset();
                    transfert.startTime();
                }
                break;
            case SLIDERSMOVING:
                if(reset) {
                    if(transfert.time(TimeUnit.SECONDS) > RotatingTime) {
                        ClawOuttake.setPosition(openpos);
                        if(!pressedButton) {
                            pressedButton = true;
                            transfert.reset();
                            transfert.startTime();
                        }
                        if(transfert.time(TimeUnit.SECONDS) > clawOpenTime) {
                            AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
                            ArmServo.setPosition(ArmServoInitPos);
                            transfert.reset();
                            transfert.startTime();
                            currentState = robotState.RETURNING;
                        }
                    }
                }
                else
                if (GoingToSpecimen != GoingToWhere.No) {
                    if (!PIDFdiff) {
                        PIDF.setTarget(PosReq);
                        PIDFdiff = true;
                    }
                    if (driver2gamepad.wasJustPressed(specimenbutton)) {
                        PIDF.setTarget(PosReq + TicksToRiseSpecimen);
                        if (!pressedButton) {
                            timer2.reset();
                            timer2.startTime();
                            pressedButton = true;
                        }
                    }
                    /*if (driver2gamepad.wasJustPressed(retractButton)) {
                        if (!pressedButton) {
                            timer2.reset();
                            timer2.startTime();
                            pressedButton = true;
                        }
                    }*/
                    if (pressedButton) {
                        if (timer2.time(TimeUnit.SECONDS) > timeToRaise)
                            ClawOuttake.setPosition(openpos);
                        if (timer2.time(TimeUnit.SECONDS) > timeToRaise + clawOpenTime) {
                            AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
                            ArmServo.setPosition(ArmServoInitPos);
                            transfert.reset();
                            transfert.startTime();
                            currentState = robotState.RETURNING;
                        }
                    }
                } else {
                    if (transfert.time(TimeUnit.SECONDS) > reqTimeA) {
                        AxialServoOuttake.setPosition(AxialServoOuttakeBasketPos);
                        ArmServo.setPosition(ArmServoBasketPos);
                        if (driver2gamepad.wasJustPressed(ReleaseButton)) {
                            timer2.reset();
                            timer2.startTime();
                            pressedButton = true;
                            ClawOuttake.setPosition(openpos);
                        }
                        if (pressedButton && timer2.time(TimeUnit.SECONDS) > clawOpenTime) {
                            AxialServoOuttake.setPosition(AxialServoOuttakeInitPos);
                            ArmServo.setPosition(ArmServoInitPos);
                            currentState = robotState.RETURNING;
                            transfert.reset();
                            transfert.startTime();
                            timer2.reset();
                            pressedButton = false;
                        }
                    }
                }
                break;
            case RETURNING:

                if (transfert.time(TimeUnit.SECONDS) > RotatingTime) {
                    PIDF.retract();
                }

                if (PIDF.isRetracted()) {
                    currentState = robotState.READY;
                    //PIDF.isResetForRetraction = false;
                    PIDF.retractReset();
                }
                break;
        }
        if(currentState != robotState.RETURNING) PIDF.update();
    }
}