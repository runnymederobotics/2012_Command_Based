package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class AlleyOopCommandGroup extends CommandGroup {
    
    public AlleyOopCommandGroup() {
        addSequential(new DriveDistanceCommand(1000));
        addSequential(new DriveDistanceCommand(-1000));
    }
    
    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new AlleyOopCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
}
