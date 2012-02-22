package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ElevatorCommand;

public class ElevatorSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    final int MAX_BALLS = 3;
    final double ELEVATOR_SPEED = 1.0;
    final double RELEASE_DELAY = 0.35;
    final double RECOVER_TIME = 0.75; //Time for shooter to recover after a shot
    final boolean RELEASE_VALUE = true;
    
    Jaguar elevatorMotor = new Jaguar(RobotMap.ELEVATOR_MOTOR);
    
    DigitalInput topBall = new DigitalInput(RobotMap.TOP_BALL);
    DigitalInput middleBall = new DigitalInput(RobotMap.MIDDLE_BALL);
    DigitalInput bottomBall = new DigitalInput(RobotMap.BOTTOM_BALL);
    
    Pneumatic ballRelease = new Pneumatic(new DoubleSolenoid(RobotMap.ELEVATOR_BALL_RELEASE_ONE, RobotMap.ELEVATOR_BALL_RELEASE_TWO));
    
    public ElevatorSubsystem() {
    }
    
    public void disable() {
        elevatorMotor.set(0.0);
    }
    
    public void enable() {
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ElevatorCommand());
    }
    
    public boolean hasMaxBalls() {
        return false;
        //return topBall.get() && middleBall.get() && bottomBall.get();
    }
    
    public boolean hasBalls() {
        return topBall.get() || middleBall.get() || bottomBall.get();
    }
    
    public boolean elevatorRunning() {
        return elevatorMotor.get() != 0.0;
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
    
    public void runRoller(boolean shootRequest) {
        if(hasMaxBalls() && !shootRequest) {
            elevatorMotor.set(0.0);
        } else {
            elevatorMotor.set(ELEVATOR_SPEED);
        }
    }
    
    public void print() {
        System.out.print("(Elevator Subsystem)\n");
        
        System.out.print("topBall: " + topBall.get() + " middleBall: " + middleBall.get() + " bottomBall: " + bottomBall.get() + "\n");
        System.out.print("elevatorMotor: " + elevatorMotor.get() + "\n");
    }
}
