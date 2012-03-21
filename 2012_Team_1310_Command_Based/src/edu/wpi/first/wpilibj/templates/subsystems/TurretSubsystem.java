package edu.wpi.first.wpilibj.templates.subsystems;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.Parsable.ParsableInteger;
import RobotCLI.ParsablePIDController;
import RobotCLI.RobotCLI;
import RobotCLI.RobotCLI.VariableContainer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.TurretCommand;

public class TurretSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public static final int SEARCH_RIGHT = -1;
    public static final int SEARCH_LEFT = 1;
    
    public ParsableInteger SEARCH_ANGLE;
    public ParsableDouble COUNTS_PER_DEGREE;
    public ParsableDouble CAMERA_ERROR;
    public ParsableDouble NEW_SETPOINT_TOLERANCE;
    ParsableDouble MANUAL_MODE_SPEED;
    
    Jaguar turretMotor = new Jaguar(RobotMap.TURRET_MOTOR);
    
    Encoder encTurret = new Encoder(RobotMap.ENC_TURRET_A, RobotMap.ENC_TURRET_B, true);

    ParsablePIDController pidTurret;
    
    DigitalInput leftLimit = new DigitalInput(RobotMap.TURRET_LEFT_LIMIT);
    DigitalInput rightLimit = new DigitalInput(RobotMap.TURRET_RIGHT_LIMIT);
    
    Pneumatic cameraLight = new Pneumatic(new Relay(RobotMap.CAMERA_LIGHT_RELAY));
    
    int searchDirection = 1;
    
    public TurretSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("turretSubsystem");
        
        SEARCH_ANGLE = vc.createInteger("searchAngle", 10);
        COUNTS_PER_DEGREE = vc.createDouble("countsPerDegree", 9.18);
        CAMERA_ERROR = vc.createDouble("cameraError", 2.5); //Was 2.5
        NEW_SETPOINT_TOLERANCE = vc.createDouble("newSetpointTolerance", 18.36);
        MANUAL_MODE_SPEED = vc.createDouble("manualModeSpeed", 0.5);
        
        pidTurret = new ParsablePIDController("pidTurret", robotCLI.getVariables(), 0.005, 0.00001, 0.0, -0.5, 0.5, 8.75);
        
        encTurret.start();
        encTurret.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new TurretCommand());
    }
    
    public void reset() {
        encTurret.reset();
        pidTurret.reset();
    }
    
    public void disable() {
        reset();
    }
    
    public void enable() {
        reset();
    }
    
    public boolean onTarget() {
        return pidTurret.onTarget();
    }
    
    public void setCameraLight(boolean value) {
        cameraLight.set(value);
    }
    
    public void execute() {        
        pidTurret.setInput(encTurret.get());
        pidTurret.process();
        
        double output = pidTurret.getOutput();
        if((rightLimit.get() && output < 0)
           || (leftLimit.get() && output > 0)) {
            output = 0;
        }
        
        turretMotor.set(output);
    }
    
    public void manualMode(double output) {
        if((rightLimit.get() && output < 0)
           || (leftLimit.get() && output > 0)) {
            output = 0;
        }
        turretMotor.set(output * MANUAL_MODE_SPEED.get());
        reset();
    }
    
    public void setRelativeAngleSetpoint(double angle) {
        boolean onTarget = Math.abs(pidTurret.getError()) < NEW_SETPOINT_TOLERANCE.get();
        
        if(angle != 0 && (onTarget || leftLimit.get() || rightLimit.get())) {
            //setPoint = encTurret.get() + (int)(angle * COUNTS_PER_DEGREE.get());
            //pidTurret.reset();
            pidTurret.setSetpoint(encTurret.get() + angle * COUNTS_PER_DEGREE.get());
        }
    }
    
    public void print() {
        System.out.print("(Turret Subsystem)\n");
        //System.out.print("setPoint (counts): " + setPoint + " setPoint (relative degrees): " + ((setPoint - encTurret.get()) / COUNTS_PER_DEGREE.get()) + " current: " + encTurret.get() + " output: " + output + " onTarget: " + onTarget() + " error: " + (encTurret.get() - setPoint) + "\n");
        System.out.print("leftLimit: " + leftLimit.get() + " rightLimit: " + rightLimit.get() + "\n");
        //System.out.print("PIDTurret output: " + pidTurret.get() + " PIDTurret setpoint: " + pidTurret.getSetpoint() + " encTurret: " + encTurret.get() + "\n");
    }
}
