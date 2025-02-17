package classes;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.TimeUnit;

@Config
public class PIDFArm {
    private PIDController controller;

    public static double p = 0.04, i = 0, d = 0.000385;

    public static double target = 0;

    private final double ticks_in_degrees = 145.1 / 180.0;

    public DcMotorEx armMotorL, armMotorR;

    public static double ff = 0.12, retractedTolerance = 2;
    double power;

    public static double inferiorLimit = -0.73, superiorLimit=1;
    public PIDFArm(HardwareMap hardwareMap, boolean resetEncoder) {

        controller = new PIDController(p, i, d);
        //controllerR = new PIDController(p, i, d);


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

        double pid = controller.calculate(armPosL, target);

        power = pid + appliedFF;

        power = Math.max(inferiorLimit, power);
        power = Math.min(superiorLimit, power);
        armMotorL.setPower(power);
        armMotorR.setPower(power);
        //armMotorR.setPower(power);

    }

    public static boolean isRetracted = false;
    public ElapsedTime time = new ElapsedTime();
    public boolean timerStarted = false;

    public static double retractedVel = -50, retractTime = 0.2;
    public void retract() {
        if(!timerStarted) {
            timerStarted=true;
            time.reset();
            time.startTime();
        }
        controller.setPID(0, 0, 0);
        target = 0;
        armMotorL.setPower(inferiorLimit);
        armMotorR.setPower(inferiorLimit);
        if(armMotorL.getVelocity() > retractedVel && time.time(TimeUnit.SECONDS)>retractTime) {
            resetSliders();
            armMotorL.setPower(0);
            armMotorR.setPower(0);
            timerStarted=false;
            isRetracted = true;
        }
    }

    public boolean isRetracted() {
        return isRetracted;
    }
}