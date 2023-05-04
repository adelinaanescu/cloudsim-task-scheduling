package BCO;

import JSwarmBCO.Bee;
import JSwarmBCO.BeeSwarm;
import JSwarmBCO.BeeUpdate;
import utils.Constants;
import java.util.Random;

public class SchedulerBeeUpdate extends BeeUpdate {
    private static final double W = 0.9;
    private static final double C = 2.0;

    public SchedulerBeeUpdate(Bee bee) {
        super(bee);
    }

    public void update(BeeSwarm swarm, Bee bee) {
        double[] v = bee.getVelocity();
        double[] x = bee.getPosition();
        double[] pbest = bee.getBestPosition();
        double[] gbest = swarm.getBestPosition();

        Random rand = new Random();

        for (int i = 0; i < Constants.NO_OF_TASKS; ++i) {
            v[i] = W * v[i] + C * rand.nextDouble() * (pbest[i] - x[i]) + C * rand.nextDouble() * (gbest[i] - x[i]);
            x[i] = (int) (x[i] + v[i]);
        }
    }

}
