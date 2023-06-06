package JSwarmBCO;

import utils.Role;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;

public abstract class Bee {

    //Best fitness function so far
    double bestFitness;
    //Best bee's position so far
    double[] bestPosition;

    //current fitness
    public double fitness;
    //current position
    private double[] position;

    private Role role;

    private Random random = new Random();

    //Constructors

    public Bee(int dimension) {
        allocate(dimension);
    }

    public Bee(Bee sampleBee) {
        int dimension = sampleBee.getDimension();
        allocate(dimension);
    }

    // Allocate memory
    public void allocate(int dimension) {
        position = new double[dimension];
        bestPosition = new double[dimension];
        bestFitness = Double.NaN;
        fitness = Double.NaN;
        for (int i = 0; i < position.length; i++)
            bestPosition[i] = Double.NaN;
    }

    /**
     * Apply position constraints
     * @param maxPosition : Vector stating maximum position for each dimension
     * @param minPosition : Vector stating minimum position for each dimension
     */
    public void applyConstraints(double[] minPosition, double[] maxPosition, double[] minVelocity, double[] maxVelocity) {

        // Position constraints are set? (do both of them in the same loop)
        if ((minPosition != null) && (maxPosition != null)) for (int i = 0; i < position.length; i++) {
            if (!Double.isNaN(minPosition[i])) position[i] = (Math.max(minPosition[i], position[i]));
            if (!Double.isNaN(maxPosition[i])) position[i] = (Math.min(maxPosition[i], position[i]));
        }
        else {
            // Do it individually
            if (minPosition != null) for (int i = 0; i < position.length; i++)
                if (!Double.isNaN(minPosition[i])) position[i] = (Math.max(minPosition[i], position[i]));
            if (maxPosition != null) for (int i = 0; i < position.length; i++)
                if (!Double.isNaN(maxPosition[i])) position[i] = (Math.min(maxPosition[i], position[i]));
        }
    }

    public double[] copyPosition() {
        return Arrays.copyOf(this.position, this.position.length);
    }
    // Copy position[] to bestPosition[]
    public void copyPosition2Best() {
        for (int i = 0; i < position.length; i++)
            bestPosition[i] = position[i];
    }


    /**
     * Initialize a bee's position vectors
     * @param maxPosition : Vector stating maximum position for each dimension
     * @param minPosition : Vector stating minimum position for each dimension
     */
    public void init(double maxPosition[], double minPosition[]) {
        for (int i = 0; i < position.length; i++) {
            if (Double.isNaN(maxPosition[i])) throw new RuntimeException("maxPosition[" + i + "] is NaN!");
            if (Double.isInfinite(maxPosition[i])) throw new RuntimeException("maxPosition[" + i + "] is Infinite!");

            if (Double.isNaN(minPosition[i])) throw new RuntimeException("minPosition[" + i + "] is NaN!");
            if (Double.isInfinite(minPosition[i])) throw new RuntimeException("minPosition[" + i + "] is Infinite!");

            // Initialize using uniform distribution
            position[i] = (maxPosition[i] - minPosition[i]) * Math.random() + minPosition[i];

            bestPosition[i] = Double.NaN;
        }
    }

    /**
     * Create a new instance of this bee
     * @return A new bee, just like this one
     */
    public Object selfFactory() {
        Class cl = this.getClass();
        Constructor cons;

        try {
            cons = cl.getConstructor((Class[]) null);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            return cons.newInstance((Object[]) null);
        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException e1) {
            throw new RuntimeException(e1);
        }

    }

    public abstract double[] performRandomSearch(double minPosition, double maxPosition);

    public abstract double[] exploreNeighborhood(double minPosition, double maxPosition);

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    public void setBestPosition(double[] bestPosition) {
        this.bestPosition = bestPosition;
    }
    public double getBestFitness() {
        return bestFitness;
    }

    public double[] getBestPosition() {
        return bestPosition;
    }

    public int getDimension() {
        return position.length;
    }

    public double getFitness() {
        return fitness;
    }

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        if (this.position == null) {
            this.position = new double[position.length];
        }
        System.arraycopy(position, 0, this.position, 0, position.length);
    }

    public void setFitness(double fitness, boolean maximize) {
        this.fitness = fitness;
        if ((maximize && (fitness > bestFitness)) // Maximize and bigger? => store data
                || (!maximize && (fitness < bestFitness)) // Minimize and smaller? => store data too
                || Double.isNaN(bestFitness)) {
            copyPosition2Best();
            bestFitness = fitness;
        }
    }


    public double[] explore() {
        double[] newPosition = getPosition().clone();

        // Randomly select a dimension to modify
        int dimensionToModify = random.nextInt(getDimension());

        // Generate a random number in the range of this dimension
        newPosition[dimensionToModify] = random.nextDouble() * getLimit(dimensionToModify);

        return newPosition;
    }

    public int getLimit(int i) {
        // Here you should return the upper limit for the i-th dimension.
        // For now, let's assume it's a constant.
        return 5;
    }


}
