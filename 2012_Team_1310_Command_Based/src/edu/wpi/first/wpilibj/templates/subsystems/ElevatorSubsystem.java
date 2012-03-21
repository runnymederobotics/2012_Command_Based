package edu.wpi.first.wpilibj.templates.subsystems;

import RobotCLI.Parsable.ParsableDouble;
import RobotCLI.RobotCLI;
import RobotCLI.RobotCLI.VariableContainer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.templates.Pneumatic;
import edu.wpi.first.wpilibj.templates.RobotMap;
import edu.wpi.first.wpilibj.templates.commands.ElevatorCommand;

public class ElevatorSubsystem extends Subsystem {
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    
    final int MAX_BALLS = 3;
    final boolean RELEASE_VALUE = true;
    
    ParsableDouble ELEVATOR_SPEED;
    ParsableDouble RELEASE_DELAY;
    ParsableDouble RECOVER_TIME; //Time for shooter to recover after a shot
    public ParsableDouble AUTONOMOUS_SHOOT_DELAY;
    public ParsableDouble AUTONOMOUS_SHOOT_TIMEOUT;
    
    Jaguar elevatorMotor = new Jaguar(RobotMap.ELEVATOR_MOTOR);
    
    DigitalInput topBall = new DigitalInput(RobotMap.TOP_BALL);
    DigitalInput middleBall = new DigitalInput(RobotMap.MIDDLE_BALL);
    DigitalInput bottomBall = new DigitalInput(RobotMap.BOTTOM_BALL);
    
    Pneumatic ballRelease = new Pneumatic(new DoubleSolenoid(RobotMap.ELEVATOR_BALL_RELEASE_ONE, RobotMap.ELEVATOR_BALL_RELEASE_TWO));
    
    NetworkTable ballCounter = NetworkTable.getTable("1310BallCounter");
    
    public ElevatorSubsystem(RobotCLI robotCLI) {
        VariableContainer vc = robotCLI.getVariables().createContainer("elevatorSubsystem");
        
        ELEVATOR_SPEED = vc.createDouble("elevatorSpeed", 0.5);
        RELEASE_DELAY = vc.createDouble("releaseDelay", 0.35);
        RECOVER_TIME = vc.createDouble("recoverTime", 0.75);
        AUTONOMOUS_SHOOT_DELAY = vc.createDouble("autonomousShootDelay", 2.0);
        AUTONOMOUS_SHOOT_TIMEOUT = vc.createDouble("autonomousShootTimeout", 2.0);
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
    
    public void updateDashboard() {
        //Update our ball counter on the dashboard
        ballCounter.beginTransaction();
        ballCounter.putBoolean("BottomBall", !bottomBall.get());
        ballCounter.putBoolean("MiddleBall", !middleBall.get());
        ballCounter.putBoolean("TopBall", !topBall.get());
        ballCounter.endTransaction();
    }
    
    public boolean getBottomBall() {
        return !bottomBall.get();
    }
    
    public boolean getMiddleBall() {
        return !middleBall.get();
    }
    
    public boolean getTopBall() {
        return !topBall.get();
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
    
    public void handleBallRelease(boolean shootRequest, boolean forceShot, boolean shooterRunning, boolean shooterOnTarget, boolean turretOnTarget) {
        updateDashboard();
        
        double now = Timer.getFPGATimestamp();
        
        //TODO: add condition for topBall being true?
        boolean allowShot = elevatorRunning() && shooterOnTarget /* && turretOnTarget */&& getTopBall();

        if(now - retractTime > RECOVER_TIME.get() && shootRequest && shooterRunning && (allowShot || forceShot)) {
            if(!releasingBall) { //If we werent previously releasing
                releaseTime = now;
                ballRelease.set(RELEASE_VALUE);
            }
            releasingBall = true;
        }
        
        if(releasingBall && now - releaseTime >= RELEASE_DELAY.get()) {
            releasingBall = false;
            retractTime = now;
        }
        
        if(!releasingBall) {
            ballRelease.set(!RELEASE_VALUE);
        }
    }
    
    public void runElevator(boolean disableRequest, boolean shootRequest, boolean reverseRequest) {
        boolean shooting = shootRequest || releasingBall;
        
        if(reverseRequest) {
            elevatorMotor.set(-ELEVATOR_SPEED.get());
        } else {
            if((hasMaxBalls() || disableRequest) && !shooting) {
                disable();
            } else {
                elevatorMotor.set(ELEVATOR_SPEED.get());
            }
        }
    }
    
    public void print() {
        System.out.print("(Elevator Subsystem)\n");
        
        System.out.print("topBall: " + !topBall.get() + " middleBall: " + !middleBall.get() + " bottomBall: " + !bottomBall.get() + "\n");
        System.out.print("elevatorMotor: " + elevatorMotor.get() + "\n");
    }
}
