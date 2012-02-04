package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;

public class CountEncoder implements PIDSource {
    Encoder encoder;
    
    boolean reverse;
    
    public CountEncoder(Encoder encoder, boolean reverse) {
        this.encoder = encoder;
        this.reverse = reverse;
    }
    
    public CountEncoder(Encoder encoder) {
        this.encoder = encoder;
        this.reverse = false;
    }
    
    public double pidGet() {
        return reverse ? -encoder.get() : encoder.get();
    }
}
