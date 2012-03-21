package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;

public class ManualBridgeTipCommand extends CommandBase {
    public ManualBridgeTipCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(bridgeTipSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if(DriverStation.getInstance().isOperatorControl()) {
            bridgeTipSubsystem.set(oi.getBridgeTipperOverride(), oi.getBridgeTipperButton());
        } else {
            boolean override = false;
            boolean request = false;
            bridgeTipSubsystem.set(override, request);
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
