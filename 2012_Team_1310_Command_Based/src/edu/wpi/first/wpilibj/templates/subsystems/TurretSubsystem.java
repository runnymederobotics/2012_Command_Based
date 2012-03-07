package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.OutputStorage;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.TurretCommand;

public class TurretSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    static final double PID_P = 0.05, PID_I = 0.000, PID_D = 0.0;
    final double COUNTS_PER_DEGREE = 9.18;
    final double TOLERANCE = COUNTS_PER_DEGREE * 2;
    public static final int SEARCH_RIGHT = 1;
    public static final int SEARCH_LEFT = -1;
    
    public static final double TURRET_SPEED_LEFT = 0.05;
    public static final double TURRET_SPEED_RIGHT = 0.1;
    final double MAX_ERROR = COUNTS_PER_DEGREE * 20;
    final double MIN_OUTPUT = 0.1;
    
    Jaguar turretMotor = new Jaguar(RobotMap.TURRET_MOTOR);
    
    Encoder encTurret = new Encoder(RobotMap.ENC_TURRET_A, RobotMap.ENC_TURRET_B);
    
    OutputStorage outputStorage = new OutputStorage();

    //PIDController pidTurret = new PIDController(PID_P, PID_I, PID_D, encTurret, outputStorage);
    
    DigitalInput leftLimit = new DigitalInput(RobotMap.TURRET_LEFT_LIMIT);
    DigitalInput rightLimit = new DigitalInput(RobotMap.TURRET_RIGHT_LIMIT);
    
    int searchDirection = 1;
    
    int setPoint = 0;
    double output = 0;
    public void execute() {
        final int currentSpot = encTurret.get();
        //double output = outputStorage.get();
        output = 0;
        final int error = currentSpot - setPoint;
        if(error >= TOLERANCE) {
            output = -TURRET_SPEED_LEFT;
        } else if(error <= -TOLERANCE) {
            output = TURRET_SPEED_RIGHT;
        }
        if((rightLimit.get() && output < 0)
           || (leftLimit.get() && output > 0)) {
            output = 0;
        }
        
        if(Math.abs(error) > MAX_ERROR) {
            output *= 2;
        }
        
        /*double percentageError = Math.abs(error / MAX_ERROR);
        percentageError = Math.abs(percentageError * output) < MIN_OUTPUT ? Math.abs(MIN_OUTPUT / output) : percentageError;
        if(percentageError < 1.0) {
            output *= percentageError;
        }*/
        
        turretMotor.set(output); 
    }
    
    public void setRelativeAngleSetpoint(double angle) {
        if(angle != 0 && (onTarget() || leftLimit.get() || rightLimit.get())) {
            setPoint = encTurret.get() + (int)(angle * COUNTS_PER_DEGREE);
            //pidTurret.setSetpoint(setPoint);
        }
    }
    
    public boolean onTarget() {
        final int error = encTurret.get() - setPoint;
        if(error < TOLERANCE && error > -TOLERANCE) {
            return true;
        }
        return false;
    }

    public TurretSubsystem() {
        encTurret.start();
        encTurret.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
        /*pidTurret.setOutputRange(-TURRET_SPEED_LEFT, TURRET_SPEED_RIGHT);
        pidTurret.setInputRange(-COUNTS_PER_DEGREE * 360, COUNTS_PER_DEGREE * 360);
        pidTurret.setTolerance(COUNTS_PER_DEGREE / (COUNTS_PER_DEGREE * 360));
        pidTurret.enable();*/
        
        
        //SmartDashboard.putData("PIDTurret", pidTurret);
    }
    
    public void reset() {
        encTurret.reset();
    }
    
    public void disable() {
        /*if(pidTurret.isEnable()) {
            reset();
            pidTurret.disable();
        }*/
    }
    
    public void enable() {
        setPoint = 0;
        reset();
        /*if(!pidTurret.isEnable()) {
            reset();
            pidTurret.enable();
        }*/
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new TurretCommand());
    }
    
    /*public void setRelativeAngleSetpoint(double angle) {
        pidTurret.setSetpoint(encTurret.get() + angle * COUNTS_PER_DEGREE);
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
        setRelativeAngleSetpoint(searchDirection);
    }*/
    
    public void print() {
        System.out.print("(Turret Subsystem)\n");
        System.out.print("setPoint (counts): " + setPoint + " setPoint (relative degrees): " + ((setPoint - encTurret.get()) / COUNTS_PER_DEGREE) + " current: " + encTurret.get() + " output: " + output + " onTarget: " + onTarget() + " error: " + (encTurret.get() - setPoint) + "\n");
        //System.out.print("PIDTurret output: " + pidTurret.get() + " PIDTurret setpoint: " + pidTurret.getSetpoint() + " encTurret: " + encTurret.get() + "\n");
    }
}
