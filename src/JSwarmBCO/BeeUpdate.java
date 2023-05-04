package JSwarmBCO;

public abstract class BeeUpdate {

    public BeeUpdate(Bee bee) {
    }

    public void begin(BeeSwarm swarm) {
    }

    public void end(BeeSwarm swarm) {
    }

    public abstract void update(BeeSwarm swarm, Bee bee);

}

