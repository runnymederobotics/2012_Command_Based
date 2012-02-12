package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.TurretCommand;

public class TurretSubsystem extends PIDSubsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    static final double PID_P = 0.005, PID_I = 0.0, PID_D = 0.0;
    final double COUNTS_PER_DEGREE = 13; //We turned the turret 90 degrees and divided the encoder counts by 90
    final double MAX_SPEED = 1.0;
    final double SEARCH_ANGULAR_VELOCITY = 1.0; //Degrees
    
    Victor turretMotor = new Victor(RobotMap.TURRET_MOTOR);
    
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
        turretMotor.set(speed);
        //TODO: add digital inputs for limit switches
    }
    
    public void searchForTarget() {
        //TODO: search for target
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
