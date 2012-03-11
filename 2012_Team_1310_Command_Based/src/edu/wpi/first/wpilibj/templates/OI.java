package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.templates.subsystems.TurretSubsystem;

public class OI {
    // Process operator interface input here.
    final int DRIVER_SPEED_AXIS = 2;
    final int DRIVER_ROTATION_AXIS = 3;
    
    final int DRIVER_TOGGLE_PID = 1;
    final int DRIVER_TOGGLE_AUTO_BALANCE = 2;
    final int DRIVER_LOW_GEAR_BUTTON = 7;
    final int DRIVER_HIGH_GEAR_BUTTON = 8;
    final int DRIVER_TOGGLE_BRIDGE_TIPPER = 6;
    
    final int OPERATOR_BALL_RELEASE = 1;
    final int OPERATOR_FORCE_SHOT = 2;
    
    final int OPERATOR_TOGGLE_MANUAL_SHOOTER = 4;
    
    final int OPERATOR_TOGGLE_MANUAL_ELEVATOR = 10;
    final int OPERATOR_REVERSE_ELEVATOR_BUTTON = 12;
    
    final int OPERATOR_TOGGLE_MANUAL_TURRET = 9;
    final int OPERATOR_TURRET_INCREASE_BUTTON = 7;
    final int OPERATOR_TURRET_DECREASE_BUTTON = 11;
    final int OPERATOR_TURRET_LEFT_BUTTON = 5;
    final int OPERATOR_TURRET_RIGHT_BUTTON = 6;
    
    public Joystick stickDriver = new Joystick(1);
    public Joystick stickOperator = new Joystick(2);

    Toggle pidToggle = new Toggle(true);
    Toggle autoBalanceToggle = new Toggle(false);
    
    Toggle manualShooterToggle = new Toggle(false);
    Toggle enableElevatorToggle = new Toggle(true);
    Toggle manualTurretToggle = new Toggle(false);
    

    /*        DRIVER        */
    
    final double AXIS_DEAD_ZONE = 0.25;
    public double getSpeedAxis() {
        double axis = stickDriver.getRawAxis(DRIVER_SPEED_AXIS);
        return Math.abs(axis) >= AXIS_DEAD_ZONE ? axis : 0.0;
    }
    
    public double getRotationAxis() {
        double axis = stickDriver.getRawAxis(DRIVER_ROTATION_AXIS);
        return Math.abs(axis) >= AXIS_DEAD_ZONE ? axis : 0.0;
    }
    
    public boolean getLowGearButton() {
        return stickDriver.getRawButton(DRIVER_LOW_GEAR_BUTTON);
    }
    
    public boolean getHighGearButton() {
        return stickDriver.getRawButton(DRIVER_HIGH_GEAR_BUTTON);
    }
    
    public boolean getPIDToggle() {
        pidToggle.feed(stickDriver.getRawButton(DRIVER_TOGGLE_PID));
        return pidToggle.get();
    }
    
    public boolean getAutoBalanceToggle() {
        autoBalanceToggle.feed(stickDriver.getRawButton(DRIVER_TOGGLE_AUTO_BALANCE));
        return autoBalanceToggle.get();
    }
    
    public boolean getBridgeTipperToggle() {
        return stickDriver.getRawButton(DRIVER_TOGGLE_BRIDGE_TIPPER);
    }
    
    /*        OPERATOR        */
    
    public boolean getManualShooterToggle() {
        manualShooterToggle.feed(stickOperator.getRawButton(OPERATOR_TOGGLE_MANUAL_SHOOTER));
        return manualShooterToggle.get();
    }
    
    final double THROTTLE_DEAD_ZONE = 0.1;
    public double getManualShooterSpeed() {
        double axis = -stickOperator.getAxis(Joystick.AxisType.kThrottle) / 2 + 0.5; //Make it between 0.0 and 1.0
        return axis >= THROTTLE_DEAD_ZONE ? axis : 0.0;
    }

    public boolean getEnableElevatorToggle() {
        enableElevatorToggle.feed(stickOperator.getRawButton(OPERATOR_TOGGLE_MANUAL_ELEVATOR));
        return enableElevatorToggle.get();
    }
    
    public boolean getReverseElevator() {
        return stickOperator.getRawButton(OPERATOR_REVERSE_ELEVATOR_BUTTON);
    }
    
    public boolean getBallRelease() {
        return stickOperator.getRawButton(OPERATOR_BALL_RELEASE);
    }
    
    public boolean getForceShot() {
        return stickOperator.getRawButton(OPERATOR_FORCE_SHOT);
    }
    
    public boolean getManualTurretToggle() {
        manualTurretToggle.feed(stickOperator.getRawButton(OPERATOR_TOGGLE_MANUAL_TURRET));
        return manualTurretToggle.get();
    }
    
    public int getManualTurretDirection() {
        if(stickOperator.getRawButton(OPERATOR_TURRET_LEFT_BUTTON)) {
            return TurretSubsystem.SEARCH_LEFT;
        } else if(stickOperator.getRawButton(OPERATOR_TURRET_RIGHT_BUTTON)) {
            return TurretSubsystem.SEARCH_RIGHT;
        }
        
        return 0;
    }
    
    public void print() {
        System.out.print("(Operator Interface)\n");
        
        System.out.print("ManualShooter: " + manualShooterToggle.get() + " ManualShooterSpeed: " + getManualShooterSpeed() + "\n");
    }
}

