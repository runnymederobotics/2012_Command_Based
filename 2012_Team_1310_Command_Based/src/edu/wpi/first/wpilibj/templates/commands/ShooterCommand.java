package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class ShooterCommand extends CommandBase {
    final double IDLE_SPEED = 0.0;
    final double SHOOTER_LAST_BALL_DELAY = 0.75; //Time to keep shooter running after we shoot our last ball
    
    double[] targetDistance = new double[1];
    boolean[] freshSequence = new boolean[1];
    
    int[] readerSequenceNumber = new int[1];
    
    double timeOfLastBall = 0.0;
    
    public ShooterCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(shooterSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        double now = Timer.getFPGATimestamp();
        if(elevatorSubsystem.hasBalls() || now - timeOfLastBall < SHOOTER_LAST_BALL_DELAY) {
            if(elevatorSubsystem.hasBalls()) {
                timeOfLastBall = now;
            }
            
            if(DriverStation.getInstance().isOperatorControl() && oi.getManualShooterToggle()) {
                double manualSpeed = oi.getManualShooterSpeed();
                shooterSubsystem.setSetpoint(manualSpeed * shooterSubsystem.MAX_SHOOTER_ENCODER_RATE.get());
            } else {
                boolean canSeeTarget = CameraSystem.getTargetDistance(readerSequenceNumber, targetDistance, freshSequence);

                if(canSeeTarget && freshSequence[0]) {
                    shooterSubsystem.setSetpoint(shooterSubsystem.getSetpointFromDistance(targetDistance[0]));
                }
            }
        } else {
            shooterSubsystem.setSetpoint(IDLE_SPEED * shooterSubsystem.MAX_SHOOTER_ENCODER_RATE.get());
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
