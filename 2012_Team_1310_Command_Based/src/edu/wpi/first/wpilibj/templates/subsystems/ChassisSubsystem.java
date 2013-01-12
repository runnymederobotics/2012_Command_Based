package edu.wpi.first.wpilibj.templates.subsystems;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.Parsable.ParsableInteger;
import RobotCLI.ParsablePIDController;
import RobotCLI.RobotCLI;
import RobotCLI.RobotCLI.VariableContainer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.OutputStorage;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.CommandBase;
import edu.wpi.first.wpilibj.templates.commands.DriveCommand;

public class ChassisSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    public final boolean USE_AUTO_TRANS = false;

    final double PID_GYRO_P = 0.001, PID_GYRO_I = 0.0, PID_GYRO_D = 0.0;
    
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
    
    ParsablePIDController pidLeft;// = new SendablePIDController(PID_P, PID_I, PID_D, encLeft, motorLeft);
    ParsablePIDController pidRight;// = new SendablePIDController(PID_P, PID_I, PID_D, encRight, motorRight);
    
    Gyro gyroXY = new Gyro(RobotMap.GYRO_XY);
    Gyro gyroYZ = new Gyro(RobotMap.GYRO_YZ);
    
    OutputStorage pidGyroStorage = new OutputStorage();
    PIDController pidGyro = new PIDController(PID_GYRO_P, PID_GYRO_I, PID_GYRO_D, gyroXY, pidGyroStorage);

    ParsablePIDController pidLeftCount;// = new SendablePIDController(PID_COUNT_P, PID_COUNT_I, PID_COUNT_D, encLeftCount, pidLeftCountStorage);
    ParsablePIDController pidRightCount;// = new SendablePIDController(PID_COUNT_P, PID_COUNT_I, PID_COUNT_D, encRightCount, pidRightCountStorage);
    
    Pneumatic transShift = new Pneumatic(new DoubleSolenoid(RobotMap.TRANS_SHIFT_FORWARD, RobotMap.TRANS_SHIFT_REVERSE));
    
    boolean usePID = true;
    boolean usePIDCount = false;
    
    public ChassisSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("chassisSubsystem");
        
        MAX_LOW_ENCODER_RATE = vc.createInteger("maxLowEncoderRate", 800);
        MAX_HIGH_ENCODER_RATE = vc.createInteger("maxHighEncoderRate", 2100);
    
        PID_COUNT_TOLERANCE = vc.createDouble("pidCountTolerance", 30);
        PID_GYRO_TOLERANCE = vc.createDouble("pidGyroTolerance", 1); //Degrees

        PID_COUNT_MAX_OUTPUT = vc.createDouble("pidCountMaxOutput", 0.5);
        PID_GYRO_MAX_OUTPUT = vc.createDouble("pidGyroMaxOutput", 0.25);
        
        pidLeft = new ParsablePIDController("pidLeft", robotCLI.getVariables(), 0.0, 0.0025, 0.0, -1.0, 1.0, 50);
        pidRight = new ParsablePIDController("pidRight", robotCLI.getVariables(), 0.0, 0.0025, 0.0, -1.0, 1.0, 50);
        pidLeftCount = new ParsablePIDController("pidLeftCount", robotCLI.getVariables(), 0.0075, 0.0, 0.0, -0.9, 0.9, 50);
        pidRightCount = new ParsablePIDController("pidRightCount", robotCLI.getVariables(), 0.0075, 0.0, 0.0, -0.9, 0.9, 50);

        encLeft.start();
        encRight.start();
        
        gyroXY.reset();
        gyroYZ.reset();
        
        pidGyro.setAbsoluteTolerance(PID_GYRO_TOLERANCE.get() / 360);
        pidGyro.setContinuous();
        pidGyro.setInputRange(-180, 180);
        pidGyro.setOutputRange(-PID_GYRO_MAX_OUTPUT.get(), PID_GYRO_MAX_OUTPUT.get());

        SmartDashboard.putData("XYGyro", gyroXY);
        SmartDashboard.putData("YZGyro", gyroYZ);
        SmartDashboard.putData("PIDGyro", pidGyro);
    }
    
    public void reset() {
        encLeft.reset();
        encRight.reset();
        
        gyroXY.reset();
        gyroYZ.reset();
        
        pidLeft.reset();
        pidRight.reset();
        pidLeftCount.reset();
        pidRightCount.reset();
        pidGyro.reset();
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
        if(usePID) {
            reset();
        }
        usePID = false;
    }
    
    public void disablePIDCount() {
        if(usePIDCount) {
            encLeft.reset();
            encRight.reset();
            pidLeftCount.reset();
            pidLeftCount.reset();
        }
        usePIDCount = false;
    }
    
    public void disablePIDGyro() {
        if(pidGyro.isEnable()) {
            pidGyro.disable();
        }
    }

    public void enablePID() {
        if(!usePID) {
            reset();
        }
        usePID = true;
    }
    
    public void enablePIDCount() {
        if(!usePIDCount) {
            encLeft.reset();
            encRight.reset();
            pidLeftCount.reset();
            pidLeftCount.reset();
        }
        usePIDCount = true;
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
        disablePID();
        enablePIDCount();
        
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
        System.out.println("Going to count setpoint: left: " + pidLeftCount.getSetpoint() + " right: " + pidRightCount.getSetpoint());
        System.out.println("left: " + encLeft.getDistance() + " right: " + encRight.getDistance());
        
        //Make sure pidCount is the only thing using the wheels
        disablePID();
        disablePIDGyro();
        enablePIDCount();
        
        robotDrive.arcadeDrive(0.0, 0.0); //Keep robotDrive updated
        
        pidLeftCount.setInput(-encLeft.getDistance());
        pidRightCount.setInput(encRight.getDistance());

        pidLeftCount.process();
        pidRightCount.process();

        motorLeft.set(-pidLeftCount.getOutput());
        motorRight.set(pidRightCount.getOutput());
        //setSetpoint(-pidLeftCount.getOutput(), pidRightCount.getOutput(), false); //Dont allow high speed
    }
    
    public void goToAngleSetpoint() {
        //Make sure pidGyro is the only thing using the wheels
        disablePID();
        enablePIDGyro();
        disablePIDCount();
        
        if(pidGyro.onTarget()) {
            disablePIDGyro();
        } else {
            drive(0.0, pidGyro.get(), false); //Dont allow high speed
        }
    }
    
    public boolean reachedCountSetpoint() {
        //double leftError = Math.abs(pidLeftCount.getSetpoint() - encLeft.get());
        //double rightError = Math.abs(pidRightCount.getSetpoint() - encRight.get());
        //return leftError < PID_COUNT_TOLERANCE.get() && rightError < PID_COUNT_TOLERANCE.get();
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
        double leftSetpoint;
        double rightSetpoint;

        //Make sure pidLeft and pidRight are the only things using the wheels
        disablePIDGyro();
        disablePIDCount();
        
        if(USE_AUTO_TRANS && autoTrans) {
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
        
        if(usePID) {
            pidLeft.setSetpoint(leftSetpoint);
            pidRight.setSetpoint(rightSetpoint);

            pidLeft.setInput(encLeft.getRate());
            pidRight.setInput(encRight.getRate());

            pidLeft.process();
            pidRight.process();

            motorLeft.set(pidLeft.getOutput());
            motorRight.set(pidRight.getOutput());
        } else {
            pidLeft.reset();
            pidRight.reset();
            
            motorLeft.set(left);
            motorRight.set(right);
        }
    }
    
    public void print() {
        System.out.print("(Chassis Subsystem)\n");
        
        System.out.print("rate: " + (Math.abs(encLeft.getRate()) + Math.abs(encRight.getRate())) / 2 + "\n");
        System.out.print("transShift: " + transShift.get() + "\n");
        System.out.print("PIDLeftCount setpoint: " + pidLeftCount.getSetpoint() + " PIDRightCount setpoint: " + pidRightCount.getSetpoint() + "\n");
             
        System.out.print("OI Drive axis: " + CommandBase.oi.getSpeedAxis() + " OI Rotation Axis: " + CommandBase.oi.getRotationAxis() + "\n");
        
                System.out.print("PIDLeft setpoint: " + pidLeft.getSetpoint() + " PIDRight setpoint: " + pidRight.getSetpoint() + "\n");
        System.out.print("EncLeftRate: " + encLeft.getRate() + " EncRightRate: " + encRight.getRate() + "\n");
    }
}
