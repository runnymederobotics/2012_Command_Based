package edu.wpi.first.wpilibj.templates.subsystems;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.Parsable.ParsableInteger;
import RobotCLI.ParsablePIDController;
import RobotCLI.RobotCLI;
import RobotCLI.RobotCLI.VariableContainer;
import com.sun.squawk.util.Arrays;
import com.sun.squawk.util.Comparer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ShooterCommand;

public class ShooterSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public ParsableInteger MAX_SHOOTER_ENCODER_RATE;
    ParsableDouble READY_TO_SHOOT_TOLERANCE;
    
    Jaguar shooterMotor = new Jaguar(RobotMap.SHOOTER_MOTOR);
    
    Encoder encShooter = new Encoder(RobotMap.ENC_SHOOTER_A, RobotMap.ENC_SHOOTER_B, true);
    
    ParsablePIDController pidShooter;

    public ShooterSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("shooterSubsystem");
        
        MAX_SHOOTER_ENCODER_RATE = vc.createInteger("maxShooterEncoderRate", 4000);
        READY_TO_SHOOT_TOLERANCE = vc.createDouble("readyToShootTolerance", 150);
        pidShooter = new ParsablePIDController("pidShooter", robotCLI.getVariables(), 0.002, 0.0, 0.0, 0.0, 1.0, 50);
        
        Arrays.sort(setpointLookupTable, new Comparer() {
            public int compare(Object o1, Object o2) {
                ExperimentalMeasurement one = (ExperimentalMeasurement)o1;
                ExperimentalMeasurement two = (ExperimentalMeasurement)o2;
                
                if(one.distance < two.distance) {
                    return -1;
                } else if(one.distance > two.distance) {
                    return 1;
                }
                return 0;
            }
        });
        
        System.out.print("*******************POWER LOOKUP TABLE START*******************\n");
        for(int i = 0; i < setpointLookupTable.length - 1; ++i) {
            System.out.print(setpointLookupTable[i].distance + " " + setpointLookupTable[i].power + "\n");
        }
        System.out.print("*******************POWER LOOKUP TABLE END*******************\n");
        
        encShooter.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encShooter.start();
    }
    
    public void reset() {
        encShooter.reset();
    }
    
    public void disable() {
        reset();
    }
    
    public void enable() {
        reset();
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ShooterCommand());
    }

    ExperimentalMeasurement[] setpointLookupTable = {
        new ExperimentalMeasurement(0.0, 0.0),
        new ExperimentalMeasurement(80, 2450),
        new ExperimentalMeasurement(88, 2300),
        new ExperimentalMeasurement(92, 2300),
        new ExperimentalMeasurement(105, 2300),
        new ExperimentalMeasurement(113, 2453),
        new ExperimentalMeasurement(130, 2500),
        new ExperimentalMeasurement(152, 2730),
        new ExperimentalMeasurement(210, 3300), //Full speed
        new ExperimentalMeasurement(1000.0, 10000),
    };
    
    double interpolateMeasurements(double distance, ExperimentalMeasurement one, ExperimentalMeasurement two) {
        return ((distance - one.distance) / (two.distance - one.distance)) * (two.power - one.power) + one.power;
    }
    
    public double getSetpointFromDistance(double distance) {
        int start = 0;
        int end = (setpointLookupTable.length - 1);

        while(start <= end) {
            int i = (start + end ) / 2;
            if(i >= setpointLookupTable.length)
                break;
            ExperimentalMeasurement current = setpointLookupTable[i];
            ExperimentalMeasurement next = setpointLookupTable[i + 1];
            
            if(current.distance <= distance && next.distance > distance) {
                return interpolateMeasurements(distance, current, next);
            }
            
            if(current.distance > distance) {
                end = i - 1;
            } else {
                start = i + 1;
            }
        }
        
        return MAX_SHOOTER_ENCODER_RATE.get();
    }
    
    public boolean onTarget() {
        return Math.abs(encShooter.getRate() - pidShooter.getSetpoint()) < READY_TO_SHOOT_TOLERANCE.get();
    }
    
    public boolean getShooterRunningAndOnTarget() {
        return shooterMotor.get() != 0.0 && onTarget();
    }
    
    public void setSetpoint(double rate) {
        pidShooter.setInput(encShooter.getRate());
        pidShooter.setSetpoint(rate * MAX_SHOOTER_ENCODER_RATE.get());
        pidShooter.process();
        shooterMotor.set(pidShooter.getOutput());
    }
    
    public void print() {
        System.out.print("(Shooter Subsystem)\n");
        
        System.out.print("PIDShooter output: " + pidShooter.getOutput() + " PIDShooter setpoint: " + pidShooter.getSetpoint() + " + EncShooter: " + encShooter.getRate() + "\n");
    }
}

class ExperimentalMeasurement {
    double distance;
    double power;

    public ExperimentalMeasurement(double distance, double power) {
        this.distance = distance;
        this.power = power;
    }
}