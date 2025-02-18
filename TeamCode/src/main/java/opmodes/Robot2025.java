package opmodes;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.concurrent.TimeUnit;

import classes.DTMove;
import classes.FSM;
import classes.HzMonitor;

@TeleOp(name = "Robot2025")
@Config
public class Robot2025 extends LinearOpMode {

    DTMove dtMove;

    Gamepad previousGamepad1;
    GamepadEx GamepadEx2;
    GamepadEx GamepadEx1;

    HzMonitor hz;
    FSM fsm;

    @Override
    public void runOpMode() {
        hz = new HzMonitor();

        fsm = new FSM(hardwareMap);
        dtMove = new DTMove(hardwareMap);

        previousGamepad1 = new Gamepad();

        GamepadEx2 = new GamepadEx(gamepad2);
        GamepadEx1 = new GamepadEx(gamepad1);

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        dtMove.resetOdoIMU();

        /*while(opModeInInit()) {
            if(gamepad2.)
        }*/

        waitForStart();
        while(opModeIsActive()) {
            double fps = hz.update();

            dtMove.Move(gamepad1);
            fsm.update(GamepadEx2, GamepadEx1);
            /*telemetry.addData("gamepad", gamepad2);
            telemetry.addData("heading", dtMove.getHeading());
            telemetry.addData("theta", dtMove.getTheta());
            telemetry.addData("position", dtMove.getPosition());
            telemetry.addData("Current mode", dtMove.driverCentric);
            telemetry.addData("gyro_offset", dtMove.getGyro_offset());
            telemetry.addData("Current State", fsm.returnState());
            telemetry.addData("Extended", fsm.isExtended());
            telemetry.addData("Claw", fsm.ClawState());
            telemetry.addData("Extendo Pos", fsm.ExtendoValue());
            telemetry.addData("timer", fsm.getTimer());
            telemetry.addLine();*/
            //telemetry.addData("slides pos R", fsm.PIDF.getArmPosR());
            int bruj = fsm.PIDF.bruh ? 1 : 0;
            telemetry.addData("slides pos L", fsm.PIDF.getArmPosL());
            telemetry.addData("target pos", fsm.PIDF.getTarget());
            telemetry.addData("slidesPower", fsm.PIDF.getPower());
            telemetry.addData("currentSlidePos", fsm.PIDF.getArmPosL());
            telemetry.addData("currentSlideVel", fsm.PIDF.getVelocity());
            telemetry.addData("Current State", fsm.returnState());
            telemetry.addData("retracted", fsm.PIDF.isRetracted()?1:0);
            telemetry.addData("resetCounter", fsm.PIDF.slideResetCounter);
            telemetry.addData("resetTimer", fsm.PIDF.t1.time(TimeUnit.MILLISECONDS));
            telemetry.addData("bruh", bruj);
            telemetry.addData("iHaveReset", fsm.PIDF.iHaveReset ? 1:0);
            telemetry.addData("timerStarted", fsm.PIDF.timerStarted);
            telemetry.addData("fps", fps);
            telemetry.update();

            GamepadEx2.readButtons();
            GamepadEx1.readButtons();
        }
    }
}