package edu.wpi.first.wpilibj.templates.commands;

public class RotateCommand extends CommandBase {
    
    double angle;
    
    public RotateCommand(double angle) {
        // Use requires() here to declare subsystem dependencies
        requires(chassisSubsystem);
        
        this.angle = angle;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        chassisSubsystem.setAngleSetpoint(angle);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        chassisSubsystem.goToAngleSetpoint();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return chassisSubsystem.reachedAngleSetpoint();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
