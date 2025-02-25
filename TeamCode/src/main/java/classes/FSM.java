package classes;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import constants.motorInit;
import constants.functions;
import classes.Intake;

@Config
public class FSM extends functions {
    public PIDFArm PIDF;
    Intake Extendo;
    boolean extended = false;
    public static double LowSpecimenPos = 0, HighSpecimenPos = 400, LowBasketPos = 550, HighBasketPos = 1100;

    enum robotState {
        READY,
        MOVING,
        WAITINGINPUT,
        SLIDERSMOVING,
        RETURNING,
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
        PIDF.setTarget(0);
        currentState = robotState.READY;
    }
    motorInit r;
    public FSM(HardwareMap hardwareMap) {
        PIDF = new PIDFArm(hardwareMap, true);
        r = new motorInit(hardwareMap);
        Extendo = new Intake(r.ExtensionL, r.ExtensionR, r.ClawIntake, r.ClawRotate, r.ClawVertical, r.AxialServoIntake);

        transfert = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timer2 = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timer2.reset();
    }

    GamepadKeys.Button
            extendbuttonspecimen = GamepadKeys.Button.LEFT_BUMPER,
            transferbutton = GamepadKeys.Button.DPAD_DOWN,
            specimenhighbutton = GamepadKeys.Button.DPAD_UP,
            specimenlowbutton = GamepadKeys.Button.DPAD_RIGHT,
            highbasketbutton = GamepadKeys.Button.Y,
            lowbasketbutton = GamepadKeys.Button.X,
            specimenbutton = GamepadKeys.Button.RIGHT_BUMPER,
            wristupbutton = GamepadKeys.Button.X,
            ReleaseButton = GamepadKeys.Button.LEFT_BUMPER,
            ResetTransferButton = GamepadKeys.Button.DPAD_LEFT;
    GamepadKeys.Trigger openclaw = GamepadKeys.Trigger.RIGHT_TRIGGER,
            closeclaw = GamepadKeys.Trigger.LEFT_TRIGGER;
    public static double reqTime = 0, reqTime2 = 0.15, timeDoneTotal = 0.3, RotatingTime = 0.5;

    public static double highBasketTime = 0.10, lowBasketTime = 0.1;

    GoingToWhere GoingToSpecimen = GoingToWhere.No;

    public static double TicksToRiseSpecimen = 125;
    public static double clawOpenTime = 0.1, timeToRaise = 0.125;
    public static double PosReq = 420, reqTimeA;
    static boolean wristup = true, pressedButton = false, PIDFdiff = false, reset = false;

    public void update(GamepadEx driver2gamepad, GamepadEx driver1gamepad) {
        switch (currentState) {
            case READY:
                if (driver1gamepad.wasJustPressed(extendbuttonspecimen)) {
                    Extendo.updateState();
                    extended = !extended;
                }

                if (extended) {
                    if (driver1gamepad.wasJustPressed(wristupbutton))
                        Extendo.updateWristState();

                    Extendo.update(driver1gamepad);
                }
                if (driver2gamepad.getTrigger(openclaw) != 0)
                    openIntakeClaw();
                if (driver2gamepad.getTrigger(closeclaw) != 0)
                    closeOuttakeClaw();
                if (driver2gamepad.wasJustPressed(transferbutton)) {
                    PIDFdiff = false;
                    reset = false;
                    wristup = false;
                    pressedButton = false;
                    extended = false;
                    Extendo.retract();
                    openOuttakeClaw();
                    transfert.reset();
                    transfert.startTime();
                    currentState = robotState.MOVING;
                }
                GoingToSpecimen = GoingToWhere.No;
                break;
            case MOVING:
                if (transfert.seconds() > reqTime) {
                    setOuttakeTransfer();
                }

                if (transfert.seconds() > reqTime2) {
                    closeOuttakeClaw();
                }

                if (transfert.seconds() > timeDoneTotal) {
                    openIntakeClaw();
                    currentState = robotState.WAITINGINPUT;
                }
                break;
            case WAITINGINPUT:
                if(driver2gamepad.wasJustPressed(ResetTransferButton)) {
                    reset = true;
                    setBasketPos();
                    transfert.reset();
                    transfert.startTime();
                    currentState = robotState.SLIDERSMOVING;
                }
                if (driver2gamepad.wasJustPressed(specimenhighbutton)) {
                    PosReq = HighSpecimenPos;
                    setSpecimenPos();
                    GoingToSpecimen = GoingToWhere.HighSpecimen;
                    transfert.reset();
                    transfert.startTime();
                    currentState = robotState.SLIDERSMOVING;
                }
                if (driver2gamepad.wasJustPressed(specimenlowbutton)) {
                    PosReq = LowSpecimenPos;
                    setSpecimenPos();
                    GoingToSpecimen = GoingToWhere.LowSpecimen;
                    PIDF.setTarget(LowBasketPos);
                    currentState = robotState.SLIDERSMOVING;
                    transfert.reset();
                    transfert.startTime();
                }
                if (driver2gamepad.wasJustPressed(highbasketbutton)) {
                    PIDF.setTarget(HighBasketPos);
                    currentState = robotState.SLIDERSMOVING;
                    reqTimeA = highBasketTime;
                    transfert.reset();
                    transfert.startTime();
                }
                if (driver2gamepad.wasJustPressed(lowbasketbutton)) {
                    PIDF.setTarget(LowBasketPos);
                    currentState = robotState.SLIDERSMOVING;
                    reqTimeA = lowBasketTime;
                    transfert.reset();
                    transfert.startTime();
                }
                break;
            case SLIDERSMOVING:
                if(reset) {
                    if(transfert.seconds() > RotatingTime) {
                        openOuttakeClaw();
                        if(!pressedButton) {
                            pressedButton = true;
                            transfert.reset();
                            transfert.startTime();
                        }
                        if(transfert.seconds() > clawOpenTime) {
                            resetOuttake();
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
                        if (timer2.seconds() > timeToRaise)
                            openOuttakeClaw();
                        if (timer2.seconds() > clawOpenTime) {
                            resetOuttake();
                            transfert.reset();
                            transfert.startTime();
                            currentState = robotState.RETURNING;
                        }
                    }
                } else {
                    if (transfert.seconds() > reqTimeA) {
                        setBasketPos();
                        if (driver2gamepad.wasJustPressed(ReleaseButton)) {
                            timer2.reset();
                            timer2.startTime();
                            pressedButton = true;
                            openOuttakeClaw();
                        }
                        if (pressedButton && timer2.seconds() > clawOpenTime) {
                            resetOuttake();
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

                if (transfert.seconds() > RotatingTime) {
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