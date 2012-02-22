package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class ShootTipShootCommandGroup extends CommandGroup {
    
    public ShootTipShootCommandGroup() {
        //addSequential(new AutonomousShootCommand());
        addSequential(new DriveDistanceCommand(-1000)); //Drive backwards
        addSequential(new RotateCommand(180)); //Rotate to face the bridge
        addSequential(new BridgeTipDownCommand()); //Tip the bridge
    }
    
    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new ShootTipCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
}
