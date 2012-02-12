package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendablePIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ShooterCommand;

public class ShooterSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    final int MAX_SHOOTER_ENCODER_RATE = 15000;
    
    Victor shooterMotor = new Victor(RobotMap.SHOOTER_MOTOR);
    
    Encoder encShooter = new Encoder(RobotMap.ENC_SHOOTER_A, RobotMap.ENC_SHOOTER_B, true);
    
    SendablePIDController pidShooter = new SendablePIDController(0.0001, 0.0, 0.0, encShooter, shooterMotor);
    
    public ShooterSubsystem() {
        encShooter.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encShooter.start();
        
        pidShooter.setInputRange(-MAX_SHOOTER_ENCODER_RATE, MAX_SHOOTER_ENCODER_RATE);
        pidShooter.setOutputRange(-1.0, 1.0);
        SmartDashboard.putData("PIDShooter", pidShooter);
        
        enablePID();
    }
    
    public void reset() {
        encShooter.reset();
    }
    
    public void disable() {
        reset();
        
        disablePID();
    }
    
    public void enable() {
        reset();
        
        enablePID();
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ShooterCommand());
    }
    
    private void enablePID() {
        if(!pidShooter.isEnable()) {
            pidShooter.enable();
        }
    }
    
    private void disablePID() {
        if(pidShooter.isEnable()) {
            pidShooter.disable();
        }
    }
    
    public boolean getShooterRunning() {
        return shooterMotor.get() != 0.0;
    }
    
    public void setSetpoint(double rate) {
        if(pidShooter.isEnable()) {
            pidShooter.setSetpoint(-rate * MAX_SHOOTER_ENCODER_RATE);
        } else {
            shooterMotor.set(rate);
        }
    }
    
    public void print() {
        System.out.print("(Shooter Subsystem)\n");
        
        System.out.print("PIDShooter output: " + pidShooter.get() + " PIDShooter setpoint: " + pidShooter.getSetpoint() + " + EncShooter: " + encShooter.getRate() + "\n");
    }
}
