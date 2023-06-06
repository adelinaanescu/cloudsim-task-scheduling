package BCO;

import JSwarmBCO.Bee;
import JSwarmBCO.BeeUpdate;
import JSwarmBCO.Colony;
import utils.Role;
import utils.Constants;

public class SchedulerBeeUpdate extends BeeUpdate {
    private static final double epsilon = 0.001;  // small value to prevent division by zero

    public SchedulerBeeUpdate(Bee sampleBee) {
        super(sampleBee);
    }

    @Override
    public void update(Colony colony, Bee bee) {
        Role role = bee.getRole();
        double[] position = bee.getPosition();
        double[] newPosition = new double[position.length];


        switch (role) {
            case EMPLOYED:
                // employed bees explore their neighborhood
                newPosition = bee.exploreNeighborhood(Constants.minPosition, Constants.maxPosition);
                break;

            case ONLOOKER:
                // onlooker bees exploit the solutions found by employed bees
                Bee chosenBee = colony.chooseEmployedBee();  // method to choose an employed bee, to be implemented
                newPosition = chosenBee.getBestPosition();
                break;

            case SCOUT:
                // scout bees perform random search
                newPosition = bee.performRandomSearch(Constants.minPosition, Constants.maxPosition);
                break;
        }

        // evaluate the new position
        double newFitness = colony.evaluate(newPosition);  // method to evaluate a position, to be implemented

        // if the new position is better, update the bee's position and fitness
        if (newFitness > bee.getFitness()) {
            bee.setPosition(newPosition);
            bee.setFitness(newFitness, true);
        }
    }
}
