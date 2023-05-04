package JSwarmBCO;

import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.ParticleUpdate;

import java.util.ArrayList;
import java.util.Iterator;

public class BeeSwarm implements Iterable<Bee>{

    public static double DEFAULT_GLOBAL_INCREMENT = 0.9;
    public static double DEFAULT_INERTIA = 0.95;
    public static int DEFAULT_NUMBER_OF_BEES = 25;
    public static double DEFAULT_BEE_INCREMENT = 0.9;
    public static double VELOCITY_GRAPH_FACTOR = 10.0;

    private double bestFitness;
    private int bestBeeIndex;
    private double[] bestPosition;
    private FitnessFunction fitnessFunction;
    private double globalIncrement;
    private double inertia;
    private double[] maxPosition;
    private double[] maxVelocity;
    private double[] minPosition;
    private double[] minVelocity;
    private int numberOfEvaluations;
    private int numberOfBees;
    private double beeIncrement;
    private Bee[] bees;
    private BeeUpdate beeUpdate;
    private VariablesUpdate variablesUpdate;
    private Neighborhood neighborhood;
    private double neighborhoodIncrement;
    private ArrayList<Bee> beesList;
    /** A sample bee: Build other bees based on this one */
    Bee sampleBee;

    public BeeSwarm(int numberOfBees, Bee sampleBee, FitnessFunction fitnessFunction) {
        if (sampleBee == null) throw new RuntimeException("Sample bee can't be null!");
        if (numberOfBees <= 0) throw new RuntimeException("Number of bees should be greater than zero.");

        globalIncrement = DEFAULT_GLOBAL_INCREMENT;
        inertia = DEFAULT_INERTIA;
        beeIncrement = DEFAULT_BEE_INCREMENT;
        numberOfEvaluations = 0;
        this.numberOfBees = numberOfBees;
        this.fitnessFunction = fitnessFunction;
        this.sampleBee = sampleBee;
        bestFitness = Double.NaN;
        bestBeeIndex = -1;

        // Set up particle update strategy (default: ParticleUpdateSimple)
        beeUpdate = new BeeUpdateSimple(sampleBee);

        // Set up variablesUpdate strategy (default: VariablesUpdate)
        variablesUpdate = new VariablesUpdate();

        neighborhood = null;
        neighborhoodIncrement = 0.0;
        beesList = null;


    }
    @Override
    public Iterator<Bee> iterator() {
        return null;
    }
    public double[] getBestPosition() {
        return bestPosition;
    }


    public double[] getNeighborhoodBestPosition(Bee bee) {
        if (neighborhood == null) return bee.getPosition();
        double d[] = neighborhood.getBestPosition(bee);
        if (d == null) return bee.getPosition();
        return d;
    }

    public double getInertia() {
        return 0;
    }

    public double getBeeIncrement() {
        return 0;
    }

    public double getNeighborhoodIncrement() {
        return 0;
    }

    public double getGlobalIncrement() {
        return 0;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public Bee getSampleBee() { return sampleBee;}

    public double getBestFitness() {
        return bestFitness;
    }

    public Bee getBestBee() {
        return bees[bestBeeIndex];
    }

    public void evolve() {
        // Initialize (if not already done)
        if (bees == null) init();

        evaluate(); // Evaluate particles
        update(); // Update positions and velocities

        variablesUpdate.update(this);
    }

    public void evaluate() {
        if (bees == null) throw new RuntimeException("No particles in this swarm! May be you need to call Swarm.init() method");
        if (fitnessFunction == null) throw new RuntimeException("No fitness function in this swarm! May be you need to call Swarm.setFitnessFunction() method");

        // Initialize
        if (Double.isNaN(bestFitness)) {
            bestFitness = (fitnessFunction.isMaximize() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
            bestBeeIndex = -1;
        }

        //---
        // Evaluate each bee (and find the 'best' one)
        //---
        for (int i = 0; i < bees.length; i++) {
            // Evaluate particle
            double fit = fitnessFunction.evaluate(bees[i]);

            numberOfEvaluations++; // Update counter

            // Update 'best global' position
            if (fitnessFunction.isBetterThan(bestFitness, fit)) {
                bestFitness = fit; // Copy best fitness, index, and position vector
                bestBeeIndex = i;
                if (bestPosition == null) bestPosition = new double[sampleBee.getDimension()];
                bees[bestBeeIndex].copyPosition(bestPosition);
            }

            // Update 'best neighborhood'
            if (neighborhood != null) {
                neighborhood.update(this, bees[i]);
            }

        }
    }


    /**
     * Initialize every particle
     * Warning: maxPosition[], minPosition[], maxVelocity[], minVelocity[] must be initialized and setted
     */
    public void init() {
        // Init particles
        bees = new Bee[numberOfBees];

        // Check constraints (they will be used to initialize particles)
        if (maxPosition == null) throw new RuntimeException("maxPosition array is null!");
        if (minPosition == null) throw new RuntimeException("maxPosition array is null!");
        if (maxVelocity == null) {
            // Default maxVelocity[]
            int dim = sampleBee.getDimension();
            maxVelocity = new double[dim];
            for (int i = 0; i < dim; i++)
                maxVelocity[i] = (maxPosition[i] - minPosition[i]) / 2.0;
        }
        if (minVelocity == null) {
            // Default minVelocity[]
            int dim = sampleBee.getDimension();
            minVelocity = new double[dim];
            for (int i = 0; i < dim; i++)
                minVelocity[i] = -maxVelocity[i];
        }

        // Init each particle
        for (int i = 0; i < numberOfBees; i++) {
            bees[i] = (Bee) sampleBee.selfFactory(); // Create a new particles (using 'sampleParticle' as reference)
            bees[i].init(maxPosition, minPosition, maxVelocity, minVelocity); // Initialize it
        }

        // Init neighborhood
        if (neighborhood != null) neighborhood.init(this);
    }
    public void update() {
        // Initialize a particle update iteration
        beeUpdate.begin(this);

        // For each particle...
        for (int i = 0; i < bees.length; i++) {
            // Update particle's position and speed
            beeUpdate.update(this, bees[i]);

            // Apply position and velocity constraints
            bees[i].applyConstraints(minPosition, maxPosition, minVelocity, maxVelocity);
        }

        // Finish a particle update iteration
        beeUpdate.end(this);
    }

    /**
     * Sets every minPosition[] to 'minPosition'
     * @param minPosition
     */
    public void setMinPosition(double minPosition) {
        if (sampleBee == null) throw new RuntimeException("Need to set sample bee before calling this method (use BeeSwarm.setSampleBee() method)");
        int dim = sampleBee.getDimension();
        this.minPosition = new double[dim];
        for (int i = 0; i < dim; i++)
            this.minPosition[i] = minPosition;
    }

    public void setMinPosition(double[] minPosition) {
        this.minPosition = minPosition;
    }

    public void setMinVelocity(double minVelocity[]) {
        this.minVelocity = minVelocity;
    }

    public void setSampleBee(Bee sampleBee) {
        this.sampleBee = sampleBee;
    }

    public void setMaxMinVelocity(double maxVelocity) {
        if (sampleBee == null) throw new RuntimeException("Need to set sample bee before calling this method (use Swarm.setSampleParticle() method)");
        int dim = sampleBee.getDimension();
        this.maxVelocity = new double[dim];
        minVelocity = new double[dim];
        for (int i = 0; i < dim; i++) {
            this.maxVelocity[i] = maxVelocity;
            minVelocity[i] = -maxVelocity;
        }
    }

    /**
     * Sets every maxPosition[] to 'maxPosition'
     * @param maxPosition
     */
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

    public void setMaxVelocity(double[] maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void setBees(Bee[] bees) {
        bees = bees;
        beesList = null;
    }

    public void setBeeUpdate(BeeUpdate beeUpdate) {
        this.beeUpdate = beeUpdate;
    }



}
