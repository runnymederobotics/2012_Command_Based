package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;

public class OI {
    // Process operator interface input here.
    final int DRIVER_SPEED_AXIS = 2;
    final int DRIVER_ROTATION_AXIS = 3;
    
    final int DRIVER_TOGGLE_PID = 1;
    final int DRIVER_TOGGLE_AUTO_BALANCE = 2;
    final int DRIVER_LOW_GEAR_BUTTON = 7;
    final int DRIVER_HIGH_GEAR_BUTTON = 8;
    
    final int OPERATOR_BALL_RELEASE = 1;
    final int OPERATOR_TOGGLE_BRIDGE_TIPPER = 3;
    
    final int OPERATOR_SHOOTER_MANUAL_ROLLER = 4;
    
    final int OPERATOR_TOGGLE_MANUAL_ROLLER = 10;
    final int OPERATOR_ROLLER_INCREASE_BUTTON = 8;
    final int OPERATOR_ROLLER_DECREASE_BUTTON = 12;
    
    final int OPERATOR_TOGGLE_MANUAL_TURRET = 9;
    final int OPERATOR_TURRET_INCREASE_BUTTON = 7;
    final int OPERATOR_TURRET_DECREASE_BUTTON = 11;
    final int OPERATOR_TURRET_LEFT_BUTTON = 5;
    final int OPERATOR_TURRET_RIGHT_BUTTON = 6;
    
    final double SPEED_INCREMENT = 0.01;
    
    public Joystick stickDriver = new Joystick(1);
    public Joystick stickOperator = new Joystick(2);

    Toggle manualShooterToggle = new Toggle(false);
    Toggle bridgeTipperToggle = new Toggle(false);
    Toggle manualRollerToggle = new Toggle(false);
    Toggle pidToggle = new Toggle(true);
    Toggle manualTurretToggle = new Toggle(false);
    Toggle autoBalanceToggle = new Toggle(false);

    final double AXIS_DEAD_ZONE = 0.25;
    public double getSpeedAxis() {
        double axis = stickDriver.getRawAxis(DRIVER_SPEED_AXIS);
        return Math.abs(axis) >= AXIS_DEAD_ZONE ? axis : 0.0;
    }
    
    public double getRotationAxis() {
        double axis = stickDriver.getRawAxis(DRIVER_ROTATION_AXIS);
        return Math.abs(axis) >= AXIS_DEAD_ZONE ? axis : 0.0;
    }
    
    public boolean getManualShooterToggle() {
        manualShooterToggle.feed(stickOperator.getRawButton(OPERATOR_SHOOTER_MANUAL_ROLLER));
        return manualShooterToggle.get();
    }
    
    final double THROTTLE_DEAD_ZONE = 0.1;
    public double getShooterSpeed() {
        double axis = -stickOperator.getAxis(Joystick.AxisType.kThrottle) / 2 + 0.5; //Make it between 0.0 and 1.0
        return axis >= THROTTLE_DEAD_ZONE ? axis : 0.0;
    }
    
    public boolean getLowGearButton() {
        return stickDriver.getRawButton(DRIVER_LOW_GEAR_BUTTON);
    }
    
    public boolean getHighGearButton() {
        return stickDriver.getRawButton(DRIVER_HIGH_GEAR_BUTTON);
    }
    
    public boolean getBallRelease() {
        return stickOperator.getRawButton(OPERATOR_BALL_RELEASE);
    }
    
    public boolean getBridgeTipperToggle() {
        bridgeTipperToggle.feed(stickOperator.getRawButton(OPERATOR_TOGGLE_BRIDGE_TIPPER));
        return bridgeTipperToggle.get();
    }

    double rollerSpeed = 0;
    public double getRollerSpeed() {
        if(stickOperator.getRawButton(OPERATOR_ROLLER_INCREASE_BUTTON)) {
            rollerSpeed += SPEED_INCREMENT;
        } else if(stickOperator.getRawButton(OPERATOR_ROLLER_DECREASE_BUTTON)) {
            rollerSpeed -= SPEED_INCREMENT;
        }
        
        rollerSpeed = Math.max(rollerSpeed, -1.0);
        rollerSpeed = Math.min(rollerSpeed, 1.0);
        
        return rollerSpeed;
    }
    
    public boolean getManualRollerToggle() {
        manualRollerToggle.feed(stickOperator.getRawButton(OPERATOR_TOGGLE_MANUAL_ROLLER));
        return manualRollerToggle.get();
    }
    
    double turretSpeed = 0;
    public double getTurretSpeed() {
        if(stickOperator.getRawButton(OPERATOR_TURRET_INCREASE_BUTTON)) {
            turretSpeed += SPEED_INCREMENT;
        } else if(stickOperator.getRawButton(OPERATOR_TURRET_DECREASE_BUTTON)) {
            turretSpeed -= SPEED_INCREMENT;
        }
        
        turretSpeed = Math.max(turretSpeed, 0.0);
        turretSpeed = Math.min(turretSpeed, 1.0);
        
        if(stickOperator.getRawButton(OPERATOR_TURRET_LEFT_BUTTON)) {
            return -turretSpeed;
        } else if(stickOperator.getRawButton(OPERATOR_TURRET_RIGHT_BUTTON)) {
            return turretSpeed;
        }
        
        return 0.0;
    }
    
    public boolean getManualTurretToggle() {
        manualTurretToggle.feed(stickOperator.getRawButton(OPERATOR_TOGGLE_MANUAL_TURRET));
        return manualTurretToggle.get();
    }
    
    public boolean getPIDToggle() {
        pidToggle.feed(stickDriver.getRawButton(DRIVER_TOGGLE_PID));
        return pidToggle.get();
    }
    
    public boolean getAutoBalanceToggle() {
        autoBalanceToggle.feed(stickDriver.getRawButton(DRIVER_TOGGLE_AUTO_BALANCE));
        return autoBalanceToggle.get();
    }
    
    public void print() {
        System.out.print("(Operator Interface)\n");
        
        System.out.print("ManualRoller: " + manualRollerToggle.get() + " RollerSpeed: " + rollerSpeed + "\n");
        System.out.print("ManualTurret: " + manualTurretToggle.get() + " TurretSpeed: " + turretSpeed + "\n");
    }
}

