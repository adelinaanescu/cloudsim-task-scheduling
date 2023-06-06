package JSwarmBCO;

import utils.Role;

import java.util.*;

public class Colony implements Iterable<Bee> {

        private static final double DEFAULT_EXPLORATION_FACTOR = 0.8;

        // Best fitness so far
        double bestFitness;
        // Index of best bee so far
        int bestBeeIndex;
        // Best position so far
        double[] bestPosition;

        // Fitness function for this colony
        FitnessFunction fitnessFunction;

    // Exploration factor for the bee's neighborhood
        double explorationFactor;
        // Maximum and Minimum positions
        double[] maxPosition;
        double[] minPosition;
        // Number of times 'bee.evaluate()' has been called
        int numberOfEvaluations;
        // Number of bees in this colony
        int numberOfBees;
        // Bees in this colony
        Bee[] bees;
        // Bee update strategy
        BeeUpdate beeUpdate;
        // A sample bee: Build other bees based on this one
        Bee sampleBee;
        // A collection used for 'Iterable' interface
        ArrayList<Bee> beesList;

    /**
     * Create a Colony and set default values
     * @param numberOfBees : Number of bees in this colony (should be greater than 0).
     * @param sampleBee : A bee that is a sample to build all other bees
     * @param fitnessFunction : Fitness function used to evaluate each bee
     */
    public Colony(int numberOfBees, Bee sampleBee, FitnessFunction fitnessFunction) {
        if (sampleBee == null) throw new RuntimeException("Sample bee can't be null!");
        if (numberOfBees <= 0) throw new RuntimeException("Number of bees should be greater than zero.");

        explorationFactor = DEFAULT_EXPLORATION_FACTOR;
        numberOfEvaluations = 0;
        this.numberOfBees = numberOfBees;
        this.sampleBee = sampleBee;
        this.fitnessFunction = fitnessFunction;
        bestFitness = Double.NaN;
        bestBeeIndex = -1;

        // Set up bee update strategy (default: BeeUpdateSimple)
        beeUpdate = new BeeUpdateSimple(sampleBee);

        beesList = new ArrayList<>();
    }

    public void evaluate() {
        if (bees == null) throw new RuntimeException("No bees in this colony! May be you need to call Colony.init() method");
        if (fitnessFunction == null) throw new RuntimeException("No fitness function in this colony! May be you need to call Colony.setFitnessFunction() method");

        // Initialize
        if (Double.isNaN(bestFitness)) {
            bestFitness = (fitnessFunction.isMaximize() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            bestBeeIndex = -1;
        }
        //---
        // Evaluate each bee (and find the 'best' one)
        //---
        for (int i = 0; i < bees.length; ++i) {
            double fit = fitnessFunction.evaluate(bees[i]);
            numberOfEvaluations++; // Update counter

            // Is it a new best position?
            if (fitnessFunction.isBetterThan(fit, bestFitness)) {
                bestFitness = fit; // Copy best fitness, index, and position vector
                bestBeeIndex = i;
                if (bestPosition == null) bestPosition = new double[sampleBee.getDimension()];
                System.arraycopy(bees[i].getPosition(), 0, bestPosition, 0, sampleBee.getDimension());
            }
        }
    }
    public void evolve() {
        // Initialize bees' role (half are EMPLOYED, half are ONLOOKER, 1 is SCOUT)
        for (int i = 0; i < numberOfBees; i++) {
            if (i < numberOfBees / 2) {
                bees[i].setRole(Role.EMPLOYED);
            } else if (i < numberOfBees - 1) {
                bees[i].setRole(Role.ONLOOKER);
            } else {
                bees[i].setRole(Role.SCOUT);
            }
        }

        // Each bee performs its role
        for (Bee bee : bees) {
            beeUpdate.update(this, bee);
        }

        // Find the best bee after the update
        Bee bestBee = null;
        double bestFitness = fitnessFunction.isMaximize() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        for (Bee bee : bees) {
            double fitness = bee.getFitness();
            if (fitnessFunction.isBetterThan(fitness, bestFitness)) {
                bestFitness = fitness;
                bestBee = bee;
            }
        }

        // If no improvement, the best bee becomes a scout
        if (fitnessFunction.isBetterThan(this.bestFitness, bestFitness)) {
            bestBee.setRole(Role.SCOUT);
        } else {
            this.bestFitness = bestFitness;
            this.bestBeeIndex = Arrays.asList(bees).indexOf(bestBee);
            this.bestPosition = bestBee.getPosition();
        }
    }

    public void initializePositionRange(double[] minPosition, double[] maxPosition) {
        this.minPosition = Arrays.copyOf(minPosition, minPosition.length);
        this.maxPosition = Arrays.copyOf(maxPosition, maxPosition.length);
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public void setExplorationFactor(double explorationFactor) {
        this.explorationFactor = explorationFactor;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public Bee getBestBee() {
        return bees[bestBeeIndex];
    }

    public double[] getBestPosition() {
        return Arrays.copyOf(bestPosition, bestPosition.length);
    }
    // Choose an employed bee with probability proportional to its fitness
    public Bee chooseEmployedBee() {
        double totalFitness = 0.0;
        for (Bee bee : bees) {
            if (bee.getRole() == Role.EMPLOYED) {
                totalFitness += bee.getFitness();
            }
        }

        double r = Math.random() * totalFitness;
        double cumulativeFitness = 0.0;
        for (Bee bee : bees) {
            if (bee.getRole() == Role.EMPLOYED) {
                cumulativeFitness += bee.getFitness();
                if (cumulativeFitness >= r) {
                    return bee;
                }
            }
        }

        return null;  // should not reach here if there are employed bees
    }

    // Evaluate the fitness of a given position
    public double evaluate(double[] position) {
        Bee tempBee = (Bee) sampleBee.selfFactory();
        tempBee.setPosition(position);
        return fitnessFunction.evaluate(tempBee);
    }


    @Override
    public Iterator<Bee> iterator() {
        return beesList.iterator();
    }

    public Bee getSampleBee() {
        return sampleBee;
    }

    public void setMinPosition(double minPosition) {
        if (sampleBee == null) throw new RuntimeException("Need to set sample particle before calling this method (use Swarm.setSampleParticle() method)");
        int dim = sampleBee.getDimension();
        this.minPosition = new double[dim];
        for (int i = 0; i < dim; i++)
            this.minPosition[i] = minPosition;
    }

    public void setMinPosition(double[] minPosition) {
        this.minPosition = minPosition;
    }

    public void setMaxPosition(double maxPosition) {
        if (sampleBee == null) throw new RuntimeException("Need to set sample particle before calling this method (use Swarm.setSampleParticle() method)");
        int dim = sampleBee.getDimension();
        this.maxPosition = new double[dim];
        for (int i = 0; i < dim; i++)
            this.maxPosition[i] = maxPosition;
    }

    public void setMaxPosition(double[] maxPosition) {
        this.maxPosition = maxPosition;
    }

    public void setBees(Bee[] bees) {
        this.bees = bees;
    }

    public void setBeeUpdate(BeeUpdate beeUpdate) {
        this.beeUpdate = beeUpdate;
    }

}
