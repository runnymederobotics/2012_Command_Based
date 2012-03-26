package edu.wpi.first.wpilibj.templates.subsystems;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.RobotCLI;
import RobotCLI.RobotCLI.VariableContainer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ManualBridgeTipCommand;

public class BridgeTipSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    ParsableDouble LOCK_DELAY;
    
    ParsableDouble LOCK_RIGHT;
    ParsableDouble UNLOCK_RIGHT;
    
    ParsableDouble LOCK_LEFT;
    ParsableDouble UNLOCK_LEFT;
    
    Pneumatic bridgeTipperPneumatic = new Pneumatic(new DoubleSolenoid(RobotMap.BRIDGE_TIPPER_ONE, RobotMap.BRIDGE_TIPPER_TWO));
    
    Servo rightLock = new Servo(RobotMap.BRIDGE_TIP_LOCK_RIGHT);
    Servo leftLock = new Servo(RobotMap.BRIDGE_TIP_LOCK_LEFT);
    
    double rightLockPosition = 0, leftLockPosition = 0;
    double lastSetTime = 0;
    
    public BridgeTipSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("bridgeTipSubsystem");
        LOCK_DELAY = vc.createDouble("lockDelay", 1.0);
        
        LOCK_RIGHT = vc.createDouble("lockRight", 1.0);
        UNLOCK_RIGHT = vc.createDouble("unlockRight", 0.71);
        LOCK_LEFT = vc.createDouble("lockLeft", 0.0);
        UNLOCK_LEFT = vc.createDouble("unlockLeft", 0.34);
    }
    
    public void disable() {
        
    }
    
    public void enable() {
        rightLock.set(UNLOCK_RIGHT.get());
        leftLock.set(UNLOCK_LEFT.get());
        
        rightLockPosition = UNLOCK_RIGHT.get();
        leftLockPosition = UNLOCK_LEFT.get();
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ManualBridgeTipCommand());
    }
    
    public void set(boolean override, boolean requestValue) {
        if(override) {
            bridgeTipperPneumatic.set(requestValue);
        } else {
            double now = Timer.getFPGATimestamp();

            boolean actualValue = bridgeTipperPneumatic.get();

            if(!actualValue && requestValue) {
                bridgeTipperPneumatic.set(true); //We are up and we want to go down
                if(lastSetTime == 0) {
                    lastSetTime = now;
                }
            } else if(actualValue && !requestValue) {
                rightLockPosition = UNLOCK_RIGHT.get(); //We are down and we want to go up
                leftLockPosition = UNLOCK_LEFT.get();
                if(lastSetTime == 0) {
                    lastSetTime = now;
                }
            }

            if(lastSetTime != 0 && now - lastSetTime > LOCK_DELAY.get()) {
                lastSetTime = 0; //Reset lastSetTime
                if(actualValue && requestValue) {
                    rightLockPosition = LOCK_RIGHT.get(); //We went down so lock in
                    leftLockPosition = LOCK_LEFT.get();
                } else {
                    bridgeTipperPneumatic.set(false); //We wanted to go up
                }
            }
            
            rightLock.setPosition(rightLockPosition);
            leftLock.setPosition(leftLockPosition);
        }
    }
    
    public boolean get() {
        return bridgeTipperPneumatic.get();
    }
    
    public void print() {
        System.out.print("(Bridge Tipper Subsystem)\n");
        
        System.out.print("rightServoRaw: " + rightLock.getPosition() + " leftServoRaw: " + leftLock.getPosition() + "\n");
        System.out.print("rightServoAngle: " + rightLock.getAngle() + " leftServoAngle: " + leftLock.getAngle() + "\n");
        System.out.print("bridgeTipperPneumatic: " + bridgeTipperPneumatic.get() + "\n");
    }
}
