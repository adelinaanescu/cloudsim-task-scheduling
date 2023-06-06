package BCO;

import JSwarmBCO.Bee;
import utils.Constants;
import utils.Role;

import java.util.Random;

public class SchedulerBee extends Bee {
    public SchedulerBee() {
        super(Constants.NO_OF_TASKS);
        double[] position = new double[Constants.NO_OF_TASKS];
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            Random randObj = new Random();
            position[i] = randObj.nextInt(Constants.NO_OF_DATA_CENTERS);
        }
        setPosition(position);
        setRole(Role.EMPLOYED); // Initialized with a default role, can be updated later
    }

    @Override
    public double[] performRandomSearch(double minPosition, double maxPosition) {
        // clone the current position
        double[] newPosition = getPosition().clone();
        // select a random dimension
        int randomDimension = (int) (Math.random() * getPosition().length);
        // apply a small random perturbation to the selected dimension
        double perturbation = (Math.random() - 0.5) * Constants.PERTURBATION_RANGE;
        newPosition[randomDimension] += perturbation;
        // make sure the new position is within the search space boundaries
        newPosition[randomDimension] = Math.max(newPosition[randomDimension], minPosition);
        newPosition[randomDimension] = Math.min(newPosition[randomDimension], maxPosition);
        return newPosition;
    }

    @Override
    public double[] exploreNeighborhood(double minPosition, double maxPosition) {
        double[] newPosition = new double[getPosition().length];
        for (int i = 0; i < newPosition.length; i++) {
            // generate a random position in the search space
            newPosition[i] = minPosition + Math.random() * (maxPosition - minPosition);
        }
        // Set the position to the new position
        setPosition(newPosition);
        // reset the fitness
        fitness = -1;
        return newPosition;
    }
    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
            String tasks = "";
            int no_of_tasks = 0;
            for (int j = 0; j < Constants.NO_OF_TASKS; j++) {
                if (i == (int) getPosition()[j]) {
                    tasks += (tasks.isEmpty() ? "" : ", ") + j;
                    ++no_of_tasks;
                }
            }
            if (tasks.isEmpty()) {
                output += "There are no tasks associated with Data Center " + i + "\n";
            } else {
                output += "There are " + no_of_tasks + " tasks associated with Data Center " + i + " and they are " + tasks + "\n";
            }
        }
        return output;
    }
}
