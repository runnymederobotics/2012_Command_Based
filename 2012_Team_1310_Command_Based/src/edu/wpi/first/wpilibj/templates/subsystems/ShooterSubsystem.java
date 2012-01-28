package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendablePIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.RobotMap;

public class ShooterSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    final int MAX_SHOOTER_ENCODER_RATE = 1000;
    final double MAX_SHOOTER_DISTANCE = 500; //5 metres
    
    final double MIN_SHOOTER_RANGE = MAX_SHOOTER_ENCODER_RATE * 0.5;
    final double MAX_SHOOTER_RANGE = MAX_SHOOTER_ENCODER_RATE * 0.9;
    
    Victor shooterMotor;
    
    Encoder encShooter;
    
    SendablePIDController pidShooter;
    
    public void initDefaultCommand() {
        shooterMotor = new Victor(RobotMap.SHOOTER_MOTOR);
        
        encShooter = new Encoder(RobotMap.ENC_SHOOTER_A, RobotMap.ENC_SHOOTER_B);
        encShooter.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encShooter.start();
        
        pidShooter = new SendablePIDController(0.0, 0.0, 0.0, encShooter, shooterMotor);
        pidShooter.setInputRange(0.0, MAX_SHOOTER_ENCODER_RATE);
        SmartDashboard.putData("PIDShooter", pidShooter);
        
        // Set the default command for a subsystem here.
        //setDefaultCommand(new DistanceShooterCommand());
    }
    
    public void setSetpoint(double rate) {
        pidShooter.setSetpoint(rate);
    }
}
