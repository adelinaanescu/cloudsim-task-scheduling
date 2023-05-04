package JSwarmBCO;

import net.sourceforge.jswarm_pso.Swarm;

public class VariablesUpdate {
    public VariablesUpdate() {
        super();
    }

    public void update(BeeSwarm swarm) {
        // Nothing updated in this case (build your own VariablesUpdate class)
        // e.g. (exponential update):
        // swarm.setInertia( 0.99 * swarm.getInertia() );
    }
}
