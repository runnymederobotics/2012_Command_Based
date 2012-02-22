package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class ShooterCommand extends CommandBase {
    double[] targetDistance = new double[1];
    boolean[] freshSequence = new boolean[1];
    
    int[] readerSequenceNumber = new int[1];
    
    public ShooterCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(shooterSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        //Manual mode
        if(DriverStation.getInstance().isOperatorControl() && oi.getManualShooterToggle()) {
            double manualSpeed = oi.getManualShooterSpeed();
            shooterSubsystem.setSetpoint(manualSpeed);
        } else {
            boolean canSeeTarget = CameraSystem.getTargetDistance(readerSequenceNumber, targetDistance, freshSequence);
            
            if(canSeeTarget && freshSequence[0]) {
                shooterSubsystem.setSetpoint(shooterSubsystem.getPowerFromDistance(targetDistance[0]));
            }
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
