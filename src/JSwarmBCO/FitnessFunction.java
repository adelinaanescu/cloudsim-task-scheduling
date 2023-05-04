package JSwarmBCO;

import net.sourceforge.jswarm_pso.Particle;

public abstract class FitnessFunction {
    /** Should this funtion be maximized or minimized */
    boolean maximize;

    /**
     * Constructor
     * @param maximize : Should we try to maximize or minimize this function?
     */
    public FitnessFunction(boolean maximize) {
        this.maximize = maximize;
    }

    /**
     * Evaluates a particles at a given position
     * NOTE: You should write your own method!
     *
     * @param position : Particle's position
     * @return Fitness function for a particle
     */
    public abstract double evaluate(double position[]);

    public double evaluate(Bee bee) {
        double position[] = bee.getPosition();
        double fit = evaluate(position);
        bee.setFitness(fit, maximize);
        return fit;
    }
    public boolean isBetterThan(double fitness, double otherValue) {
        if (maximize) {
            if (otherValue > fitness) return true;
        } else {
            if (otherValue < fitness) return true;
        }
        return false;
    }
    /** Are we maximizing this fitness function? */
    public boolean isMaximize() {
        return maximize;
    }

    public void setMaximize(boolean maximize) {
        this.maximize = maximize;
    }


}
