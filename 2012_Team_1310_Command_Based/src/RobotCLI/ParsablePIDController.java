package RobotCLI;

import RobotCLI.RobotCLI.VariableContainer;
import edu.wpi.first.wpilibj.Timer;

public class ParsablePIDController extends VariableContainer {
    ParsableDouble p;
    ParsableDouble outputP;
    ParsableDouble i;
    ParsableDouble sumI;
    ParsableDouble outputI;
    ParsableDouble d;
    ParsableDouble outputD;

    ParsableDouble setpoint;
    ParsableDouble input;
    ParsableDouble error;
    ParsableDouble output;

    ParsableDouble processInterval;
    double lastProcessTime = 0;

    ParsableDouble maxOut;
    ParsableDouble minOut;
    ParsableDouble tolerance;

    public double getTime() {
        return Timer.getFPGATimestamp();
        //return (double) System.currentTimeMillis() * 0.001;
    }

    void initVariables(double p, double i, double d, double minOut, double maxOut, double tolerance) {
        this.p = this.createDouble("p", p);
        this.outputP = this.createDouble("outputP", 0);
        this.i = this.createDouble("i", i);
        this.outputI = this.createDouble("outputI", 0);
        this.sumI = this.createDouble("sumI", 0); 
        this.d = this.createDouble("d", d);
        this.outputD = this.createDouble("outputD", 0);
        this.setpoint = this.createDouble("setpoint", 0);
        this.input = this.createDouble("input", 0);
        this.error = this.createDouble("error", 0);
        this.output = this.createDouble("output", 0);
        this.processInterval = this.createDouble("processInterval", 0);
        this.maxOut = this.createDouble("maxOut", maxOut);
        this.minOut = this.createDouble("minOut", minOut);
        this.tolerance = this.createDouble("tolerance", tolerance);
        lastProcessTime = getTime();
    }

    public ParsablePIDController(String name, VariableContainer vc, double p, double i, double d, double minOut, double maxOut, double tolerance) {
        super(vc, name);
        initVariables(p, i, d, minOut, maxOut, tolerance);
    }

    public void setSetpoint(double s) {
        setpoint.set(s);
    }

    public double getSetpoint() {
        return setpoint.get();
    }

    public void setInput(double i) {
        input.set(i);
    }

    public double getOutput() {
        return output.get();
    }

    public void setOutputRange(double minOut, double maxOut) {
        this.minOut.set(minOut);
        this.maxOut.set(maxOut);
    }

    public void setTolerance(double tolerance) {
        this.tolerance.set(tolerance);
    }

    public boolean onTarget() {
        return Math.abs(error.get()) <= tolerance.get();
    }

    public void process() {
        double now = getTime();
        final double localProcessInterval = now - lastProcessTime;
        processInterval.set(localProcessInterval);
        lastProcessTime = now;

        final double localError = setpoint.get() - input.get();
        error.set(localError);

        final double localP = p.get() * localError;
        outputP.set(localP);

        final double localSumI = sumI.get() + localError * localProcessInterval;
        sumI.set(localSumI);
        final double localI = i.get() * localSumI;
        outputI.set(localI);

        final double localD = d.get() * localProcessInterval * localError;
        outputD.set(localD);

        if(!onTarget()) {
            double sumPID = localP + localI + localD;
            sumPID = Math.min(sumPID, maxOut.get());
            sumPID = Math.max(sumPID, minOut.get());
            output.set(sumPID);
        } else {
            output.set(0);
        }
    }
    
    public void reset() {
        this.outputP.set(0);
        this.outputI.set(0);
        this.sumI.set(0); 
        this.outputD.set(0);
        this.setpoint.set(0);
        this.input.set(0);
        this.error.set(0);
        this.output.set(0);
    }

    public void parseFrom(String value) {
        // TODO throw
    }

    public String toString() {
        return "{p: " + p + " i: " + i + " d: " + d + " setpoint: " + setpoint + " input: " + input
                + " error: " + error + " outputP: " + outputP + " outputI: " + outputI + " outputD: "+ outputD + " output: " + output + " onTarget: " + onTarget()+ "}";
    }
}