package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;

public class AutoBalanceCommand extends CommandBase {
    class AutoBalanceState {
        static final int HORIZONTAL = 0;
        static final int INCLINE_BACKWARDS = 1;
        static final int INCLINE_FORWARDS = 2;
    }
    
    static final double HORIZONTAL_TOLERANCE = 13.0;
    static final double INCLINE = 30.0;
    static final double DRIVE_SPEED = 0.25; //Speed to drive while auto balancing

    static final double CORRECTION_TIME = 0.5; //Correct so we dont go past horizontal
    
    int curState;
    double speed = 0.0, stateChangeTime = 0.0;
    
    public AutoBalanceCommand() {
        // Use requires() here to declare subsystem dependencies
        requires(chassisSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        curState = AutoBalanceState.HORIZONTAL;
        
        chassisSubsystem.enablePID();
    }

    // Called repeatedly when this Command is scheduled to run
    //Auto balance state machine
    protected void execute() {
        if(!oi.getAutoBalanceToggle()) {
            //Driving takes over this command because it requires(chassisSubsystem)
            Scheduler.getInstance().add(new DriveCommand());
        }
        
        double gyroAngle = chassisSubsystem.getYZAngle();
        
        switch(curState) {
        //Horizontal
        case AutoBalanceState.HORIZONTAL: {
                double now = Timer.getFPGATimestamp();
                if(now - stateChangeTime > CORRECTION_TIME) { //Correction time for "kickback"
                    speed = 0.0;
                    if(gyroAngle > HORIZONTAL_TOLERANCE && gyroAngle < INCLINE)
                        curState = AutoBalanceState.INCLINE_FORWARDS;
                    else if(gyroAngle < -HORIZONTAL_TOLERANCE && gyroAngle > -INCLINE)
                        curState = AutoBalanceState.INCLINE_BACKWARDS;
                }
            }
            break;
        //We are on the bridge forwards
        case AutoBalanceState.INCLINE_FORWARDS: {
                if(gyroAngle < HORIZONTAL_TOLERANCE && gyroAngle > -HORIZONTAL_TOLERANCE) {
                    stateChangeTime = Timer.getFPGATimestamp(); //The time we changed
                    speed *= -1.5; //Reverse speed for "kickback"
                    curState = AutoBalanceState.HORIZONTAL;
                    break;
                }

                //Drive towards the fulcrum (forwards)
                speed = DRIVE_SPEED;
            }
            break;
        //We are on the bridge backwards
        case AutoBalanceState.INCLINE_BACKWARDS: {
                if(gyroAngle < HORIZONTAL_TOLERANCE && gyroAngle > -HORIZONTAL_TOLERANCE) {
                    stateChangeTime = Timer.getFPGATimestamp(); //The time we changed
                    speed *= -1.5; //Reverse speed for "kickback"
                    curState = AutoBalanceState.HORIZONTAL;
                    break;
                }

                //Drive towards the fulcrum (backwards)
                speed = -DRIVE_SPEED;
            }
            break;
        default:
            curState = AutoBalanceState.HORIZONTAL;
        }
 
        chassisSubsystem.drive(speed, 0.0, false); //Dont allow high speed
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
