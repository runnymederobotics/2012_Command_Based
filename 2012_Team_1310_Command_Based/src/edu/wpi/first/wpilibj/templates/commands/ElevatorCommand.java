package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;

public class ElevatorCommand extends CommandBase {
    
    public ElevatorCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(elevatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    boolean releasingBall = false;
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        elevatorSubsystem.runRoller(oi.getEnableElevatorToggle(), oi.getBallRelease());
            
        elevatorSubsystem.handleBallRelease(oi.getBallRelease(), shooterSubsystem.getShooterRunning());
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
