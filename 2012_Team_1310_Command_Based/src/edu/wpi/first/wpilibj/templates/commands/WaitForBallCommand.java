package edu.wpi.first.wpilibj.templates.commands;

public class WaitForBallCommand extends CommandBase {
    
    public WaitForBallCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(elevatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        //Continuously run the elevator
        elevatorSubsystem.runElevator(false, false, false);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return elevatorSubsystem.hasBalls();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
