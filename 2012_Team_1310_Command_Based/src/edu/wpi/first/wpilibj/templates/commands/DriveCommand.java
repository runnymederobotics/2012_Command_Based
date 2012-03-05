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
        boolean allowAutoShift = false;
        if(oi.getLowGearButton()) {
            chassisSubsystem.transShift(false);
        } else if(oi.getHighGearButton()) {
            chassisSubsystem.transShift(true);
        } else {
            //Dont allow autoshift unless we havent pressed one of the manual shift buttons
            allowAutoShift = true;
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
        
        chassisSubsystem.drive(oi.getSpeedAxis(), oi.getRotationAxis(), allowAutoShift);
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
