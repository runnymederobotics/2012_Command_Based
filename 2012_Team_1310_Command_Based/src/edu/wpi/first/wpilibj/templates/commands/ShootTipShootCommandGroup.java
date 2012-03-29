package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class ShootTipShootCommandGroup extends CommandGroup {
    
    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new ShootTipShootCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
    
    public ShootTipShootCommandGroup() {
        //Default Commands
        //Contantly run the shooter
        //Constantly track the target
        //Constantly run the elevator
        
        addSequential(new AutonomousShootCommand(false, true)); //Shoot all our balls
        addParallel(new AutonomousBridgeTipCommand(true)); //Lower the bridge tipper
        addSequential(new DriveDistanceCommand(1800)); //Drive towards the bridge (a condition for this stopping is if the YZ gyro has a reading)
        addParallel(new AutonomousBridgeTipCommand(false)); //Raise the bridge tipper
        addSequential(new WaitForBallCommand());
        addSequential(new DriveDistanceCommand(-1800));
        addSequential(new AutonomousShootCommand(false, true)); //Shoot all our balls
    }
}
