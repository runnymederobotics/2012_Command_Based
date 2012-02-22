package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class ShootTipCommandGroup extends CommandGroup {

    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new ShootTipCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
    
    public ShootTipCommandGroup() {
        //addSequential(new BridgeTipDownCommand());
    }
}
