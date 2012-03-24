package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.templates.CameraSystem;

public class AutonomousShootCommand extends CommandBase {
    double[] targetAngle = new double[1];
    boolean[] freshSequence = new boolean[1];
    
    int[] readerSequenceNumber = new int[1];
    
    final double CAMERA_TOLERANCE = 1.0;
    
    double lastShotTime = 0;
    double lastBallTime = 0;
    
    boolean neverStop = true;
    boolean trackTarget = true;
    
    public AutonomousShootCommand(boolean neverStop, boolean trackTarget) {
        // Use requires() here to declare subsystem dependencies
        requires(elevatorSubsystem);
        
        this.neverStop = neverStop;
        this.trackTarget = trackTarget;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        System.out.println("Starting autonomousShootCommand");
        lastShotTime = Timer.getFPGATimestamp();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        double now = Timer.getFPGATimestamp();
        
        lastBallTime = elevatorSubsystem.hasBalls() ? now : lastBallTime;
        
        CameraSystem.getTargetAngle(readerSequenceNumber, targetAngle, freshSequence);
        
        boolean shootRequest = true;
        if(trackTarget) {
            shootRequest = targetAngle[0] < CAMERA_TOLERANCE + turretSubsystem.CAMERA_ERROR.get() && targetAngle[0] > turretSubsystem.CAMERA_ERROR.get() - CAMERA_TOLERANCE;
        }
        boolean disableRequest = false;
        boolean forceShot = false;
        boolean reverseRequest = false;
        boolean shooterRunning = false;
        boolean shooterOnTarget = false;
        boolean turretOnTarget = false;
        
        if(now - lastShotTime > elevatorSubsystem.AUTONOMOUS_SHOOT_DELAY.get()) {
            shooterRunning = shooterSubsystem.getShooterRunning();
            shooterOnTarget = shooterSubsystem.onTarget();
            if(trackTarget) {
                turretOnTarget = turretSubsystem.onTarget();
            } else {
                turretOnTarget = true;
            }
            if(shooterRunning && turretOnTarget) {
                lastShotTime = now;
                forceShot = true;
                System.out.println("ReadyToShoot, restarting lastShootTime");
            }
        }

        elevatorSubsystem.runElevator(disableRequest, shootRequest, reverseRequest);
        elevatorSubsystem.handleBallRelease(shootRequest, forceShot, shooterRunning, shooterOnTarget, turretOnTarget);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        if(neverStop) {
            return false;
        }
        
        double now = Timer.getFPGATimestamp();
        return now - lastBallTime > elevatorSubsystem.AUTONOMOUS_SHOOT_TIMEOUT.get(); //Finish when we no longer have balls
    }

    // Called once after isFinished returns true
    protected void end() {
        System.out.println("AutonomousShootCommand ended");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        System.out.println("AutonomousShootCommand ended");
    }
}
