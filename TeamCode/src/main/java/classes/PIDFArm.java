package classes;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class PIDFArm {
    private PIDController controllerL, controllerR;

    public static double p = 0.02, i = 0, d = 0.00006, f = 0;

    public static double target = 0;

    private final double ticks_in_degrees = 145.1 / 180.0;

    public DcMotorEx armMotorL, armMotorR;

    public static double inferiorLimit, superiorLimit;
    public PIDFArm(HardwareMap hardwareMap, double inferiorLimit1, double superiorLimit1, boolean resetEncoder) {

        controllerL = new PIDController(p, i, d);
        controllerR = new PIDController(p, i, d);

        inferiorLimit = inferiorLimit1;
        superiorLimit = superiorLimit1;

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

    public void update() {

        controllerL.setPID(p, i, d);
        controllerR.setPID(p, i, d);

        int armPosL = getArmPosL();
        int armPosR = getArmPosR();

        double pidL = controllerL.calculate(armPosL, target);
        double pidR = controllerR.calculate(armPosR, target);

        double ff = Math.cos(Math.toRadians(target / ticks_in_degrees)) * f;

        double powerL = pidL + ff;
        double powerR = pidR + ff;

        powerL = Math.max(inferiorLimit, powerL);
        powerR = Math.max(inferiorLimit, powerR);

        powerL = Math.min(superiorLimit, powerL);
        powerR = Math.min(superiorLimit, powerR);
        armMotorL.setPower(powerL);
        armMotorR.setPower(powerR);
        //armMotorR.setPower(power);

    }
}