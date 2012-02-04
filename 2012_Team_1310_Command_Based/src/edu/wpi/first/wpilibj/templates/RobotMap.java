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
    public static final int LEFT_MOTOR = 1;
    public static final int RIGHT_MOTOR = 2;
    public static final int SHOOTER_MOTOR = 3;
    public static final int ROLLER_MOTOR = 4;
    public static final int CAMERA_SERVO = 6;
    
    public static final int TRANS_SHIFT_FORWARD = 1;
    public static final int TRANS_SHIFT_REVERSE = 2;
    
    public static final int ENC_LEFT_A = 3;
    public static final int ENC_LEFT_B = 4;
    public static final int ENC_RIGHT_A = 1;
    public static final int ENC_RIGHT_B = 2;
    public static final int ENC_SHOOTER_A = 7;
    public static final int ENC_SHOOTER_B = 8;
    public static final int BALL_ENTER_SWITCH = 9;
    public static final int BALL_EXIT_SWITCH = 11;
    public static final int COMPRESSOR_DI = 10;
    
    public static final int GYRO_XY = 2;
    public static final int GYRO_YZ = 1;
    
    // If you are using multiple modules, make sure to define both the port
    // number and the module. For example you with a rangefinder:
    // public static final int rangefinderPort = 1;
    // public static final int rangefinderModule = 1;
}
