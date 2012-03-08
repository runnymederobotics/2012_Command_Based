package edu.wpi.first.wpilibj.templates.subsystems;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.Parsable.ParsableInteger;
import RobotCLI.RobotCLI;
import RobotCLI.RobotCLI.VariableContainer;
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
    
    final double PID_COUNT_MAX_INPUT = Short.MAX_VALUE;
    final double PID_COUNT_MIN_INPUT = -Short.MAX_VALUE;
    
    final double PID_P = 0.0, PID_I = 0.0003, PID_D = 0.0;
    final double PID_GYRO_P = 0.001, PID_GYRO_I = 0.0, PID_GYRO_D = 0.0;
    final double PID_COUNT_P = 0.05, PID_COUNT_I = 0.0, PID_COUNT_D = 0.0;
    
    ParsableInteger MAX_LOW_ENCODER_RATE;
    ParsableInteger MAX_HIGH_ENCODER_RATE;
    
    ParsableDouble PID_COUNT_TOLERANCE; //Counts
    ParsableDouble PID_GYRO_TOLERANCE; //Degrees
    
    ParsableDouble PID_COUNT_MAX_OUTPUT;
    ParsableDouble PID_GYRO_MAX_OUTPUT;
    
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

    //double maxEncoderRate = MAX_LOW_ENCODER_RATE;
    
    public ChassisSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("chassisSubsystem");
        
        MAX_LOW_ENCODER_RATE = vc.createInteger("maxLowEncoderRate", 800);
        MAX_HIGH_ENCODER_RATE = vc.createInteger("maxHighEncoderRate", 2100);
    
        PID_COUNT_TOLERANCE = vc.createDouble("pidCountTolerance (counts)", 30);
        PID_GYRO_TOLERANCE = vc.createDouble("pidGyroTolerance (degrees)", 1); //Degrees

        PID_COUNT_MAX_OUTPUT = vc.createDouble("pidCountMaxOutput", 0.5);
        PID_GYRO_MAX_OUTPUT = vc.createDouble("pidGyroMaxOutput", 0.25);
        
        encLeft.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encRight.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encLeft.start();
        encRight.start();
        
        pidLeft.setInputRange(-Short.MAX_VALUE, Short.MAX_VALUE);
        pidLeft.setOutputRange(-1.0, 1.0);
        
        pidRight.setInputRange(-Short.MAX_VALUE, Short.MAX_VALUE);
        pidRight.setOutputRange(-1.0, 1.0);
        
        gyroXY.reset();
        gyroYZ.reset();
        
        pidGyro.setTolerance(PID_GYRO_TOLERANCE.get() / 360);
        pidGyro.setContinuous();
        pidGyro.setInputRange(-180, 180);
        pidGyro.setOutputRange(-PID_GYRO_MAX_OUTPUT.get(), PID_GYRO_MAX_OUTPUT.get());
 
        pidLeftCount.setTolerance(PID_COUNT_TOLERANCE.get() / (PID_COUNT_MAX_INPUT - PID_COUNT_MIN_INPUT));
        pidLeftCount.setInputRange(PID_COUNT_MIN_INPUT, PID_COUNT_MAX_INPUT);
        pidLeftCount.setOutputRange(-PID_COUNT_MAX_OUTPUT.get(), PID_COUNT_MAX_OUTPUT.get());

        pidRightCount.setTolerance(PID_COUNT_TOLERANCE.get() / (PID_COUNT_MAX_INPUT - PID_COUNT_MIN_INPUT));
        pidRightCount.setInputRange(PID_COUNT_MIN_INPUT, PID_COUNT_MAX_INPUT);
        pidRightCount.setOutputRange(-PID_COUNT_MAX_OUTPUT.get(), PID_COUNT_MAX_OUTPUT.get());
        
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
    
    public void disablePID() {
        if(pidLeft.isEnable() || pidRight.isEnable()) {
            pidLeft.disable();
            pidRight.disable();
        }
    }
    
    public void disablePIDCount() {
        if(pidLeftCount.isEnable() || pidRightCount.isEnable()) {
            pidLeftCount.setSetpoint(0.0);
            pidRightCount.setSetpoint(0.0);
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
        //maxEncoderRate = value ? MAX_HIGH_ENCODER_RATE : MAX_LOW_ENCODER_RATE;
    }
    
    public void setCountSetpoint(int encoderCounts) {
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
        System.out.println("goToCountSetpoint Function");
        System.out.println("leftSetpoint:" + pidLeftCount.getSetpoint() + " rightSetpoint: " + pidRightCount.getSetpoint());
        System.out.println("leftCounts: " + encLeftCount.pidGet() + " rightCounts: " + encRightCount.pidGet());
        System.out.println("pidCountLeft: " + pidLeftCount.get() + " pidCountRight: " + pidRightCount.get());
        System.out.println("pidCountLeftError: " + pidLeftCount.getError() + " pidCountRightError: " + pidRightCount.getError());
        robotDrive.arcadeDrive(0.0, 0.0); //Keep robotDrive updated
        if(reachedCountSetpoint()) {
            disablePIDCount();
        } else {
            //drive((pidLeftCount.get() + pidRightCount.get()) / -2, 0.0, false);
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
        double leftError = Math.abs(pidLeftCount.getSetpoint() - encLeftCount.pidGet());
        double rightError = Math.abs(pidRightCount.getSetpoint() - encRightCount.pidGet());
        return leftError < PID_COUNT_TOLERANCE.get() && rightError < PID_COUNT_TOLERANCE.get();
        //return (pidLeftCount.onTarget() && pidRightCount.onTarget());
    }
    
    public boolean reachedAngleSetpoint() {
        return pidGyro.onTarget();
    }
    
    public void drive(double speed, double rotation, boolean autoTrans) {
        robotDrive.arcadeDrive(speed, rotation);

        setSetpoint(storageLeft.get(), storageRight.get(), autoTrans);
    }
    
    private void setSetpoint(double left, double right, boolean autoTrans) {
        double leftSetpoint;
        double rightSetpoint;
        
        if(autoTrans) {
            //Right rate - left rate because left rate is negative
            final double rate = Math.abs(encRight.getRate() - encLeft.getRate()) / 2; //Average rate
             
            final double SWITCH_UP_PERCENT = 0.9; //Threshold to switch at when in low speed
            final double SWITCH_DOWN_PERCENT = 0.5; //Threshold to switch at when in high speed
            
            if(rate >= SWITCH_UP_PERCENT * MAX_LOW_ENCODER_RATE.get()) {
                transShift.set(true);
            } else if(rate <= SWITCH_DOWN_PERCENT * MAX_LOW_ENCODER_RATE.get()) {
                transShift.set(false);
            }
            
            //Use high gear in auto trans so that you dont get a jump in speed when it actually shifts
            leftSetpoint = left * MAX_HIGH_ENCODER_RATE.get();
            rightSetpoint = right * MAX_HIGH_ENCODER_RATE.get();
        } else {
            leftSetpoint = left * (transShift.get() ? MAX_HIGH_ENCODER_RATE.get() : MAX_LOW_ENCODER_RATE.get());
            rightSetpoint = right * (transShift.get() ? MAX_HIGH_ENCODER_RATE.get() : MAX_LOW_ENCODER_RATE.get());
        }
        
        if(pidLeft.isEnable() && pidRight.isEnable()) {
            pidLeft.setSetpoint(leftSetpoint);
            pidRight.setSetpoint(rightSetpoint);
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
