package classes;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;


@Config
public class FSM {
    public static double reqTime = 0, reqTime2 = 0.15, timeDoneTotal = 0.3, RotatingTime = 0.5;
    public static double highBasketTime = 0.10, lowBasketTime = 0.1;
    public static double clawOpenTime = 0.1, timeToRaise = 0.15, retractTime = 0.3;
    public static double reqTimeA;
    static boolean wristup = true, pressedButton = false, reset = false, transfer = false;
    public PIDFArm PIDF;
    Intake intake;
    Outtake outtake;
    boolean extended = false;
    ElapsedTime transfert, timer2;
    robotState currentState = robotState.READY;
    GamepadKeys.Button
            extendbuttonspecimen = GamepadKeys.Button.LEFT_BUMPER,
            transferbutton = GamepadKeys.Button.A,
            specimenhighbutton = GamepadKeys.Button.DPAD_UP,
            specimenlowbutton = GamepadKeys.Button.DPAD_RIGHT,
            highbasketbutton = GamepadKeys.Button.Y,
            lowbasketbutton = GamepadKeys.Button.X,
            specimenbutton = GamepadKeys.Button.RIGHT_BUMPER,
            wristupbutton = GamepadKeys.Button.X,
            ReleaseButton = GamepadKeys.Button.RIGHT_BUMPER,
            ResetTransferButton = GamepadKeys.Button.DPAD_LEFT,
            HardResetButton = GamepadKeys.Button.RIGHT_STICK_BUTTON;
    GamepadKeys.Trigger openclaw = GamepadKeys.Trigger.RIGHT_TRIGGER,
            closeclaw = GamepadKeys.Trigger.LEFT_TRIGGER;
    GoingToWhere goingToWhere = GoingToWhere.BASKET;
    public FSM(HardwareMap hardwareMap) {
        PIDF = new PIDFArm(hardwareMap, true);
        intake = new Intake(hardwareMap);
        outtake = new Outtake(hardwareMap);

        transfert = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timer2 = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        timer2.reset();
    }

    public void init() {
        PIDF.setTarget(0);
        currentState = robotState.READY;
    }

    public void update(GamepadEx driver2gamepad, GamepadEx driver1gamepad) {
        if (PIDF.getTarget() < 100 && !transfer) {
            if (driver1gamepad.wasJustPressed(extendbuttonspecimen)) {
                intake.updateState();
                extended = !extended;
            }

            if (extended) {
                if (driver1gamepad.wasJustPressed(wristupbutton)) {
                    intake.updateWristState();
                    transfert.reset();
                }

                intake.update(driver1gamepad, transfert);
            }
        }
        if (driver1gamepad.wasJustPressed(HardResetButton)) {
            currentState = robotState.HARDRESET;
            pressedButton = false;
        }
        switch (currentState) {
            case READY:
                if (driver2gamepad.getTrigger(openclaw) != 0)
                    intake.openIntakeClaw();
                if (driver2gamepad.getTrigger(closeclaw) != 0)
                    intake.closeIntakeClaw();

                if (driver1gamepad.wasJustPressed(transferbutton)) {
                    reset = false;
                    wristup = false;
                    transfer = true;
                    pressedButton = false;
                    outtake.openOuttakeClaw();
                    transfert.reset();
                    currentState = robotState.MOVING;
                }
                break;
            case MOVING:
                if (extended) {
                    intake.retract(false);
                    if (transfert.seconds() > retractTime) {
                        transfert.reset();
                        extended = false;
                    }
                }

                if (!extended && transfert.seconds() > reqTime) {
                    outtake.setOuttakeTransfer();
                }

                if (!extended && transfert.seconds() > reqTime2) {
                    outtake.closeOuttakeClaw();
                }

                if (!extended && transfert.seconds() > timeDoneTotal) {
                    intake.openIntakeClaw();
                    transfer = false;
                    currentState = robotState.WAITINGINPUT;
                }
                break;
            case WAITINGINPUT:
                if (driver2gamepad.wasJustPressed(ResetTransferButton)) {
                    goingToWhere = GoingToWhere.RESET;
                    outtake.setBasketPos();
                    transfert.reset();
                    currentState = robotState.SLIDERSMOVING;
                }
                if (driver2gamepad.wasJustPressed(specimenhighbutton)) {
                    goingToWhere = GoingToWhere.SPECIMEN;
                    PIDF.setTarget(PIDFArm.Positions.HIGH_SPECIMEN);
                    outtake.setSpecimenPos();
                    transfert.reset();
                    currentState = robotState.SLIDERSMOVING;
                }
                if (driver2gamepad.wasJustPressed(specimenlowbutton)) {
                    goingToWhere = GoingToWhere.SPECIMEN;
                    PIDF.setTarget(PIDFArm.Positions.LOW_SPECIMEN);
                    outtake.setSpecimenPos();
                    currentState = robotState.SLIDERSMOVING;
                    transfert.reset();
                }
                if (driver2gamepad.wasJustPressed(highbasketbutton)) {
                    goingToWhere = GoingToWhere.BASKET;
                    PIDF.setTarget(PIDFArm.Positions.HIGH_BASKET);
                    currentState = robotState.SLIDERSMOVING;
                    reqTimeA = highBasketTime;
                    transfert.reset();
                }
                if (driver2gamepad.wasJustPressed(lowbasketbutton)) {
                    goingToWhere = GoingToWhere.BASKET;
                    PIDF.setTarget(PIDFArm.Positions.LOW_BASKET);
                    currentState = robotState.SLIDERSMOVING;
                    reqTimeA = lowBasketTime;
                    transfert.reset();
                }
                break;
            case SLIDERSMOVING:
                switch (goingToWhere) {
                    case RESET:
                        if (transfert.seconds() > RotatingTime) {
                            outtake.openOuttakeClaw();
                            if (!pressedButton) {
                                pressedButton = true;
                                transfert.reset();
                            }
                            if (transfert.seconds() > clawOpenTime) {
                                outtake.resetOuttake();
                                transfert.reset();
                                currentState = robotState.RETURNING;
                            }
                        }
                        break;
                    case SPECIMEN:
                        if (!pressedButton && driver2gamepad.wasJustPressed(specimenbutton)) {
                            PIDF.addPosSpec();
                            timer2.reset();
                            pressedButton = true;
                        }
                        if (pressedButton) {
                            if (timer2.seconds() > timeToRaise)
                                outtake.openOuttakeClaw();
                            if (timer2.seconds() > timeToRaise + clawOpenTime) {
                                outtake.resetOuttake();
                                transfert.reset();
                                currentState = robotState.RETURNING;
                            }
                        }
                        break;
                    case BASKET:
                        if (transfert.seconds() > reqTimeA) {
                            if(!reset) {
                                intake.setExtendoPos(Intake.extendomax);
                                outtake.setBasketPos();
                                reset = true;
                            }
                            if (driver2gamepad.wasJustPressed(ReleaseButton)) {
                                timer2.reset();
                                pressedButton = true;
                                outtake.openOuttakeClaw();
                            }
                            if (pressedButton && timer2.seconds() > clawOpenTime) {
                                outtake.resetOuttake();
                                currentState = robotState.RETURNING;
                                transfert.reset();
                                timer2.reset();
                                pressedButton = false;
                            }
                        }
                        break;
                }
                break;
            case RETURNING:
                if (transfert.seconds() > RotatingTime) {
                    intake.retract(true);
                    PIDF.retract();
                }

                if (PIDF.isRetracted()) {
                    currentState = robotState.READY;
                    PIDF.retractReset();
                }
                break;
            case HARDRESET:
                if(!pressedButton) {
                    intake.retract(true);
                    outtake.resetOuttake();
                    PIDF.retract();
                    outtake.openOuttakeClaw();
                    pressedButton = true;
                }
                if (PIDF.isRetracted()) {
                    currentState = robotState.READY;
                    PIDF.retractReset();
                }
                break;
        }
        if (currentState != robotState.RETURNING) PIDF.update();
    }
    enum robotState {
        READY,
        MOVING,
        WAITINGINPUT,
        SLIDERSMOVING,
        RETURNING,
        HARDRESET
    }

    enum GoingToWhere {
        BASKET,
        SPECIMEN,
        RESET,
    }
}