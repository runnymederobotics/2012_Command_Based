package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class FollowTargetCommand extends CommandBase {
    final double CAMERA_TOLERANCE = 1; //Degrees
    final double GYRO_TOLERANCE = 5;
    final double CAMERA_SEARCH_SPEED = 10;
    final double CAMERA_DIRECTION_THRESHOLD = 0.9;
    
    double[] targetAngle = new double[1];
    boolean[] freshSequence = new boolean[1];
    
    int[] readerSequenceNumber = new int[1];
    
    double searchDirection = 1.0;
    
    public FollowTargetCommand() {
        requires(chassisSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        chassisSubsystem.enablePIDGyro();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        /*double servoAngle = cameraSubsystem.getServoAngle();
        
        boolean canSeeTarget = cameraSubsystem.getTargetAngle(targetAngle, freshSequence);
        if(canSeeTarget && freshSequence[0] && Math.abs(targetAngle[0]) > CAMERA_TOLERANCE) {
            cameraSubsystem.setServoAngle(servoAngle + targetAngle[0] / 5);
        } else if(canSeeTarget && !freshSequence[0]) {
            //Do nothing
        } else if(!canSeeTarget && freshSequence[0]) {
            if(Math.abs(cameraSubsystem.getNormalizedServoAngle()) > CAMERA_DIRECTION_THRESHOLD)
                searchDirection *= -1;
            cameraSubsystem.setServoAngle(servoAngle + searchDirection * CAMERA_SEARCH_SPEED);
        }*/
        
        if(!oi.getFollowTargetToggle()) {
            //Driving takes over this command because it requires(chassisSubsystem)
            Scheduler.getInstance().add(new DriveCommand());
        }
        
        double gyroAngle = chassisSubsystem.getXYAngle();
        
        boolean canSeeTarget = CameraSystem.getTargetAngle(readerSequenceNumber, targetAngle, freshSequence);
        if(canSeeTarget && freshSequence[0] && Math.abs(targetAngle[0]) > GYRO_TOLERANCE) {
            chassisSubsystem.setAngleSetpoint(gyroAngle + targetAngle[0] / 5);
        }
        
        chassisSubsystem.goToAngleSetpoint();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
        chassisSubsystem.disablePIDGyro();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        chassisSubsystem.disablePIDGyro();
    }
}
