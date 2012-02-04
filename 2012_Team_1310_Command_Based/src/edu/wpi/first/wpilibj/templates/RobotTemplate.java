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
    
    Compressor compressor;
    
    SendableChooser autonomousChooser;
    
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
        
        compressor = new Compressor(RobotMap.COMPRESSOR_DI, 1); 
        compressor.start();
        
        autonomousChooser = new SendableChooser();
        autonomousChooser.addDefault("Nothing", DriveDistanceCommand.creator(0));
        autonomousChooser.addObject("Alley-Oop", AlleyOopCommandGroup.creator());
        autonomousChooser.addObject("Shoot-Then-Tip", ShootTipCommandGroup.creator());
        autonomousChooser.addObject("Shoot-Then-Tip-Then-Shoot", ShootTipShootCommandGroup.creator());
        
        SmartDashboard.putData("Autonomous", autonomousChooser);
    }
    
    //Called when disabled mode is entered
    public void disabledInit() {
    }
    //Called when autonomous mode is entered
    public void autonomousInit() {
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
    final double PRINT_DELAY = 0.125;
    double lastPrintTime = 0.0;
    public void print(String mode) {
        final double now = Timer.getFPGATimestamp();
        if(now - lastPrintTime > PRINT_DELAY) {
            lastPrintTime = now;
            
            //A bunch of new lines
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

            System.out.println("[" + mode + "]");
            
            CommandBase.chassisSubsystem.print();
            
            System.out.println();
            
            CommandBase.elevatorSubsystem.print();
            
            System.out.println();
            
            CommandBase.shooterSubsystem.print();
        }
    }
}
