package BCO;

import JSwarmBCO.Colony;
import utils.Constants;

public class BCO {

    private static Colony colony;
    private static SchedulerBee bees[];
    private static SchedulerFitnessFunction ff = new SchedulerFitnessFunction();

    public BCO() {
        initBees();
    }

    public double[] run() {
        colony = new Colony(Constants.POPULATION_SIZE, new SchedulerBee(), ff);

        colony.setMinPosition(0.0);
        colony.setMaxPosition(Constants.NO_OF_DATA_CENTERS - 1.0);
        colony.setBees(bees);
        colony.setBeeUpdate(new SchedulerBeeUpdate(new SchedulerBee()));

        for (int i = 0; i < Constants.NO_OF_ITER; i++) {
            colony.evolve();
            if (i % 10 == 0) {
                System.out.printf("Global best at iteration (%d): %f\n", i, colony.getBestFitness());
            }
        }

        System.out.println("\nThe best fitness value: " + colony.getBestFitness() + "\nBest makespan: " + ff.calcMakespan(colony.getBestBee().getBestPosition()));

        System.out.println("The best solution is: ");
        SchedulerBee bestBee = (SchedulerBee) colony.getBestBee();
        System.out.println(bestBee.toString());

        return colony.getBestPosition();
    }

    private static void initBees() {
        bees = new SchedulerBee[Constants.POPULATION_SIZE];
        for (int i = 0; i < Constants.POPULATION_SIZE; ++i)
            bees[i] = new SchedulerBee();
    }

    public void printBestFitness() {
        System.out.println("\nBest fitness value: " + colony.getBestFitness() +
                "\nBest makespan: " + ff.calcMakespan(colony.getBestBee().getBestPosition()));
    }
}
