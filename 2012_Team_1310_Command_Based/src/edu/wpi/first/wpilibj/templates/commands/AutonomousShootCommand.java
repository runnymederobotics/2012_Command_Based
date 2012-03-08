package edu.wpi.first.wpilibj.templates.commands;

public class AutonomousShootCommand extends CommandBase {
    
    public AutonomousShootCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(elevatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        boolean disableRequest = false;
        boolean shootRequest = true;
        boolean reverseRequest = false;
        
        elevatorSubsystem.runRoller(disableRequest, shootRequest, reverseRequest);
        elevatorSubsystem.handleBallRelease(shootRequest, shooterSubsystem.getShooterRunning());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !elevatorSubsystem.hasBalls(); //Finish when we no longer have balls
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
