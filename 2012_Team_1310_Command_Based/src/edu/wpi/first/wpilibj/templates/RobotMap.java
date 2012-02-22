package edu.wpi.first.wpilibj.templates;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // For example to map the left and right motors, you could define the
    // following variables to use with your drivetrain subsystem.
    public static final int LEFT_MOTOR = 2;
    public static final int RIGHT_MOTOR = 1;
    public static final int SHOOTER_MOTOR = 3;
    public static final int ELEVATOR_MOTOR = 4;
    public static final int TURRET_MOTOR = 5;
    
    public static final int TRANS_SHIFT_FORWARD = 1;
    public static final int TRANS_SHIFT_REVERSE = 2;
    public static final int BRIDGE_TIPPER_ONE = 5;
    public static final int BRIDGE_TIPPER_TWO = 6;
    public static final int ELEVATOR_BALL_RELEASE_ONE= 3;
    public static final int ELEVATOR_BALL_RELEASE_TWO = 4;
    
    public static final int ENC_LEFT_A = 5;
    public static final int ENC_LEFT_B = 6;
    public static final int ENC_RIGHT_A = 3;
    public static final int ENC_RIGHT_B = 4;
    public static final int ENC_SHOOTER_A = 11;
    public static final int ENC_SHOOTER_B = 12;
    public static final int ENC_TURRET_A = 7;
    public static final int ENC_TURRET_B = 8;
    public static final int TOP_BALL = 13;
    public static final int MIDDLE_BALL = 9;
    public static final int BOTTOM_BALL = 14;
    public static final int TURRET_LEFT_LIMIT = 1;
    public static final int TURRET_RIGHT_LIMIT = 2;
    public static final int COMPRESSOR_DI = 10;
    
    public static final int GYRO_XY = 2;
    public static final int GYRO_YZ = 1;
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static final int rangefinderPort = 1;
    // public static final int rangefinderModule = 1;
}
