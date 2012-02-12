package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class TurretCommand extends CommandBase {  
    final double CAMERA_TOLERANCE = 0.5; //Degrees
    
    double[] targetAngle = new double[1];
    boolean[] freshSequence = new boolean[1];
    
    int[] readerSequenceNumber = new int[1];
    
    public TurretCommand() {
        requires(turretSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        if(DriverStation.getInstance().isOperatorControl() && oi.getManualTurretToggle()) {
            turretSubsystem.disablePID();
            turretSubsystem.setSpeed(oi.getTurretSpeed());
        } else {
            turretSubsystem.enablePID();
            
            boolean canSeeTarget = CameraSystem.getTargetAngle(readerSequenceNumber, targetAngle, freshSequence);
            
            if(canSeeTarget && freshSequence[0] && Math.abs(targetAngle[0]) > CAMERA_TOLERANCE) {
                turretSubsystem.setRelativeAngleSetpoint(targetAngle[0] / 2 /* / 5*/);
            } else if(canSeeTarget && !freshSequence[0]) {
                //Do nothing
            } else if(!canSeeTarget && freshSequence[0]) {
                //TODO: get EP to get their shit together and put DigitalInputs on the turret
                //turretSubsystem.searchForTarget();
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
