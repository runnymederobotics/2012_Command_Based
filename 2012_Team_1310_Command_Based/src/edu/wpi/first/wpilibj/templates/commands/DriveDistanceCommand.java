package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;
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
        chassisSubsystem.reset();
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
        //double inclineAngle = Math.abs(chassisSubsystem.getYZAngle());
        //If our gyro says we are on an incline then we need to stop
        //TODO: Make this logic work
        //boolean onBridge = inclineAngle > AutoBalanceCommand.HORIZONTAL_TOLERANCE && inclineAngle < AutoBalanceCommand.INCLINE;
        //End the command when we've reached our setpoint or if we are in teleop mode
        return chassisSubsystem.reachedCountSetpoint() || DriverStation.getInstance().isOperatorControl();// || onBridge;
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
