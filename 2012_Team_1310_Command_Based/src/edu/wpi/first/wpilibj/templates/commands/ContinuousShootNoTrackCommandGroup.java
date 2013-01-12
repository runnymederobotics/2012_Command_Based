package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class ContinuousShootNoTrackCommandGroup extends CommandGroup {
    
    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new ContinuousShootNoTrackCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
    
    public ContinuousShootNoTrackCommandGroup() {
        final boolean neverStop = true;
        final boolean trackTarget = false;
        
        addParallel(new TurretCommand(trackTarget));
        addSequential(new AutonomousShootCommand(neverStop, trackTarget));
        
    }
}
