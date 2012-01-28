package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableGyro;
import edu.wpi.first.wpilibj.smartdashboard.SendablePIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.OutputStorage;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.DriveCommand;

public class ChassisSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    public static final int MAX_LOW_ENCODER_RATE = 800;
    public static final int MAX_HIGH_ENCODER_RATE = 1900;
    public static final double AUTO_TRANS_THRESHOLD = 0.6;
    
    public static final double PID_P = 0.0, PID_I = 0.0005, PID_D = 0.0;
    
    NetworkTable encoderTable;
    
    public Victor motorLeft;
    public Victor motorRight;
    
    OutputStorage storageLeft;
    OutputStorage storageRight;
    
    RobotDrive robotDrive;
    
    public Encoder encLeft;
    public Encoder encRight;
    
    public SendablePIDController pidLeft;
    public SendablePIDController pidRight;
    
    public SendableGyro gyroXY;
    public SendableGyro gyroYZ;
    
    OutputStorage pidGyroStorage;
    SendablePIDController pidGyro;
    
    Pneumatic transShift;

    double maxEncoderRate;
    
    public void initDefaultCommand() {
        motorLeft = new Victor(RobotMap.LEFT_MOTOR);
        motorRight = new Victor(RobotMap.RIGHT_MOTOR);
        
        storageLeft = new OutputStorage();
        storageRight = new OutputStorage();
        
        robotDrive = new RobotDrive(storageLeft, storageRight);
        
        encLeft = new Encoder(RobotMap.ENC_LEFT_A, RobotMap.ENC_LEFT_B, true);
        encRight = new Encoder(RobotMap.ENC_RIGHT_A, RobotMap.ENC_RIGHT_B, true);
        encLeft.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encRight.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encLeft.start();
        encRight.start();
        
        pidLeft = new SendablePIDController(PID_P, PID_I, PID_D, encLeft, motorLeft);
        pidRight = new SendablePIDController(PID_P, PID_I, PID_D, encRight, motorRight);
        SmartDashboard.putData("PIDLeft", pidLeft);
        SmartDashboard.putData("PIDRight", pidRight);
        
        gyroXY.reset();
        gyroYZ.reset();
        
        pidGyroStorage = new OutputStorage();
        pidGyro = new SendablePIDController(0.0, 0.0, 0.0, gyroXY, pidGyroStorage);
        
        transShift = new Pneumatic(new DoubleSolenoid(1, 2));
        
        maxEncoderRate = MAX_LOW_ENCODER_RATE;
        updateInputRange();
        
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveCommand());
    }
    
    void updateInputRange() {
        pidLeft.setInputRange(-maxEncoderRate, maxEncoderRate);
        pidLeft.setOutputRange(-1.0, 1.0);
        
        pidRight.setInputRange(-maxEncoderRate, maxEncoderRate);
        pidRight.setOutputRange(-1.0, 1.0);
    }
    
    public void disablePIDGyro() {
        if(pidGyro.isEnable()) {
            pidGyro.disable();
        }
    }
    
    public void enablePIDGyro() {
        if(!pidGyro.isEnable()) {
            gyroXY.reset();
            pidGyro.enable();
            pidGyro.setSetpoint(0.0);
        }
    }
    
    public void disablePID() {
        if(pidLeft.isEnable() || pidRight.isEnable()) {
            pidLeft.disable();
            pidRight.disable();
        }
    }
    
    public void enablePID() {
        if(!pidLeft.isEnable() || !pidRight.isEnable()) {
            pidLeft.enable();
            pidRight.enable();
        }
    }
    
    public double getXYAngle() {
        return gyroXY.getAngle();
    }
    
    public double getYZAngle() {
        return gyroYZ.getAngle();
    }
    
    public void transShift(boolean value) {
        transShift.set(value);
        maxEncoderRate = value ? MAX_HIGH_ENCODER_RATE : MAX_LOW_ENCODER_RATE;
        updateInputRange();
    }
    
    public void setAngleSetpoint(double angle) {
        enablePIDGyro();
        
        pidGyro.setSetpoint(angle);
    }
    
    public void goToGyroSetpoint() {
        setSetpoint(0.0, pidGyroStorage.get());
    }
    
    public void setSetpoint(double speed, double rotation) {
        
        
        /*if(!transShift.get() && Math.abs(encLeft.get()) >= MAX_LOW_ENCODER_RATE * AUTO_TRANS_THRESHOLD
                && Math.abs(encRight.get()) >= MAX_LOW_ENCODER_RATE * AUTO_TRANS_THRESHOLD)*/
        /*if(Math.abs(speed) >= AUTO_TRANS_THRESHOLD)
            transShift(true);
        else if(transShift.get())
            transShift(false);*/
        robotDrive.arcadeDrive(speed, rotation);
        
        
        if(pidLeft.isEnable() && pidRight.isEnable()) {
            pidLeft.setSetpoint(storageLeft.get() * maxEncoderRate);
            pidRight.setSetpoint(storageRight.get() * maxEncoderRate);
        } else {
            motorLeft.set(storageLeft.get());
            motorRight.set(storageRight.get()); 
        }
    }
}
