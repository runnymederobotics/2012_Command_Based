package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/*
 * Camera Network Table Format
 * boolean foundTarget
 * double targetAngle
 * double targetDistance
 */

public class CameraSystem {
    static NetworkTable networkTable;
    
    //static Servo cameraServo;
    //static final int CAMERA_MAX_ANGLE = 180;

    public static void init() {
        if(networkTable == null) {
            networkTable = NetworkTable.getTable("Camera1310");
        }
    }
    
    public static boolean canSeeTarget() {
        try {
            init();
            return networkTable.getBoolean("FoundTarget");
        } catch(Exception e) {
            return false;
        }
    }
    
    private static boolean getNetworkField(String field, int[] readerSequenceNumber, double[] value, boolean[] fresh) {
        boolean foundTarget;
        value[0] = 0;
        fresh[0] = false;
        int currentSequenceNumber;
        try {
            init();
            foundTarget = networkTable.getBoolean("FoundTarget");
            value[0] = networkTable.getDouble(field);
            currentSequenceNumber = networkTable.getInt("SequenceNumber");
        } catch(Exception e) {
            return false;
        }
        
        if(readerSequenceNumber[0] != currentSequenceNumber) {
            fresh[0] = true;
        }
        readerSequenceNumber[0] = currentSequenceNumber;

        return foundTarget;
    }
    
    public static boolean getTargetDistance(int[] readerSequenceNumber, double[] distance, boolean[] fresh) {
        return getNetworkField("TargetDistance", readerSequenceNumber, distance, fresh);
    }
    
    public static boolean getTargetAngle(int[] readerSequenceNumber, double[] angle, boolean[] fresh) {
        return getNetworkField("TargetAngle", readerSequenceNumber, angle, fresh);
    }
    
    /*public double getServoAngle() {
        return cameraServo.getAngle();
        //return cameraServo.getPosition() * CAMERA_MAX_ANGLE;
    }
    
    public double getNormalizedServoAngle() {
        return (cameraServo.get() - 0.5) * 2;
    }

    public void setServoAngle(double angle) {
        cameraServo.setAngle(angle);
    }*/
}