package JSwarmBCO;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Bee {

    /** Best fitness function so far */
    double bestFitness;
    /** Best particles's position so far */
    double bestPosition[];

    double bestVelocity[];
    /** current fitness */
    double fitness;
    /** Position */
    double position[];
    /** Velocity */
    double velocity[];


    /**
     * Constructor
     * @param dimension : Particle's dimension
     */
    public Bee(int dimension) {
        allocate(dimension);
    }

    public Bee(Bee sampleBee) {
        int dimension = sampleBee.getDimension();
        allocate(dimension);
    }


    public void allocate(int dimension) {
        position = new double[dimension];
        bestPosition = new double[dimension];
        velocity = new double[dimension];
        bestFitness = Double.NaN;
        fitness = Double.NaN;
        for (int i = 0; i < position.length; i++)
            bestPosition[i] = Double.NaN;
    }

    public double[] getPosition() {
        return position;
    }
    public void setPosition(double[] position) {
        this.position = position;
    }

    public double[] getBestPosition() {
        return bestPosition;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public double[] getBestVelocity() {
        return bestVelocity;
    }

    public int getDimension() {
        return position.length;
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
    /** Copy position[] to bestPosition[] */
    public void copyPosition2Best() {
        for (int i = 0; i < position.length; i++)
            bestPosition[i] = position[i];
    }
    /** Copy position[] to positionCopy[] */
    public void copyPosition(double positionCopy[]) {
        for (int i = 0; i < position.length; i++)
            positionCopy[i] = position[i];
    }

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
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * Initialize a particles's position and velocity vectors
     * @param maxPosition : Vector stating maximum position for each dimension
     * @param minPosition : Vector stating minimum position for each dimension
     * @param maxVelocity : Vector stating maximum velocity for each dimension
     * @param minVelocity : Vector stating minimum velocity for each dimension
     */
    public void init(double maxPosition[], double minPosition[], double maxVelocity[], double minVelocity[]) {
        for (int i = 0; i < position.length; i++) {
            if (Double.isNaN(maxPosition[i])) throw new RuntimeException("maxPosition[" + i + "] is NaN!");
            if (Double.isInfinite(maxPosition[i])) throw new RuntimeException("maxPosition[" + i + "] is Infinite!");

            if (Double.isNaN(minPosition[i])) throw new RuntimeException("minPosition[" + i + "] is NaN!");
            if (Double.isInfinite(minPosition[i])) throw new RuntimeException("minPosition[" + i + "] is Infinite!");

            if (Double.isNaN(maxVelocity[i])) throw new RuntimeException("maxVelocity[" + i + "] is NaN!");
            if (Double.isInfinite(maxVelocity[i])) throw new RuntimeException("maxVelocity[" + i + "] is Infinite!");

            if (Double.isNaN(minVelocity[i])) throw new RuntimeException("minVelocity[" + i + "] is NaN!");
            if (Double.isInfinite(minVelocity[i])) throw new RuntimeException("minVelocity[" + i + "] is Infinite!");

            // Initialize using uniform distribution
            position[i] = (maxPosition[i] - minPosition[i]) * Math.random() + minPosition[i];
            velocity[i] = (maxVelocity[i] - minVelocity[i]) * Math.random() + minVelocity[i];

            bestPosition[i] = Double.NaN;
        }
    }

    public void applyConstraints(double[] minPosition, double[] maxPosition, double[] minVelocity, double[] maxVelocity) {
        //---
        // Every constraint is set? (do all of them it one loop)
        //---
        if ((minPosition != null) && (maxPosition != null) && (minVelocity != null) && (maxVelocity != null)) for (int i = 0; i < position.length; i++) {
            if (!Double.isNaN(minPosition[i])) position[i] = (Math.max(minPosition[i], position[i]));
            if (!Double.isNaN(maxPosition[i])) position[i] = (Math.min(maxPosition[i], position[i]));
            if (!Double.isNaN(minVelocity[i])) velocity[i] = (Math.max(minVelocity[i], velocity[i]));
            if (!Double.isNaN(maxVelocity[i])) velocity[i] = (Math.min(maxVelocity[i], velocity[i]));
        }
        else {
            //---
            // Position constraints are set? (do both of them in the same loop)
            //---
            if ((minPosition != null) && (maxPosition != null)) for (int i = 0; i < position.length; i++) {
                if (!Double.isNaN(minPosition[i])) position[i] = (Math.max(minPosition[i], position[i]));
                if (!Double.isNaN(maxPosition[i])) position[i] = (Math.min(maxPosition[i], position[i]));
            }
            else {
                //---
                // Do it individually
                //---
                if (minPosition != null) for (int i = 0; i < position.length; i++)
                    if (!Double.isNaN(minPosition[i])) position[i] = (Math.max(minPosition[i], position[i]));
                if (maxPosition != null) for (int i = 0; i < position.length; i++)
                    if (!Double.isNaN(maxPosition[i])) position[i] = (Math.min(maxPosition[i], position[i]));
            }

            //---
            // Velocity constraints are set? (do both of them in the same loop)
            //---
            if ((minVelocity != null) && (maxVelocity != null)) for (int i = 0; i < velocity.length; i++) {
                if (!Double.isNaN(minVelocity[i])) velocity[i] = (Math.max(minVelocity[i], velocity[i]));
                if (!Double.isNaN(maxVelocity[i])) velocity[i] = (Math.min(maxVelocity[i], velocity[i]));
            }
            else {
                //---
                // Do it individually
                //---
                if (minVelocity != null) for (int i = 0; i < velocity.length; i++)
                    if (!Double.isNaN(minVelocity[i])) velocity[i] = (Math.max(minVelocity[i], velocity[i]));
                if (maxVelocity != null) for (int i = 0; i < velocity.length; i++)
                    if (!Double.isNaN(maxVelocity[i])) velocity[i] = (Math.min(maxVelocity[i], velocity[i]));
            }
        }
    }



}
