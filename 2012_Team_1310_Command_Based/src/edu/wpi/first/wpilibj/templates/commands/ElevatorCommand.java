package edu.wpi.first.wpilibj.templates.commands;

public class ElevatorCommand extends CommandBase {
    public ElevatorCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(elevatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    boolean releasingBall = false;
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        elevatorSubsystem.handleBallRelease(oi.getBallRelease(), shooterSubsystem.getShooterRunning());
        
        if(oi.getManualRollerToggle()) {
            elevatorSubsystem.runRoller(oi.getRollerSpeed());
        } else {
            elevatorSubsystem.runRoller(0.0); //Only run the roller positive
        }
        
        //elevatorSubsystem.runRoller(!elevatorSubsystem.hasMaxBalls());
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
