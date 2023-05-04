package BCO;

import JSwarmBCO.BeeSwarm;
import utils.Constants;

public class BCO {
    private static BeeSwarm swarm;
    private static SchedulerBee bees[];
    private static SchedulerFitnessFunction ff = new SchedulerFitnessFunction();

    public BCO() {
        initBees();
    }


    public double[] run() {
        swarm = new BeeSwarm(Constants.POPULATION_SIZE, new SchedulerBee(), ff);

        swarm.setMinPosition(0);
        swarm.setMaxPosition(Constants.NO_OF_DATA_CENTERS - 1);
        swarm.setMaxMinVelocity(0.5);
        swarm.setBees(bees);
        swarm.setBeeUpdate(new SchedulerBeeUpdate(new SchedulerBee()));

        for (int i = 0; i < 500; i++) {
            swarm.evolve();
            if (i % 10 == 0) {
                System.out.printf("Gloabl best at iteration (%d): %f\n", i, swarm.getBestFitness());
            }
        }

        System.out.println("\nThe best fitness value: " + swarm.getBestFitness() + "\nBest makespan: " + ff.calcMakespan(swarm.getBestBee().getBestPosition()));

        System.out.println("The best solution is: ");
        SchedulerBee bestBee = (SchedulerBee) swarm.getBestBee();
        System.out.println(bestBee.toString());

        return swarm.getBestPosition();
    }

    private static void initBees() {
        bees = new SchedulerBee[Constants.POPULATION_SIZE];
        for (int i = 0; i < Constants.POPULATION_SIZE; ++i)
            bees[i] = new SchedulerBee();
    }

    public void printBestFitness() {
        System.out.println("\nBest fitness value: " + swarm.getBestFitness() +
                "\nBest makespan: " + ff.calcMakespan(swarm.getBestBee().getBestPosition()));
    }
}
