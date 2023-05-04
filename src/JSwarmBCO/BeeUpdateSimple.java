package JSwarmBCO;

public class BeeUpdateSimple extends BeeUpdate {

    /** Random vector for local update */
    double rlocal[];
    /** Random vector for global update */
    double rglobal[];
    /** Random vector for neighborhood update */
    double rneighborhood[];

    public BeeUpdateSimple(Bee bee) {
        super(bee);
        rlocal = new double[bee.getDimension()];
        rglobal = new double[bee.getDimension()];
        rneighborhood = new double[bee.getDimension()];
    }

    @Override
    public void begin(BeeSwarm swarm) {
        int i, dim = swarm.getSampleBee().getDimension();
        for (i = 0; i < dim; i++) {
            rlocal[i] = Math.random();
            rglobal[i] = Math.random();
            rneighborhood[i] = Math.random();
        }
    }
    /** This method is called at the end of each iteration */
    @Override
    public void end(BeeSwarm swarm) {
    }

    @Override
    public void update(BeeSwarm swarm, Bee bee) {
        double position[] = bee.getPosition();
        double velocity[] = bee.getVelocity();
        double globalBestPosition[] = swarm.getBestPosition();
        double particleBestPosition[] = bee.getBestPosition();
        double neighBestPosition[] = swarm.getNeighborhoodBestPosition(bee);

        // Update velocity and position
        for (int i = 0; i < position.length; i++) {
            // Update velocity
            velocity[i] = swarm.getInertia() * velocity[i] // Inertia
                    + rlocal[i] * swarm.getBeeIncrement() * (particleBestPosition[i] - position[i]) // Local best
                    + rneighborhood[i] * swarm.getNeighborhoodIncrement() * (neighBestPosition[i] - position[i]) // Neighborhood best
                    + rglobal[i] * swarm.getGlobalIncrement() * (globalBestPosition[i] - position[i]); // Global best
            // Update position
            position[i] += velocity[i];
        }
    }


}
