package JSwarmBCO;

public abstract class Optimizer {

    private Colony colony;
    private FitnessFunction fitnessFunction;
    private int maxIterations;

    public Optimizer(Colony colony, FitnessFunction fitnessFunction, int maxIterations) {
        this.colony = colony;
        this.fitnessFunction = fitnessFunction;
        this.maxIterations = maxIterations;
    }

    public abstract void optimize();

    // getters and setters
    public Colony getColony() {
        return colony;
    }

    public void setColony(Colony colony) {
        this.colony = colony;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
