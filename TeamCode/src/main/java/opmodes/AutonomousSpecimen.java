package opmodes;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import classes.Intake;
import classes.Outtake;
import classes.PIDFArm;
import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous
@Config
public class AutonomousSpecimen extends OpMode {

    public interface Runnable {

    }
    private PIDFArm PIDF;
    private Intake intake;
    private Outtake outtake;
    private static PathBuilder builder;
    private static PathChain line1, line2, line3, line4, line5, line6;

    private static Follower follower;
    public static double path5Time = 1;

    public static int pathState = 0;
    public static final Pose startPose = new Pose(7.5, 72, Math.toRadians(180));
    Timer pathTimer;

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(line1);
                setPathState(1);
                break;

            case 1:
                if (!follower.isBusy()) {
                    follower.followPath(line2, true);
                    setPathState(2);
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(line3, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(line4, true);
                    setPathState(4);
                }
                break;
            case 4:
                if (!follower.isBusy()) {
                    outtake.setWallPos();
                    follower.followPath(line5, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (pathTimer.getElapsedTimeSeconds() >= path5Time) {
                    outtake.closeOuttakeClaw();
                    follower.followPath(line6, true);
                    setPathState(6);
                }
                break;
            case 6:
                if (!follower.isBusy()) {
                    follower.followPath(line5, true);
                    setPathState(7);
                }
                break;
            case 7:
                if (pathTimer.getElapsedTimeSeconds() >= path5Time) {
                    follower.followPath(line6, true);
                    setPathState(8);
                }
                break;
            case 8:
                if (!follower.isBusy()) {
                    follower.followPath(line5, true);
                    setPathState(9);
                }
                break;
            case 9:
                if (pathTimer.getElapsedTimeSeconds() >= path5Time) {
                    follower.followPath(line6, true);
                    setPathState(-1);
                }
                break;
        }
    }

    public void buildPaths() {
        line1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(0.897, 72.000, Point.CARTESIAN),
                                new Point(30, 72.000, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();
        line2 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(30.000, 72.000, Point.CARTESIAN),
                                new Point(28.229, 9.914, Point.CARTESIAN),
                                new Point(40.495, 56.961, Point.CARTESIAN),
                                new Point(62.131, 22.430, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0))
                .addPath(
                        new BezierLine(
                                new Point(62.131, 22.430, Point.CARTESIAN),
                                new Point(14, 22.430, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
        line3 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(14, 22.430, Point.CARTESIAN),
                                new Point(50.744, 39.487, Point.CARTESIAN),
                                new Point(62.674, 17.139, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .addPath(
                        new BezierLine(
                                new Point(62.674, 17.139, Point.CARTESIAN),
                                new Point(14, 17.139, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
        line4 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(14, 17.139, Point.CARTESIAN),
                                new Point(47.720, 32.429, Point.CARTESIAN),
                                new Point(63.851, 10.582, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .addPath(
                        new BezierLine(
                                new Point(63.851, 10.582, Point.CARTESIAN),
                                new Point(14, 10.582, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
        line5 = follower.pathBuilder()
                .addPath(new BezierLine(
                                new Point(14, 10.582, Point.CARTESIAN),
                                new Point(15, 35, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
        line6 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(15, 35, Point.CARTESIAN),
                                new Point(30, 67, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180))
                .build();
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        pathTimer = new Timer();
        intake = new Intake(hardwareMap);
        outtake = new Outtake(hardwareMap);
        PIDF = new PIDFArm(hardwareMap, true);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(startPose);
        setPathState(0);
        buildPaths();
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }
}
