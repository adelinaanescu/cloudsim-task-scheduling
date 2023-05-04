package JSwarmBCO;

import java.util.Collection;
import java.util.HashMap;

public abstract class Neighborhood {
    // All neighborhoods are stored here, so that we do not need to calculate them each time
    HashMap<Bee, Collection<Bee>> neighborhoods;
    // The best Bee in the neighborhood is stored here
    HashMap<Bee, Bee> bestInNeighborhood;
    public Neighborhood() {
        neighborhoods = new HashMap<Bee, Collection<Bee>>();
        bestInNeighborhood = new HashMap<Bee, Bee>();
    }

    /**
     * Calculate all neighbors of Bee 'p'
     *
     * Note: The p's neighbors DO NOT include 'p'
     *
     * @param p : a Bee
     * @return A collection with all neighbors
     */
    public abstract Collection<Bee> calcNeighbours(Bee p);

    /**
     * Get the best Bee in the neighborhood
     * @param p
     * @return The best Bee in the neighborhood of 'p'
     */
    public Bee getBestBee(Bee p) {
        return bestInNeighborhood.get(p);
    }

    /**
     * Get the best position ever found by all the Bees in the neighborhood of 'p'
     * @param p
     * @return The best position in the neighborhood of 'p'
     */
    public double[] getBestPosition(Bee p) {
        Bee bestp = getBestBee(p);
        if (bestp == null) return null;
        return bestp.getBestPosition();
    }

    /**
     * Get all neighbors of Bee 'p'
     * @param p : a Bee
     * @return A collection with all neighbors
     */
    public Collection<Bee> getNeighbours(Bee p) {
        Collection<Bee> neighs = neighborhoods.get(p);
        if (neighs == null) neighs = calcNeighbours(p);
        return neighs;
    }

    /**
     * Initialize neighborhood
     * @param swarm
     * @return
     */
    public void init(BeeSwarm swarm) {
        // Create neighborhoods for each Bee
        for (Bee p : swarm) {
            Collection<Bee> neigh = getNeighbours(p);
            neighborhoods.put(p, neigh);
        }
    }

    /**
     * Update neighborhood: This is called after each iteration
     * @param swarm
     * @return
     */
    public void update(BeeSwarm swarm, Bee p) {
        // Find best fitness in this neighborhood
        Bee pbest = getBestBee(p);
        if ((pbest == null) || swarm.getFitnessFunction().isBetterThan(pbest.getBestFitness(), p.getBestFitness())) {
            // Bee 'p' is the new 'best in neighborhood' => we need to update all neighbors
            Collection<Bee> neigh = getNeighbours(p);
            for (Bee pp : neigh) {
                bestInNeighborhood.put(pp, p);
            }
        }
    }
}
   