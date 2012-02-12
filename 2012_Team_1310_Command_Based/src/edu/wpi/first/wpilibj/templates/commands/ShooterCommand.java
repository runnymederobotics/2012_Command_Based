package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class ShooterCommand extends CommandBase {
    double[] targetDistance = new double[1];
    boolean[] freshSequence = new boolean[1];
    
    int[] readerSequenceNumber = new int[1];
    
    public ShooterCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(shooterSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        //Manual mode
        
        if(oi.getManualShooterToggle()) {
            double manualSpeed = oi.getShooterSpeed();
            shooterSubsystem.setSetpoint(manualSpeed);
        } else {
            //shooterSubsystem.setSetpoint(0.0);
            //Do something to convert targetDistance to a motor speed

            boolean canSeeTarget = CameraSystem.getTargetDistance(readerSequenceNumber, targetDistance, freshSequence);
            
            final double minPower = DriverStation.getInstance().getAnalogIn(1) / 5.0; //0.2794
            double powerDivisor = DriverStation.getInstance().getAnalogIn(2) * 1000; //1816
            if(powerDivisor <= 0) {
                powerDivisor = 1500;
            }
            double motorSpeed = targetDistance[0] / powerDivisor + minPower;
            
            if(canSeeTarget && freshSequence[0]) {
                shooterSubsystem.setSetpoint(motorSpeed);
            }
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
