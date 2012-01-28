package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.parsing.IDeviceController;

//A storage class to hold the output for a speed controller
public class OutputStorage implements SpeedController, IDeviceController {
    //Necessary overrides
    public void disable() {
    }

    public void set(double val) {
        value = val;
    }

    public void set(double val, byte i) {
        value = val;
    }

    public void pidWrite() {
    }

    public void pidWrite(double val) {
    }

    public double get() {
        return value;
    }

    double value = 0;
};
