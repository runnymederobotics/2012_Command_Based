package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkListener;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ElevatorCommand;

public class ElevatorSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    final int MAX_BALLS = 3;
    double ELEVATOR_SPEED = 0.5;
    double RELEASE_DELAY = 0.35;
    double RECOVER_TIME = 0.75; //Time for shooter to recover after a shot
    final double BALL_SENSOR_SEND_DELAY = 0.25;
    final boolean RELEASE_VALUE = true;
    
    Jaguar elevatorMotor = new Jaguar(RobotMap.ELEVATOR_MOTOR);
    
    DigitalInput topBall = new DigitalInput(RobotMap.TOP_BALL);
    DigitalInput middleBall = new DigitalInput(RobotMap.MIDDLE_BALL);
    DigitalInput bottomBall = new DigitalInput(RobotMap.BOTTOM_BALL);
    
    Pneumatic ballRelease = new Pneumatic(new DoubleSolenoid(RobotMap.ELEVATOR_BALL_RELEASE_ONE, RobotMap.ELEVATOR_BALL_RELEASE_TWO));
    
    NetworkTable networkTable = NetworkTable.getTable("1310BallCounter");
    
    public ElevatorSubsystem() {
        NetworkTable.getTable("Settings1310").addListenerToAll(new NetworkListener() {

            public void valueChanged(String key, Object value) {
                if("ElevatorSpeed".equals(key)) {
                    ELEVATOR_SPEED = Double.parseDouble((String)value);
                    System.out.println("changed ELEVATOR_SPEED to " + (String)value);
                } else if("RecoverTime".equals(key)) {
                    RECOVER_TIME = Double.parseDouble((String)value);
                    System.out.println("changed RECOVER_TIME to " + (String)value);
                } else if("ReleaseDelay".equals(key)) {
                    RELEASE_DELAY = Double.parseDouble((String)value);
                    System.out.println("changed RELEASE_DELAY to " + (String)value);
                }
            }

            public void valueConfirmed(String key, Object value) {
            }
        });
    }
    
    public void disable() {
        elevatorMotor.set(0.0);
    }
    
    public void enable() {
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ElevatorCommand());
    }
    
    public boolean hasMaxBalls() {
        return !topBall.get() && !middleBall.get() && !bottomBall.get();
    }
    
    public boolean hasBalls() {
        return !topBall.get() || !middleBall.get() || !bottomBall.get();
    }
    
    public boolean elevatorRunning() {
        return elevatorMotor.get() != 0.0;
    }
    
    boolean releasingBall = false;
    double releaseTime = 0.0;
    double retractTime = 0.0;
    
    public void handleBallRelease(boolean shootRequest, boolean shooterRunning) {
        updateDashboard();
        
        double now = Timer.getFPGATimestamp();
        
        //TODO: add condition for topBall being true?
        //TODO: add condition for shooter to be "on target" instead of waiting a specific time?
        if(now - retractTime > RECOVER_TIME && shootRequest && elevatorRunning() && shooterRunning) {
            if(!releasingBall) { //If we werent previously releasing
                releaseTime = now;
                ballRelease.set(RELEASE_VALUE);
            }
            releasingBall = true;
        }
        
        //We've waited the amount of time for the ball to be released
        if(releasingBall && now - releaseTime >= RELEASE_DELAY) {
            releasingBall = false;
            retractTime = now;
        }
        
        //Close the ball release
        if(!releasingBall) {
            ballRelease.set(!RELEASE_VALUE);
        }
    }
    
    public void runRoller(boolean disableRequest, boolean shootRequest) {
        //We may be shooting even if we arent pressing the button
        boolean shooting = shootRequest || releasingBall;
        
        if((hasMaxBalls() || disableRequest) && !shooting) {
            disable();
        } else {
            elevatorMotor.set(ELEVATOR_SPEED);
        }
    }
    
    public void print() {
        System.out.print("(Elevator Subsystem)\n");
        
        System.out.print("topBall: " + !topBall.get() + " middleBall: " + !middleBall.get() + " bottomBall: " + !bottomBall.get() + "\n");
        System.out.print("elevatorMotor: " + elevatorMotor.get() + "\n");
    }
    
    boolean lastBottom = false;
    boolean lastMiddle = false;
    boolean lastTop = false;
    double lastBallSensorSendTime = 0.0;
    public void updateDashboard() {
        /*double now = Timer.getFPGATimestamp();
        if(now - lastBallSensorSendTime > BALL_SENSOR_SEND_DELAY) {
            
            //Update our ball counter on the dashboard
            networkTable.beginTransaction();
            networkTable.putBoolean("BottomBall", !bottomBall.get());
            networkTable.putBoolean("MiddleBall", !middleBall.get());
            networkTable.putBoolean("TopBall", !topBall.get());
            networkTable.endTransaction();
            
            lastBallSensorSendTime = now;
        }*/
    }
}
