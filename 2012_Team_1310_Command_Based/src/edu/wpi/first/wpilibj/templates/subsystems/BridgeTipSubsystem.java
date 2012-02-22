package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ManualBridgeTipCommand;

public class BridgeTipSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    Pneumatic bridgeTipperPneumatic = new Pneumatic(new DoubleSolenoid(RobotMap.BRIDGE_TIPPER_ONE, RobotMap.BRIDGE_TIPPER_TWO));
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ManualBridgeTipCommand());
    }
    
    public void set(boolean value) {
        bridgeTipperPneumatic.set(value);
    }
    
    public boolean get() {
        return bridgeTipperPneumatic.get();
    }
    
    public void print() {
        System.out.print("(Bridge Tipper Subsystem)\n");
        
        System.out.print("bridgeTipperPneumatic: " + bridgeTipperPneumatic.get() + "\n");
    }
}
