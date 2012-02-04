package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.CollectBallCommand;

public class ElevatorSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    final int MAX_BALLS = 3;
    final double ROLLER_SPEED = 0.5;
    
    Victor rollerMotor = new Victor(RobotMap.ROLLER_MOTOR);
    
    DigitalInput ballEntered = new DigitalInput(RobotMap.BALL_ENTER_SWITCH);
    DigitalInput ballExited = new DigitalInput(RobotMap.BALL_EXIT_SWITCH);
    
    boolean lastBallEntered = false;
    boolean lastBallExited = false;
    
    int numBalls = 0;

    public void initDefaultCommand() {        
        // Set the default command for a subsystem here.
        setDefaultCommand(new CollectBallCommand());
    }
    
    public void updateBallStatus() {
        if(!lastBallEntered && ballEntered.get()) {
            ++numBalls;
        }
        if(!lastBallExited && ballExited.get()) {
            --numBalls;
        }
        
        lastBallEntered = ballEntered.get();
        lastBallExited = ballExited.get();
        
        //Limit # of balls to 0
        if(numBalls < 0) {
            numBalls = 0;
        }
    }
    
    public boolean hasMaxBalls() {
        return numBalls >= MAX_BALLS;
    }
    
    public void runRoller(boolean run) {
        rollerMotor.set(run ? ROLLER_SPEED : 0.0);
    }
    
    public void print() {
        System.out.println("(Elevator Subsystem)");
        
        System.out.println("ballEntered: " + ballEntered.get() + " ballExited: " + ballExited.get() + " numBalls: " + numBalls);
        System.out.println("rollerMotor: " + rollerMotor.get());
    }
}
