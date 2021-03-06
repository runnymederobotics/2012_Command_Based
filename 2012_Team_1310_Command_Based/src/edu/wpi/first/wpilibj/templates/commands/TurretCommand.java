package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class TurretCommand extends CommandBase {
    final double TURRET_SPEED = 0.7;
    final double CAMERA_TOLERANCE = 0.5; //Degrees
    
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
        if(DriverStation.getInstance().isOperatorControl() && oi.getManualTurretToggle()) {
            //turretSubsystem.disablePID();
            //turretSubsystem.setSpeed(TURRET_SPEED * oi.getManualTurretDirection());
            turretSubsystem.setRelativeAngleSetpoint(5 * oi.getManualTurretDirection());
        } else {
            //turretSubsystem.enablePID();
            
            boolean canSeeTarget = CameraSystem.getTargetAngle(readerSequenceNumber, targetAngle, freshSequence);
            
            if(canSeeTarget && freshSequence[0]) {
                turretSubsystem.setRelativeAngleSetpoint(targetAngle[0]);
            } else if(canSeeTarget && !freshSequence[0]) {
                //Do nothing
            } else if(!canSeeTarget && freshSequence[0]) {
                //TODO: get EP to get their shit together and put DigitalInputs on the turret
                //turretSubsystem.searchForTarget(oi.getManualTurretDirection());
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
    
    public static void print() {
        System.out.print("Turret angle: " + targetAngle[0] + " readerSequence: " + readerSequenceNumber[0] + " freshSequence: " + freshSequence[0] + " operator: " + oi.getManualTurretToggle() + "\n");
    }
}
