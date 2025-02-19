package opmodes;

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

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;

@Autonomous
public class AutonomousSpecimen extends OpMode {

    public static PathBuilder builder;
    public static PathChain line1, line2, line3, line4;

    public static Follower follower;

    public static int pathState = 0;
    private final Pose startPose = new Pose(7.5, 72, Math.toRadians(180));
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
                                new Point(39.701, 72.000, Point.CARTESIAN),
                                new Point(2.019, 40.598, Point.CARTESIAN),
                                new Point(60.112, 41.720, Point.CARTESIAN),
                                new Point(62.131, 22.430, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0))
                .addPath(
                        new BezierLine(
                                new Point(62.131, 22.430, Point.CARTESIAN),
                                new Point(10, 22.430, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
        line3 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(10.000, 22.430, Point.CARTESIAN),
                                new Point(50.744, 39.487, Point.CARTESIAN),
                                new Point(62.674, 17.139, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .addPath(
                        new BezierLine(
                                new Point(62.674, 17.139, Point.CARTESIAN),
                                new Point(10, 17.139, Point.CARTESIAN)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();
        line4 = follower.pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Point(10.000, 17.139, Point.CARTESIAN),
                                new Point(47.720, 32.429, Point.CARTESIAN),
                                new Point(63.851, 9.082, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .addPath(
                        new BezierLine(
                                new Point(63.851, 9.082, Point.CARTESIAN),
                                new Point(10, 9.082, Point.CARTESIAN)
                        )
                )
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .addPath(new BezierLine(
                        new Point(10, 9.082, Point.CARTESIAN),
                        new Point(15, 35, Point.CARTESIAN)
                ))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        pathTimer = new Timer();

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
