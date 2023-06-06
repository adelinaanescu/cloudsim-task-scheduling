package JSwarmBCO;

public class BeeUpdateSimple extends BeeUpdate{
    double r[];

    public BeeUpdateSimple(Bee bee) {
        super(bee);
        r = new double[bee.getDimension()];
    }

    @Override
    public void begin(Colony colony) {
        int dim = colony.getSampleBee().getDimension();
        for (int i = 0; i < dim; i++) {
            r[i] = Math.random();
        }
    }

    @Override
    public void end(Colony colony) {
    }

    @Override
    public void update(Colony colony, Bee bee) {
        double position[] = bee.getPosition();
        double bestPosition[] = colony.getBestPosition();

        for (int i = 0; i < position.length; i++) {
            // Update position
            position[i] += r[i] * (bestPosition[i] - position[i]);
        }
    }
}
