/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableGyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.commands.CommandBase;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    
    Compressor compressor;
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        compressor = new Compressor(10, 1); 
        compressor.start();
        
        CommandBase.chassisSubsystem.gyroXY = new SendableGyro(RobotMap.GYRO_YZ);
        CommandBase.chassisSubsystem.gyroYZ = new SendableGyro(RobotMap.GYRO_YZ);
        SmartDashboard.putData("YZGyro", CommandBase.chassisSubsystem.gyroYZ);
        
        // Initialize all subsystems
        CommandBase.init();
    }
    
    //Called when disabled mode is entered
    public void disabledInit() {
    }
    //Called when autonomous mode is entered
    public void autonomousInit() {
    }
    //Called when teleop mode is entered
    public void teleopInit() {
    }

    //This function is called periodically when disabled
    public void disabledPeriodic() {
        //print("Disabled");
    }
    //This function is called periodically during autonomous
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        print("Autonomous");
    }
    //This function is called periodically during operator control
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        print("Teleoperated");
    }
    
    //Custom print function
    final double PRINT_DELAY = 1.0;
    double lastPrintTime = 0.0;
    public void print(String mode) {
        final double now = Timer.getFPGATimestamp();
        if(now - lastPrintTime > PRINT_DELAY) {
            lastPrintTime = now;
            
            //A bunch of new lines
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

            System.out.println("[" + mode + "]");

            System.out.println("Drive Axis: " + -CommandBase.oi.stickDriver.getRawAxis(CommandBase.oi.DRIVER_SPEED_AXIS));
            
            if(CommandBase.chassisSubsystem != null) {
                System.out.println("EncLeft: " + CommandBase.chassisSubsystem.encLeft.getRate() + " EncRight: " + CommandBase.chassisSubsystem.encRight.getRate());
                System.out.println("PidLeftSetpoint: " + CommandBase.chassisSubsystem.pidLeft.getSetpoint() + " PidRightSetpoint: " + CommandBase.chassisSubsystem.pidRight.getSetpoint());
                System.out.println("PidLeftOutput: " + CommandBase.chassisSubsystem.pidLeft.get() + " PidRightOutput: " + CommandBase.chassisSubsystem.pidRight.get());
            }
            System.out.println("GyroYZ Angle: " + CommandBase.chassisSubsystem.gyroYZ.getAngle());
            //if(CommandBase.cameraSubsystem != null && CommandBase.cameraSubsystem.cameraServo != null)
            //    System.out.println("Servo angle: " + (CommandBase.cameraSubsystem.cameraServo.getPosition() * 180));
        }
    }
}
