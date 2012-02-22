package edu.wpi.first.wpilibj.templates.commands;

public class BridgeTipDownCommand extends CommandBase {
    public BridgeTipDownCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(bridgeTipSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        bridgeTipSubsystem.set(true);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return bridgeTipSubsystem.get();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
