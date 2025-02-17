package opmodes;

import com.pedropathing.pathgen.PathBuilder;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import pedroPathing.constants.FConstants;
import pedroPathing.constants.LConstants;
@Autonomous
public class AutonomousSpecimen extends OpMode {

    public static PathBuilder builder;
    public static PathChain line1, line2, line3;

    public static Follower follower;

    public static int pathState = 0;
    Timer pathTimer;

    private final Pose startPose = new Pose(7.5, 72, Math.toRadians(180));

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Move from start to scoring position
                follower.followPath(line1);
                setPathState(1);
                break;

            case 1: // Wait until the robot is near the scoring position
                if (!follower.isBusy()) {
                    follower.followPath(line2, true);
                    setPathState(2);
                }
                break;

            case 2: // Wait until the robot is near the first sample pickup position
                if (!follower.isBusy()) {
                    follower.followPath(line3, true);
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
                                new Point(39.701, 72.000, Point.CARTESIAN)
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
                .build();
        line3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Point(62.131, 22.430, Point.CARTESIAN),
                                new Point(8.299, 22.430, Point.CARTESIAN)
                        )
                )
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
