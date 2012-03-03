package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableGyro;
import edu.wpi.first.wpilibj.smartdashboard.SendablePIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.CountEncoder;
import edu.wpi.first.wpilibj.templates.OutputStorage;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.CommandBase;
import edu.wpi.first.wpilibj.templates.commands.DriveCommand;

public class ChassisSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    final int MAX_LOW_ENCODER_RATE = 800;
    final int MAX_HIGH_ENCODER_RATE = 1900;
    
    final double PID_COUNT_TOLERANCE = 10; //Counts
    final double PID_GYRO_TOLERANCE = 1; //Degrees
    
    final double PID_COUNT_MAX_INPUT = Short.MAX_VALUE;
    final double PID_COUNT_MIN_INPUT = -Short.MAX_VALUE;
    
    final double PID_COUNT_MAX_OUTPUT = 0.25;
    final double PID_GYRO_MAX_OUTPUT = 0.25;
    
    final double PID_P = 0.0, PID_I = 0.0003, PID_D = 0.0;
    final double PID_GYRO_P = 0.001, PID_GYRO_I = 0.0, PID_GYRO_D = 0.0;
    final double PID_COUNT_P = 0.05, PID_COUNT_I = 0.0, PID_COUNT_D = 0.0;
    
    Jaguar motorLeft = new Jaguar(RobotMap.LEFT_MOTOR);
    Jaguar motorRight = new Jaguar(RobotMap.RIGHT_MOTOR);
    
    OutputStorage storageLeft = new OutputStorage();
    OutputStorage storageRight = new OutputStorage();
    
    RobotDrive robotDrive = new RobotDrive(storageLeft, storageRight);
    
    Encoder encLeft = new Encoder(RobotMap.ENC_LEFT_A, RobotMap.ENC_LEFT_B, true);
    Encoder encRight = new Encoder(RobotMap.ENC_RIGHT_A, RobotMap.ENC_RIGHT_B, true);
    
    SendablePIDController pidLeft = new SendablePIDController(PID_P, PID_I, PID_D, encLeft, motorLeft);
    SendablePIDController pidRight = new SendablePIDController(PID_P, PID_I, PID_D, encRight, motorRight);
    
    SendableGyro gyroXY = new SendableGyro(RobotMap.GYRO_XY);
    SendableGyro gyroYZ = new SendableGyro(RobotMap.GYRO_YZ);
    
    OutputStorage pidGyroStorage = new OutputStorage();
    SendablePIDController pidGyro = new SendablePIDController(PID_GYRO_P, PID_GYRO_I, PID_GYRO_D, gyroXY, pidGyroStorage);
    
    CountEncoder encLeftCount = new CountEncoder(encLeft, true); //Reverse again for counts
    CountEncoder encRightCount = new CountEncoder(encRight, false);
    OutputStorage pidLeftCountStorage = new OutputStorage();
    OutputStorage pidRightCountStorage = new OutputStorage();
    SendablePIDController pidLeftCount = new SendablePIDController(PID_COUNT_P, PID_COUNT_I, PID_COUNT_D, encLeftCount, pidLeftCountStorage);
    SendablePIDController pidRightCount = new SendablePIDController(PID_COUNT_P, PID_COUNT_I, PID_COUNT_D, encRightCount, pidRightCountStorage);
    
    Pneumatic transShift = new Pneumatic(new DoubleSolenoid(1, 2));

    double maxEncoderRate = MAX_LOW_ENCODER_RATE;
    
    public ChassisSubsystem() {
        encLeft.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encRight.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encLeft.start();
        encRight.start();
        
        gyroXY.reset();
        gyroYZ.reset();
        
        pidGyro.setTolerance(PID_GYRO_TOLERANCE / 360);
        pidGyro.setContinuous();
        pidGyro.setInputRange(-180, 180);
        pidGyro.setOutputRange(-PID_GYRO_MAX_OUTPUT, PID_GYRO_MAX_OUTPUT);
 
        pidLeftCount.setTolerance(PID_COUNT_TOLERANCE / (PID_COUNT_MAX_INPUT - PID_COUNT_MIN_INPUT));
        pidLeftCount.setInputRange(PID_COUNT_MIN_INPUT, PID_COUNT_MAX_INPUT);
        pidLeftCount.setOutputRange(-PID_COUNT_MAX_OUTPUT, PID_COUNT_MAX_OUTPUT);

        pidRightCount.setTolerance(PID_COUNT_TOLERANCE / (PID_COUNT_MAX_INPUT - PID_COUNT_MIN_INPUT));
        pidRightCount.setInputRange(PID_COUNT_MIN_INPUT, PID_COUNT_MAX_INPUT);
        pidRightCount.setOutputRange(-PID_COUNT_MAX_OUTPUT, PID_COUNT_MAX_OUTPUT);
        
        updateInputRange();
        
        SmartDashboard.putData("PIDLeft", pidLeft);
        SmartDashboard.putData("PIDRight", pidRight);
        SmartDashboard.putData("XYGyro", gyroXY);
        SmartDashboard.putData("YZGyro", gyroYZ);
        SmartDashboard.putData("PIDGyro", pidGyro);
        SmartDashboard.putData("PIDLeftCount", pidLeftCount);
        SmartDashboard.putData("PIDRightCount", pidRightCount);
    }
    
    private void reset() {
        encLeft.reset();
        encRight.reset();
        
        gyroXY.reset();
        gyroYZ.reset();
    }
    
    public void disable() {
        reset();
        
        disablePID();
        disablePIDCount();
        disablePIDGyro();
    }
    
    public void enable() {
        reset();
        
        enablePID();
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveCommand());
    }
    
    private void updateInputRange() {
        pidLeft.setInputRange(-Short.MAX_VALUE, Short.MAX_VALUE);
        pidLeft.setOutputRange(-1.0, 1.0);
        
        pidRight.setInputRange(-Short.MAX_VALUE, Short.MAX_VALUE);
        pidRight.setOutputRange(-1.0, 1.0);
    }
    
    public void disablePID() {
        if(pidLeft.isEnable() || pidRight.isEnable()) {
            pidLeft.disable();
            pidRight.disable();
        }
    }
    
    public void disablePIDCount() {
        if(pidLeftCount.isEnable() || pidRightCount.isEnable()) {
            pidLeftCount.disable();
            pidRightCount.disable();
        }
    }
    
    public void disablePIDGyro() {
        if(pidGyro.isEnable()) {
            pidGyro.disable();
        }
    }

    public void enablePID() {
        if(!pidLeft.isEnable() || !pidRight.isEnable()) {
            pidLeft.enable();
            pidRight.enable();
        }
    }
    
    public void enablePIDCount() {
        if(!pidLeftCount.isEnable() || !pidRightCount.isEnable()) {
            encLeft.reset();
            encRight.reset();
            pidLeftCount.enable();
            pidRightCount.enable();
            pidLeftCount.setSetpoint(0.0);
            pidRightCount.setSetpoint(0.0);
        }
    }
    
    public void enablePIDGyro() {
        if(!pidGyro.isEnable()) {
            gyroXY.reset();
            pidGyro.enable();
            pidGyro.setSetpoint(0.0);
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
    }
    
    public void setCountSetpoint(int encoderCounts) {
        disablePIDCount(); //Make sure we reset the encoders
        enablePIDCount();
        //enablePID();
        
        pidLeftCount.setSetpoint(encoderCounts);
        pidRightCount.setSetpoint(encoderCounts);
    }
    
    public void setAngleSetpoint(double angle) {
        disablePIDGyro(); //Make sure we reset the gyro
        enablePIDGyro();
        //enablePID();
        
        pidGyro.setSetpoint(angle);
    }
    
    public void goToCountSetpoint() {
        if(pidLeftCount.onTarget() && pidRightCount.onTarget()) {
            disablePIDCount();
        } else {
            robotDrive.arcadeDrive(0.0, 0.0); //Keep robotDrive updated
            
            setSetpoint(-pidLeftCount.get(), pidRightCount.get(), false); //Dont allow high speed
        }
    }
    
    public void goToAngleSetpoint() {
        if(pidGyro.onTarget()) {
            disablePIDGyro();
        } else {
            drive(0.0, pidGyro.get(), false); //Dont allow high speed
        }
    }
    
    public boolean reachedCountSetpoint() {
        return (pidLeftCount.onTarget() && pidRightCount.onTarget());
    }
    
    public boolean reachedAngleSetpoint() {
        return pidGyro.onTarget();
    }
    
    public void drive(double speed, double rotation, boolean autoTrans) {
        robotDrive.arcadeDrive(speed, rotation);

        setSetpoint(storageLeft.get(), storageRight.get(), autoTrans);
    }
    
    private void setSetpoint(double left, double right, boolean autoTrans) {
        if(autoTrans) {
            final double rate = (Math.abs(encLeft.getRate()) + Math.abs(encRight.getRate())) / 2; //Average rate
            
            final double SWITCH_UP_PERCENT = 0.9; //Threshold to switch at when in low speed
            final double SWITCH_DOWN_PERCENT = 0.5; //Threshold to switch at when in high speed
            
            if(rate >= SWITCH_UP_PERCENT * MAX_LOW_ENCODER_RATE) {
                transShift.set(true);
            } else if(rate <= SWITCH_DOWN_PERCENT * MAX_LOW_ENCODER_RATE) {
                transShift.set(false);
            }
        }
        
        if(pidLeft.isEnable() && pidRight.isEnable()) {
            pidLeft.setSetpoint(left * maxEncoderRate);
            pidRight.setSetpoint(right * maxEncoderRate);
        } else {
            motorLeft.set(left);
            motorRight.set(right);
        }
    }
    
    public void print() {
        System.out.print("(Chassis Subsystem)\n");
        System.out.print("rate: " + (Math.abs(encLeft.getRate()) + Math.abs(encRight.getRate())) / 2 + "\n");
        System.out.print("transShift: " + transShift.get() + "\n");
        System.out.print("PIDLeftCount output: " + pidLeftCount.get() + " PIDRightCount output: " + pidRightCount.get() + "\n");
        System.out.print("PIDLeftCount setpoint: " + pidLeftCount.getSetpoint() + " PIDRightCount setpoint: " + pidRightCount.getSetpoint() + "\n");
        System.out.print("EncLeftCount: " + encLeftCount.pidGet() + " EncRightCount: " + encRightCount.pidGet() + "\n");
        System.out.print("PIDLeftCount storage: " + pidLeftCountStorage.get() + " PIDRightCount storage: " + pidRightCountStorage.get() + "\n");
        System.out.print("PIDEnabled: " + (pidLeft.isEnable() && pidRight.isEnable()) + "\n");
        
        System.out.print("OI Drive axis: " + CommandBase.oi.getSpeedAxis() + " OI Rotation Axis: " + CommandBase.oi.getRotationAxis() + "\n");
        
        System.out.print("PIDLeft output: " + pidLeft.get() + " PIDRight output: " + pidRight.get() + "\n");
        System.out.print("PIDLeft setpoint: " + pidLeft.getSetpoint() + " PIDRight setpoint: " + pidRight.getSetpoint() + "\n");
        System.out.print("EncLeftRate: " + encLeft.getRate() + " EncRightRate: " + encRight.getRate() + "\n");
    }
}
