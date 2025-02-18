package classes;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.util.concurrent.TimeUnit;

@Config
public class PIDFArm {
    private PIDController controller;

    public static double p = 0.025, i = 0, d = 0.00018;

    public static double target = 0;

    private final double ticks_in_degrees = 145.1 / 180.0;

    public DcMotorEx armMotorL, armMotorR;

    public static double ff = 0.12, retractedTolerance = 2;
    double power;
    public ElapsedTime t1;
    public static double inferiorLimit = -0.73, superiorLimit=1;
    public PIDFArm(HardwareMap hardwareMap, boolean resetEncoder) {

        controller = new PIDController(p, i, d);
        //controllerR = new PIDController(p, i, d);
        t1= new ElapsedTime();

        armMotorL = hardwareMap.get(DcMotorEx.class, "armMotorL");
        armMotorR = hardwareMap.get(DcMotorEx.class, "armMotorR");

        if(resetEncoder) {
            resetSliders();
        }

        armMotorR.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void resetSliders() {
        armMotorL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotorR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armMotorR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armMotorL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setTarget(double newTarget) {
        target = newTarget;
    }

    public double getTarget() {return target;}

    public int getArmPosL() { return armMotorL.getCurrentPosition(); }

    public int getArmPosR() { return armMotorR.getCurrentPosition(); }
    public double getPower() {return power;}
    public double getVelocity(){return armMotorL.getVelocity();}

    public void update() {
        bruh= false;
        isRetracted = false;
        controller.setPID(p, i, d);
        double appliedFF;

        if(target >= 100) {
            controller.setTolerance(0);
            appliedFF = ff;
        }
        else {
            controller.setTolerance(retractedTolerance);
            appliedFF = 0;
        }
        int armPosL = getArmPosL();
        double pid;
        if(iHaveReset) pid = controller.calculate(armPosL-7, target);
        else pid = controller.calculate(armPosL, target);

        power = pid + appliedFF;

        power = Math.max(inferiorLimit, power);
        power = Math.min(superiorLimit, power);
        armMotorL.setPower(power);
        armMotorR.setPower(power);
        //armMotorR.setPower(power);

    }

    public boolean isRetracted = false;
    public ElapsedTime time = new ElapsedTime();
    public boolean timerStarted = false;

    public static double resetPosition=20, slideResetWait = 200;

    public int slideResetCounter = 0;

    //public boolean isResetForRetraction = false;
    public void retractReset() {
        isRetracted = false;
        timerStarted=false;
    }

    public boolean bruh;
    public boolean iHaveReset = false;
    public void retract() {
        controller.setPID(0, 0, 0);
        target = 0;
        bruh = armMotorL.getVelocity()<0.000000001 && armMotorL.getCurrentPosition()<resetPosition;
        if(!timerStarted) {
            armMotorL.setPower(inferiorLimit);
            armMotorR.setPower(inferiorLimit);
        }
        if(bruh) {
            armMotorL.setPower(-0.1);
            armMotorR.setPower(-0.1);
            if(!timerStarted) {
                t1.reset();
                timerStarted=true;
            }
        }

        if(timerStarted && t1.time(TimeUnit.MILLISECONDS)>slideResetWait) {
            resetSliders();
            iHaveReset = true;
            slideResetCounter++;
            timerStarted=false;
            isRetracted = true;
        }
    }

    public double getPowerDraw() {
        return armMotorL.getCurrent(CurrentUnit.AMPS)+armMotorR.getCurrent(CurrentUnit.AMPS);
    }
    public boolean isRetracted() {
        return isRetracted;
    }
}