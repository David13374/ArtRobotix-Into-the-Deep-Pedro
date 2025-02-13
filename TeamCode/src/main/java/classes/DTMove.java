package classes;

import com.pedropathing.localization.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import constants.motorInit;

@Config
public class DTMove {

    public void setMotorPowers(double front_left_motor ,double back_left_motor, double front_right_motor, double back_right_motor) {
        r.leftFront.setPower(front_left_motor);
        r.leftBack.setPower(back_left_motor);
        r.rightFront.setPower(front_right_motor);
        r.rightBack.setPower(back_right_motor);
    }
    IMU imu;
    //GoBildaPinpointDriver odo;
    Gamepad currentgamepad1;
    //MecanumDrive robot;
    public boolean driverCentric=true;
    double x;
    double y;
    double turn;
    double robotDegree, gamepadDegrees, theta, sin, cos, max, power;
    public double front_left_motor, front_right_motor, back_left_motor, back_right_motor;
    double gyro_offset;
    double coef = 1;

    GoBildaPinpointDriver pinpoint;

    motorInit r;
    public DTMove(HardwareMap hardwareMap) {
        r = new motorInit(hardwareMap);
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.getPosition().getHeading(AngleUnit.RADIANS);
    }

    public double getHeading() {
        return pinpoint.getPosition().getHeading(AngleUnit.RADIANS);
        //return imu.getRobotYawPitchRollAngles().getYaw();
    }

    public void resetOdoIMU() {
        pinpoint.recalibrateIMU();
    }

    public Pose2D getPosition() {
        return pinpoint.getPosition();
    }
    public void Move(Gamepad currentGamepad1) {

        if(currentGamepad1.b) driverCentric=true;
        else if(currentGamepad1.a) driverCentric=false;

        if (currentGamepad1.right_bumper) {
            coef = 0.65;
        }
        else
            coef = 1;

        x = currentGamepad1.left_stick_x;
        y = -currentGamepad1.left_stick_y;
        turn = Math.max(-currentGamepad1.left_trigger, currentGamepad1.right_trigger)
                + Math.min(-currentGamepad1.left_trigger, currentGamepad1.right_trigger);
        if(turn<-1) turn=-1;

        robotDegree = getHeading()-gyro_offset;
        gamepadDegrees = Math.atan2(y, x);
        power = Math.hypot(x, y);
        if(power>1) power=1;

        if(driverCentric) {
            theta = gamepadDegrees-robotDegree;
        }
        else theta = gamepadDegrees;

        sin = Math.sin(theta - Math.PI/4);
        cos = Math.cos(theta - Math.PI/4);
        max = Math.max(Math.abs(sin), Math.abs(cos));


        front_left_motor = power * cos/max + turn;
        front_right_motor = power * sin/max - turn;
        back_left_motor = power * sin/max + turn;
        back_right_motor = power * cos/max - turn;

        if ((power + Math.abs(turn)) > 1 && turn > 0) {
            front_left_motor /= power + turn;
            front_right_motor /= power + turn;
            back_left_motor /= power + turn;
            back_right_motor /= power + turn;
        }

        if ((power + Math.abs(turn)) > 1 && turn < 0) {
            front_left_motor /= power - turn;
            front_right_motor /= power - turn;
            back_left_motor /= power - turn;
            back_right_motor /= power - turn;
        }
        if(currentGamepad1.y) {
            reset_gyro();

        }
        pinpoint.update();
        setMotorPowers(coef*front_left_motor, coef*back_left_motor, coef*back_right_motor, coef*front_right_motor);
    }

    public double getTheta() {
        return theta;
    }

    public double getGyro_offset(){
        return gyro_offset;
    }

    public void reset_gyro() {
        gyro_offset = getHeading();
    }

}