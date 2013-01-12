package edu.wpi.first.wpilibj.templates.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

public class TipShootCommandGroup extends CommandGroup {
    static class Creator implements RobotTemplate.CommandCreator {
        public Command create() {
            return new TipShootCommandGroup();
        }
    }
    
    public static RobotTemplate.CommandCreator creator() {
        return new Creator();
    }
    
    public TipShootCommandGroup() {
        //Default Commands
        //Contantly run the shooter
        //Constantly track the target
        //Constantly run the elevator
        
        final int keyWidth = 800;
        final int driveToBridgeDistance = 2000 - keyWidth;
        final int driveToKeyDistance = -(driveToBridgeDistance + keyWidth + 400); //Drive back to where we started + keyWidth + drive a bit closer
        
        addParallel(new AutonomousBridgeTipCommand(true)); //Lower the bridge tipper
        addSequential(new DriveDistanceCommand(driveToBridgeDistance)); //Drive to the bridge
        addParallel(new AutonomousBridgeTipCommand(false)); //Raise the bridge tipper
        addSequential(new WaitCommand(3)); //Wait for 3 seconds so balls come to our side
        addSequential(new DriveDistanceCommand(driveToKeyDistance)); //Drive to top of key
        addSequential(new AutonomousShootCommand(false, true)); //Stop when we have no balls and trackTarget
    }
}
