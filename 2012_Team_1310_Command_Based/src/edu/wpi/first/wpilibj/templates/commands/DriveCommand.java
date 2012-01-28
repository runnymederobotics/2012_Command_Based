package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Scheduler;

public class DriveCommand extends CommandBase {

    public DriveCommand() {
        requires(chassisSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if(oi.getTransShiftButton()) {
            chassisSubsystem.transShift(true);
        } else {
            chassisSubsystem.transShift(false);
        }
        
        if(oi.getFollowTargetToggle()) {
            //Follow target takes over this command because it requires(chassisSubsystem)
            Scheduler.getInstance().add(new FollowTargetCommand());
        }
        
        if(oi.getAutoBalanceToggle()) {
            //Auto balance takes over this command because it requires(chassisSubsystem)
            Scheduler.getInstance().add(new AutoBalanceCommand());
        }
        
        if(oi.getPIDToggle()) {
            chassisSubsystem.enablePID();
        } else {
            chassisSubsystem.disablePID();
        }
        
        chassisSubsystem.setSetpoint(oi.getSpeedAxis(), oi.getRotationAxis());
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
