package JSwarmBCO;

public abstract class FitnessFunction {

    //Shoud the  function be maxime or minimized
    boolean maximize;

    public FitnessFunction() {
        maximize = false; // Default: Maximize
    }
    public FitnessFunction(boolean maximize) {
        this.maximize = maximize;
    }


    public abstract double evaluate(double[] position);
    public double evaluate(Bee bee) {
        double[] position = bee.getPosition();
        double fit = evaluate(position);
        bee.setFitness(fit, maximize);
        return fit;
    }

    public boolean isBetterThan(double fitness1, double fitness2) {
        return (maximize ? fitness1 > fitness2 : fitness1 < fitness2);
    }

    public boolean isMaximize() {
        return maximize;
    }
    public void setMaximize(boolean maximize) {
        this.maximize = maximize;
    }







}
