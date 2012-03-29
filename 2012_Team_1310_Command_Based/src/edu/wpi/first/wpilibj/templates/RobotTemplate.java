/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import RobotCLI.RobotCLI;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.commands.*;
import java.io.IOException;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    
    private RobotCLI robotCLI;
    
    Compressor compressor = new Compressor(RobotMap.COMPRESSOR_DI, RobotMap.COMPRESSOR_RELAY);
    
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
        try {
            if(robotCLI == null) {
                robotCLI = new RobotCLI("1310", 10000);
            }
        } catch (IOException ex) {
        }
        
        // Initialize all subsystems
        CommandBase.init(robotCLI);

        compressor.start();
        
        autonomousChooser.addDefault("Nothing", DoNothingCommandGroup.creator());
        autonomousChooser.addObject("Continuous-Shoot", ContinuousShootCommandGroup.creator());
        autonomousChooser.addObject("Continuous-Shoot-No-Track", ContinuousShootNoTrackCommandGroup.creator());
        autonomousChooser.addObject("Alley-Oop", AlleyOopCommandGroup.creator());
        autonomousChooser.addObject("Shoot-Then-Tip", ShootTipCommandGroup.creator());
        autonomousChooser.addObject("Shoot-Then-Tip-Then-Shoot", ShootTipShootCommandGroup.creator());
        autonomousChooser.addObject("Alley-Oop-Shoot-Tip-Shoot", AlleyOopShootTipShootCommandGroup.creator());
        autonomousChooser.addObject("Tip-Then-Shoot", TipShootCommandGroup.creator());
        
        SmartDashboard.putData("Autonomous", autonomousChooser);
    }
    
    private void enableSubsystems() {
        CommandBase.bridgeTipSubsystem.enable();
        CommandBase.chassisSubsystem.enable();
        CommandBase.elevatorSubsystem.enable();
        CommandBase.shooterSubsystem.enable();
        CommandBase.turretSubsystem.enable();
    }
    
    private void disableSubsystems() {
        CommandBase.bridgeTipSubsystem.disable();
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
        CommandCreator creator = (CommandCreator)autonomousChooser.getSelected();
        autonomousCommand = creator.create();
        if(!autonomousCommand.getName().equals("DoNothingCommandGroup")) {
            enableSubsystems();
            System.out.println("autonomousInit starting " + autonomousCommand);
            autonomousCommand.start();
        }
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
        //print("Disabled");
    }
    //This function is called periodically during autonomous
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        //print("Autonomous");
    }
    //This function is called periodically during operator control
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        print("Teleoperated");
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
            
            //CommandBase.chassisSubsystem.print();
            //System.out.print("\n");
            //CommandBase.elevatorSubsystem.print();
            //System.out.print("\n");
            //CommandBase.shooterSubsystem.print();
            //System.out.print("\n");
            CommandBase.turretSubsystem.print();
            System.out.print("\n");
            //CommandBase.bridgeTipSubsystem.print();
            System.out.print("\n");
            TurretCommand.print();
            System.out.print("\n");
            //CommandBase.oi.print();
            //System.out.print("\n");
            
            System.out.flush();
        }
    }
}
