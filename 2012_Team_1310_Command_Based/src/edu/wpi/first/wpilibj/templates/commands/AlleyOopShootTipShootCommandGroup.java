package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class AlleyOopShootTipShootCommandGroup extends CommandGroup {
    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new AlleyOopShootTipShootCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
    
    public AlleyOopShootTipShootCommandGroup() {
        //Default Commands
        //Contantly run the shooter
        //Constantly track the target
        //Constantly run the elevator
        
        final int moveUpDistance = -400;
        final int moveBackDistance = 2000;
        
        addSequential(new DriveDistanceCommand(moveUpDistance)); //Drive closer
        addSequential(new AutonomousShootCommand(false, true)); //Stop when we have no balls and trackTarget
        addParallel(new AutonomousBridgeTipCommand(true)); //Lower the bridge tipper
        addSequential(new DriveDistanceCommand(moveBackDistance - moveUpDistance)); //Drive towards the bridge (a condition for this stopping is if the YZ gyro has a reading)
        addParallel(new AutonomousBridgeTipCommand(false)); //Raise the bridge tipper
        addSequential(new WaitForBallCommand()); //Wait for balls
        addSequential(new DriveDistanceCommand(-moveBackDistance)); //Move back
        addSequential(new AutonomousShootCommand(false, true)); //Shoot all our balls
    }
}
