package JSwarmBCO;

import net.sourceforge.jswarm_pso.Particle;
import net.sourceforge.jswarm_pso.Swarm;

public abstract class BeeUpdate {

    public BeeUpdate(Bee sampleBee) {
        this.sampleBee = sampleBee;
    }

    protected Bee sampleBee;

    /**
     * This method is called at the begining of each iteration
     * Initialize random vectors use for local and global updates (rlocal[] and rother[])
     */
    public void begin(Colony colony) {
    }

    /** This method is called at the end of each iteration */
    public void end(Colony colony) {
    }

    /** Update bee's position */
    public abstract void update(Colony colony, Bee bee);

}
