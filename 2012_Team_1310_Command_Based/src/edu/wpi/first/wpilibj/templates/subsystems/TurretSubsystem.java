package edu.wpi.first.wpilibj.templates.subsystems;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.Parsable.ParsableInteger;
import RobotCLI.RobotCLI;
import RobotCLI.RobotCLI.VariableContainer;
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

    public static final int SEARCH_RIGHT = 1;
    public static final int SEARCH_LEFT = -1;
    
    public ParsableInteger SEARCH_ANGLE;
    
    //static final double PID_P = 0.05, PID_I = 0.000, PID_D = 0.0;
    ParsableDouble COUNTS_PER_DEGREE;
    ParsableDouble TOLERANCE;
    
    public ParsableDouble TURRET_SPEED_LEFT;
    public ParsableDouble TURRET_SPEED_RIGHT;
    ParsableDouble SLOW_TURRET_RANGE;
    
    Jaguar turretMotor = new Jaguar(RobotMap.TURRET_MOTOR);
    
    Encoder encTurret = new Encoder(RobotMap.ENC_TURRET_A, RobotMap.ENC_TURRET_B);
    
    OutputStorage outputStorage = new OutputStorage();

    //PIDController pidTurret = new PIDController(PID_P, PID_I, PID_D, encTurret, outputStorage);
    
    DigitalInput leftLimit = new DigitalInput(RobotMap.TURRET_LEFT_LIMIT);
    DigitalInput rightLimit = new DigitalInput(RobotMap.TURRET_RIGHT_LIMIT);
    
    int searchDirection = 1;
    
    int setPoint = 0;
    double output = 0;
    
    public TurretSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("turretSubsystem");
        
        SEARCH_ANGLE = vc.createInteger("searchAngle", 10);
        COUNTS_PER_DEGREE = vc.createDouble("countsPerDegree", 9.18);
        TOLERANCE = vc.createDouble("tolerance", 1);
        TURRET_SPEED_LEFT = vc.createDouble("turretSpeedLeft", 0.2);
        TURRET_SPEED_RIGHT = vc.createDouble("turretSpeedRight", 0.3);
        SLOW_TURRET_RANGE = vc.createDouble("slowTurretRange", 8);
        
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
    
    public void execute() {
        final int currentSpot = encTurret.get();
        //double output = outputStorage.get();
        output = 0;
        final int error = currentSpot - setPoint;
        if(error >= COUNTS_PER_DEGREE.get() * TOLERANCE.get()) {
            output = TURRET_SPEED_LEFT.get();
        } else if(error <= COUNTS_PER_DEGREE.get() * -TOLERANCE.get()) {
            output = -TURRET_SPEED_RIGHT.get();
        }
        if((rightLimit.get() && output < 0)
           || (leftLimit.get() && output > 0)) {
            output = 0;
        }
        
        if(Math.abs(error) > COUNTS_PER_DEGREE.get() * SLOW_TURRET_RANGE.get()) {
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
            setPoint = encTurret.get() + (int)(angle * COUNTS_PER_DEGREE.get());
            //pidTurret.setSetpoint(setPoint);
        }
    }
    
    public boolean onTarget() {
        final int error = encTurret.get() - setPoint;
        if(error < COUNTS_PER_DEGREE.get() * TOLERANCE.get() && error > COUNTS_PER_DEGREE.get() * -TOLERANCE.get()) {
            return true;
        }
        return false;
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
    }*/
   
    
    public void searchForTarget(int requestedDirection) {
        boolean requestingDirection = requestedDirection != 0;
        if(requestingDirection) {
            searchDirection = requestedDirection;
            if(searchDirection == SEARCH_LEFT && leftLimit.get()) {
                searchDirection = 0;
            } else if(searchDirection == SEARCH_RIGHT && rightLimit.get()) {
                searchDirection = 0;
            }
        }/* else {
            if(leftLimit.get()) {
                searchDirection = SEARCH_RIGHT;
            } else if(rightLimit.get()) {
                searchDirection = SEARCH_LEFT;
            }
        }*/
        setRelativeAngleSetpoint(searchDirection * SEARCH_ANGLE.get());
    }
    
    public void print() {
        System.out.print("(Turret Subsystem)\n");
        System.out.print("setPoint (counts): " + setPoint + " setPoint (relative degrees): " + ((setPoint - encTurret.get()) / COUNTS_PER_DEGREE.get()) + " current: " + encTurret.get() + " output: " + output + " onTarget: " + onTarget() + " error: " + (encTurret.get() - setPoint) + "\n");
        System.out.print("leftLimit: " + leftLimit.get() + " rightLimit: " + rightLimit.get() + "\n");
        //System.out.print("PIDTurret output: " + pidTurret.get() + " PIDTurret setpoint: " + pidTurret.getSetpoint() + " encTurret: " + encTurret.get() + "\n");
    }
}
