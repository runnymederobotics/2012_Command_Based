package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.TurretCommand;

public class TurretSubsystem extends PIDSubsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    static final double PID_P = 0.0025, PID_I = 0.0, PID_D = 0.0;
    final double COUNTS_PER_DEGREE = 13; //We turned the turret 90 degrees and divided the encoder counts by 90
    final double MAX_SPEED = 1.0;
    final double SEARCH_ANGULAR_VELOCITY = 1.0; //Degrees
    
    Jaguar turretMotor = new Jaguar(RobotMap.TURRET_MOTOR);
    
    Encoder encTurret = new Encoder(RobotMap.ENC_TURRET_A, RobotMap.ENC_TURRET_B);
    
    DigitalInput leftLimit = new DigitalInput(RobotMap.TURRET_LEFT_LIMIT);
    DigitalInput rightLimit = new DigitalInput(RobotMap.TURRET_RIGHT_LIMIT);
    
    boolean usePID = true;
    
    int searchDirection = 1;
    
    public TurretSubsystem() {
        super("PIDTurret", PID_P, PID_I, PID_D);
        
        encTurret.start();
        
        setSetpointRange(Short.MIN_VALUE, Short.MAX_VALUE);
    }
    
    public void reset() {
        encTurret.reset();
    }
    
    public void disable() {
        reset();
        
        super.disable();
    }
    
    public void enable() {
        reset();
        
        super.enable();
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new TurretCommand());
    }
    
    public void enablePID() {
        usePID = true;
    }
    
    public void disablePID() {
        usePID = false;
    }
    
    public void setRelativeAngleSetpoint(double angle) {
        setSetpointRelative(angle * COUNTS_PER_DEGREE);
    }
    
    public void setSpeed(double speed) {
        if(Math.abs(speed - SEARCH_RIGHT) < Math.abs(speed - SEARCH_LEFT)) {
            //Difference from searching right is less than difference from searching left
            //This means we are moving right
            if(rightLimit.get()) {
                speed = 0;
            }
        } else {
            //We are moving left
            if(leftLimit.get()) {
                speed = 0;
            }
        }
        
        turretMotor.set(speed);
    }
    
    public static final int SEARCH_RIGHT = 1;
    public static final int SEARCH_LEFT = -1;
    
    public void searchForTarget(int requestedDirection) {
        boolean requestingDirection = requestedDirection != 0;
        if(requestingDirection) {
            searchDirection = requestedDirection;
            if(searchDirection == SEARCH_LEFT && leftLimit.get()) {
                searchDirection = 0;
            } else if(searchDirection == SEARCH_RIGHT && rightLimit.get()) {
                searchDirection = 0;
            }
        } else {
            if(leftLimit.get()) {
                searchDirection = SEARCH_RIGHT;
            } else if(rightLimit.get()) {
                searchDirection = SEARCH_LEFT;
            }
        }
        
        if(usePID) {
            setRelativeAngleSetpoint(searchDirection);
        }
    }

    protected double returnPIDInput() {
        return encTurret.get();
    }

    protected void usePIDOutput(double output) {
        if(usePID) {
            setSpeed(output);
        }
    }
    
    public void print() {
        System.out.print("(Turret Subsystem)\n");
        
        System.out.print("PIDTurret output: " + turretMotor.get() + " PIDTurret setpoint: " + getSetpoint() + " encTurret: " + encTurret.get() + "\n");
    }
}
