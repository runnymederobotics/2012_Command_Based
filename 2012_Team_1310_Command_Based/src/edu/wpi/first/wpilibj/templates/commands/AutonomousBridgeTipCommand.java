package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;

public class AutonomousBridgeTipCommand extends CommandBase {
    boolean value;
    
    public AutonomousBridgeTipCommand(boolean value) {
        // Use requires() here to declare subsystem dependencies
        requires(bridgeTipSubsystem);
        
        this.value = value;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        boolean override = false;
        bridgeTipSubsystem.set(override, value);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !DriverStation.getInstance().isAutonomous();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
