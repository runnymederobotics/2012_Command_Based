package edu.wpi.first.wpilibj.templates.subsystems;

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

    ParsableInteger MAX_SHOOTER_ENCODER_RATE;
    
    Jaguar shooterMotor = new Jaguar(RobotMap.SHOOTER_MOTOR);
    
    Encoder encShooter = new Encoder(RobotMap.ENC_SHOOTER_A, RobotMap.ENC_SHOOTER_B, true);
    
    ParsablePIDController pidShooter;

    public ShooterSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("shooterSubsystem");
        
        MAX_SHOOTER_ENCODER_RATE = vc.createInteger("maxShooterEncoderRate", 4000);
        pidShooter = new ParsablePIDController("pidShooter", robotCLI.getVariables(), 0.004, 0.0, 0.0, 0.0, 0.9, 50);
        
        Arrays.sort(powerLookupTable, new Comparer() {
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
        for(int i = 0; i < powerLookupTable.length - 1; ++i) {
            System.out.print(powerLookupTable[i].distance + " " + powerLookupTable[i].power + "\n");
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
    //march7:
    //148 3062(counts)
    //97 2600
    //155 2860
    
    //end march7
    ExperimentalMeasurement[] powerLookupTable = {
        new ExperimentalMeasurement(0.0, 0.0),
        
        new ExperimentalMeasurement(107, 0.33),
        new ExperimentalMeasurement(93, 0.36),
        new ExperimentalMeasurement(110.5, 0.39),
        new ExperimentalMeasurement(78, 0.31),
        new ExperimentalMeasurement(160, 0.56),
        
        new ExperimentalMeasurement(10000.0, 1.0),
    };
    
    double interpolateMeasurements(double distance, ExperimentalMeasurement one, ExperimentalMeasurement two) {
        return ((distance - one.distance) / (two.distance - one.distance)) * (two.power - one.power) + one.power;
    }
    
    public double getPowerFromDistance(double distance) {
        int start = 0;
        int end = (powerLookupTable.length - 1);

        while(start <= end) {
            int i = (start + end ) / 2;
            if(i >= powerLookupTable.length)
                break;
            ExperimentalMeasurement current = powerLookupTable[i];
            ExperimentalMeasurement next = powerLookupTable[i + 1];
            
            if(current.distance <= distance && next.distance > distance) {
                return interpolateMeasurements(distance, current, next);
            }
            
            if(current.distance > distance) {
                end = i - 1;
            } else {
                start = i + 1;
            }
        }
        
        return 1.0;
    }
    
    public boolean getShooterRunning() {
        return shooterMotor.get() != 0.0;
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