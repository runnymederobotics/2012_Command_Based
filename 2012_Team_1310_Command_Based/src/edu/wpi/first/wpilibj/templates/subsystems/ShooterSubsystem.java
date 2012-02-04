package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendablePIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.DistanceShooterCommand;

public class ShooterSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    final int MAX_SHOOTER_ENCODER_RATE = 15000;
    final double MAX_SHOOTER_DISTANCE = 500; //5 metres
    
    final double MIN_SHOOTER_RANGE = MAX_SHOOTER_ENCODER_RATE * 0.5;
    final double MAX_SHOOTER_RANGE = MAX_SHOOTER_ENCODER_RATE * 0.9;
    
    Victor shooterMotor = new Victor(RobotMap.SHOOTER_MOTOR);
    
    Encoder encShooter = new Encoder(RobotMap.ENC_SHOOTER_A, RobotMap.ENC_SHOOTER_B, true);
    
    SendablePIDController pidShooter = new SendablePIDController(0.0001, 0.0, 0.0, encShooter, shooterMotor);
    
    public ShooterSubsystem() {
        encShooter.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encShooter.start();
        
        pidShooter.setInputRange(0.0, MAX_SHOOTER_ENCODER_RATE);
        pidShooter.setOutputRange(0.0, 1.0);
        SmartDashboard.putData("PIDShooter", pidShooter);
        
        enablePID();
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DistanceShooterCommand());
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
    
    public void setSetpoint(double rate) {
        //shooterMotor.set(rate);
        
        pidShooter.setSetpoint(rate * MAX_SHOOTER_ENCODER_RATE);
    }
    
    public void print() {
        System.out.println("(Shooter Subsystem)");
        
        System.out.println("PIDShooter output: " + pidShooter.get() + " PIDShooter setpoint: " + pidShooter.getSetpoint() + " + EncShooter: " + encShooter.getRate());
    }
}
