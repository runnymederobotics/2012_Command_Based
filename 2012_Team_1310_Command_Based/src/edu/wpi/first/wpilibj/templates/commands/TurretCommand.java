package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class TurretCommand extends CommandBase {    
    static double[] targetAngle = new double[1];
    static boolean[] freshSequence = new boolean[1];
    
    static int[] readerSequenceNumber = new int[1];
    
    public TurretCommand() {
        requires(turretSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        boolean canSeeTarget = CameraSystem.getTargetAngle(readerSequenceNumber, targetAngle, freshSequence);
        double manualSetpoint = turretSubsystem.SEARCH_ANGLE.get() * oi.getManualTurretDirection();
        
        if(DriverStation.getInstance().isOperatorControl() && oi.getManualTurretToggle()) {
            turretSubsystem.setRelativeAngleSetpoint(manualSetpoint);
            turretSubsystem.setCameraLight(false); //Turn off camera in manual mode
        } else {
            //turretSubsystem.enable();
            if(canSeeTarget && freshSequence[0]) {
                turretSubsystem.setRelativeAngleSetpoint(-targetAngle[0]);
            } else if(canSeeTarget && !freshSequence[0]) {
                //Do nothing
            } else if(!canSeeTarget && freshSequence[0]) {
                turretSubsystem.setRelativeAngleSetpoint(manualSetpoint);
            }
            turretSubsystem.setCameraLight(true); //Turn off camera in manual mode
        }
        turretSubsystem.execute();
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
    
    public static void print() {
        System.out.print("Turret angle: " + targetAngle[0] + " readerSequence: " + readerSequenceNumber[0] + " freshSequence: " + freshSequence[0] + " operator: " + oi.getManualTurretToggle() + "\n");
    }
            
}
