package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ElevatorCommand;

public class ElevatorSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    final int MAX_BALLS = 3;
    final double RELEASE_DELAY = 0.5;
    final double RECOVER_TIME = 0.5; //Time for shooter to recover after a shot
    final boolean RELEASE_VALUE = true;
    
    Victor rollerMotor = new Victor(RobotMap.ROLLER_MOTOR);
    
    DigitalInput topBall = new DigitalInput(RobotMap.TOP_BALL);
    DigitalInput middleBall = new DigitalInput(RobotMap.MIDDLE_BALL);
    DigitalInput bottomBall = new DigitalInput(RobotMap.BOTTOM_BALL);
    
    Pneumatic ballRelease = new Pneumatic(new DoubleSolenoid(RobotMap.ELEVATOR_BALL_RELEASE_ONE, RobotMap.ELEVATOR_BALL_RELEASE_TWO));
    
    public ElevatorSubsystem() {
    }
    
    public void disable() {
    }
    
    public void enable() {
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ElevatorCommand());
    }
    
    public boolean getTopBall() {
        return topBall.get();
    }
    
    public int getBallCount() {
        return (topBall.get() ? 1 : 0) + (middleBall.get() ? 1 : 0) + (bottomBall.get() ? 1 : 0);
    }
    
    public boolean elevatorRunning() {
        return rollerMotor.get() != 0.0;
    }
    
    boolean releasingBall = false;
    double releaseTime = 0.0;
    double retractTime = 0.0;
    
    public void handleBallRelease(boolean shootRequest, boolean shooterRunning) {
        double now = Timer.getFPGATimestamp();
        
        //TODO: add condition for topBall being true?
        if(now - retractTime > RECOVER_TIME && shootRequest && elevatorRunning() && shooterRunning) {
            if(!releasingBall) { //If we werent previously releasing
                releaseTime = now;
                ballRelease.set(RELEASE_VALUE);
            }
            releasingBall = true;
        }
        
        if(releasingBall && now - releaseTime >= RELEASE_DELAY) {
            releasingBall = false;
            retractTime = now;
        }
        
        if(!releasingBall) {
            ballRelease.set(!RELEASE_VALUE);
        }
    }
    
    public void keepBall() {
        
    }
    
    public void runRoller(double speed) {
        rollerMotor.set(speed);
    }
    
    public void print() {
        System.out.print("(Elevator Subsystem)\n");
        
        System.out.print("topBall: " + topBall.get() + " middleBall: " + middleBall.get() + " bottomBall: " + bottomBall.get() + "\n");
        System.out.print("rollerMotor: " + rollerMotor.get() + "\n");
    }
}
