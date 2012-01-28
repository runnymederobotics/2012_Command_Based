package edu.wpi.first.wpilibj.templates;

//Toggle class
public class Toggle {
    //Last value of the button
    boolean lastButtonState = false;
    //Value of the toggle
    boolean value = false;

    //Constructor takes a boolean
    public Toggle(boolean val) {
        value = val;
    }

    //Update value
    public void feed(boolean button) {
        //If the button is pressed and it wasnt last time
        if(button && !lastButtonState) {
            //Do the toggle
            value = !value;
        }
        //Set the last button state to the current button state
        lastButtonState = button;
    }

    //Return the value
    public boolean get() {
        return value;
    }

    public void set(boolean val) {
        value = val;
    }
}