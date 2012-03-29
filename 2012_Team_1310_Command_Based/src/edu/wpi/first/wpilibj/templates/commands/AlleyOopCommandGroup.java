package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class AlleyOopCommandGroup extends CommandGroup {
    
    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new AlleyOopCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
    
    public AlleyOopCommandGroup() {
        //Default Commands
        //Contantly run the shooter
        //Constantly track the target
        //Constantly run the elevator
        
        final int driveUpDistance = -400;
        
        addSequential(new DriveDistanceCommand(driveUpDistance));
        addSequential(new AutonomousShootCommand(false, true));
    }
}
