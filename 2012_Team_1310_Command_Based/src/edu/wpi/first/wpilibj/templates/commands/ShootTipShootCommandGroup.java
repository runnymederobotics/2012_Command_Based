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
        addSequential(new AutonomousShootCommand(false, true)); //Shoot all our balls
        addSequential(new BridgeTipDownCommand()); //Lower the bridge tipper
        addSequential(new DriveDistanceCommand(1000)); //Drive towards the bridge (a condition for this stopping is if the YZ gyro has a reading)
        addSequential(new WaitForBallCommand());
        addSequential(new DriveDistanceCommand(-1000));
        addSequential(new AutonomousShootCommand(false, true)); //Shoot all our balls
    }
}
