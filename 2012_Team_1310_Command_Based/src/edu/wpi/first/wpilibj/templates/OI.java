package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;

public class OI {
    // Process operator interface input here.
    final int DRIVER_SPEED_AXIS = 2;
    final int DRIVER_ROTATION_AXIS = 3;
    
    final int DRIVER_TOGGLE_PID = 1;
    final int DRIVER_TOGGLE_FOLLOW_TARGET = 5;
    final int DRIVER_TOGGLE_AUTO_BALANCE = 7;
    final int DRIVER_TRANS_SHIFT_BUTTON = 8;
    
    public Joystick stickDriver = new Joystick(1);
    public Joystick stickOperator = new Joystick(2);
    
    public Toggle pidToggle = new Toggle(true);
    public Toggle followTargetToggle = new Toggle(false);
    public Toggle autoBalanceToggle = new Toggle(false);

    final double AXIS_DEAD_ZONE = 0.25;
    public double getSpeedAxis() {
        double axis = stickDriver.getRawAxis(DRIVER_SPEED_AXIS);
        return Math.abs(axis) >= AXIS_DEAD_ZONE ? axis : 0.0;
    }
    
    public double getRotationAxis() {
        double axis = stickDriver.getRawAxis(DRIVER_ROTATION_AXIS);
        return Math.abs(axis) >= AXIS_DEAD_ZONE ? axis : 0.0;
    }
    
    public boolean getTransShiftButton() {
        return stickDriver.getRawButton(DRIVER_TRANS_SHIFT_BUTTON);
    }
    
    public boolean getPIDToggle() {
        pidToggle.feed(stickDriver.getRawButton(DRIVER_TOGGLE_PID));
        return pidToggle.get();
    }
    
    public boolean getFollowTargetToggle() {
        followTargetToggle.feed(stickDriver.getRawButton(DRIVER_TOGGLE_FOLLOW_TARGET));
        return followTargetToggle.get();
    }
    
    public boolean getAutoBalanceToggle() {
        autoBalanceToggle.feed(stickDriver.getRawButton(DRIVER_TOGGLE_AUTO_BALANCE));
        return autoBalanceToggle.get();
    }
}

