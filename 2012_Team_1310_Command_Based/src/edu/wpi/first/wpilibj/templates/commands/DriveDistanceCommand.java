package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class DriveDistanceCommand extends CommandBase {
    
    int encoderCounts;
    
    static class Creator implements RobotTemplate.CommandCreator {
        private final int counts;
        
        Creator(int counts) {
            this.counts = counts;
        }

        public Command create() {
            return new DriveDistanceCommand(counts);
        }
    }
    
    public static RobotTemplate.CommandCreator creator(int counts) {
        return new Creator(counts);
    }
    
    public DriveDistanceCommand(int encoderCounts) {        
        // Use requires() here to declare subsystem dependencies
        requires(chassisSubsystem);
        
        System.out.println("DriveDistanceCommand encoderCounts = " + encoderCounts);
        this.encoderCounts = encoderCounts;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        System.out.println("Setpoint is " + encoderCounts);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        chassisSubsystem.setCountSetpoint(encoderCounts);
        chassisSubsystem.goToCountSetpoint();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return chassisSubsystem.reachedCountSetpoint();
    }

    // Called once after isFinished returns true
    protected void end() {
        System.out.println("Reached setpoint");
        chassisSubsystem.disablePIDCount();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        chassisSubsystem.disablePIDCount();
    }
}
