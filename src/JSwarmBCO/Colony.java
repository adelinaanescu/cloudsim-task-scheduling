package JSwarmBCO;

import java.util.ArrayList;
import java.util.List;

public class Colony {

    private double bestFitness;
    private int bestBeeIndex;
    private double[] bestPosition;
    private FitnessFunction fitnessFunction;
    private List<Bee> bees;
    private Bee sampleBee;

    public Colony(int numberOfBees, Bee sampleBee, FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
        this.sampleBee = sampleBee;
        bees = new ArrayList<>(numberOfBees);
        bestFitness = Double.NaN;
        bestBeeIndex = -1;
    }

    public void evaluate() {
        if (bees.isEmpty())
            throw new RuntimeException("No bees in this colony! Maybe you need to call BCO.Colony.init() method");
        if (fitnessFunction == null)
            throw new RuntimeException("No fitness function in this colony! Maybe you need to call BCO.Colony.setFitnessFunction() method");

        // Initialize
        if (Double.isNaN(bestFitness)) {
            bestFitness = fitnessFunction.isMaximize() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            bestBeeIndex = -1;
        }

        // Evaluate each bee (and find the 'best' one)
        for (int i = 0; i < bees.size(); i++) {
            double fit = fitnessFunction.evaluate(bees.get(i).getPosition());

            // Update 'best colony' position
            if (fitnessFunction.isBetterThan(bestFitness, fit)) {
                bestFitness = fit;
                bestBeeIndex = i;
                if (bestPosition == null) bestPosition = new double[sampleBee.getDimension()];
                bees.get(bestBeeIndex).copyPosition();
            }
        }
    }

    //public abstract void update();

    public double[] getBestPosition() {
        return bestPosition;
    }

    public double getBestFitness() {
        return bestFitness;
    }
}
