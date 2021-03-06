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
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.commands.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    
    Compressor compressor = new Compressor(RobotMap.COMPRESSOR_DI, 1);
    
    SendableChooser autonomousChooser = new SendableChooser();
    
    Command autonomousCommand;
    
    public interface CommandCreator {
        public abstract Command create();
    }
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        // Initialize all subsystems
        CommandBase.init();
        
        compressor.start();

        autonomousChooser.addDefault("Nothing", DriveDistanceCommand.creator(0));
        autonomousChooser.addObject("Alley-Oop", AlleyOopCommandGroup.creator());
        autonomousChooser.addObject("Shoot-Then-Tip", ShootTipCommandGroup.creator());
        autonomousChooser.addObject("Shoot-Then-Tip-Then-Shoot", ShootTipShootCommandGroup.creator());
        
        SmartDashboard.putData("Autonomous", autonomousChooser);
    }
    
    private void enableSubsystems() {
        CommandBase.chassisSubsystem.enable();
        CommandBase.elevatorSubsystem.enable();
        CommandBase.shooterSubsystem.enable();
        CommandBase.turretSubsystem.enable();
    }
    
    private void disableSubsystems() {
        CommandBase.chassisSubsystem.disable();
        CommandBase.elevatorSubsystem.disable();
        CommandBase.shooterSubsystem.disable();
        CommandBase.turretSubsystem.disable();
    }
    
    //Called when disabled mode is entered
    public void disabledInit() {
        disableSubsystems();
    }
    //Called when autonomous mode is entered
    public void autonomousInit() {
        enableSubsystems();
        
        CommandCreator creator = (CommandCreator)autonomousChooser.getSelected();
        autonomousCommand = creator.create();
        System.out.println("autonomousInit starting " + autonomousCommand);
        autonomousCommand.start();
    }
    //Called when teleop mode is entered
    public void teleopInit() {
        if(autonomousCommand != null) {
            autonomousCommand.cancel();
        }
        
        enableSubsystems();
    }

    //This function is called periodically when disabled
    public void disabledPeriodic() {
        CommandBase.elevatorSubsystem.updateDashboard();
        print("Disabled");
    }
    //This function is called periodically during autonomous
    public void autonomousPeriodic() {
        print("Autonomous");
        Scheduler.getInstance().run();
    }
    //This function is called periodically during operator control
    public void teleopPeriodic() {
        print("Teleoperated");
        Scheduler.getInstance().run();
    }
    
    public void disabledContinuous() {
    }
    
    public void autonomousContinuous() {
    }
    
    public void teleopContinuous() {
    }
    
    //Custom print function
    final double PRINT_DELAY = 0.25;
    double lastPrintTime = 0.0;
    public void print(String mode) {
        final double now = Timer.getFPGATimestamp();
        if(now - lastPrintTime > PRINT_DELAY) {
            lastPrintTime = now;
            
            //System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.print("[" + mode + "]\n");
            
            CommandBase.chassisSubsystem.print();
            System.out.print("\n");
            CommandBase.elevatorSubsystem.print();
            System.out.print("\n");
            CommandBase.shooterSubsystem.print();
            System.out.print("\n");
            CommandBase.turretSubsystem.print();
            System.out.print("\n");
            TurretCommand.print();
            System.out.print("\n");
            CommandBase.oi.print();
            System.out.print("\n");
            
            System.out.flush();
        }
    }
}
