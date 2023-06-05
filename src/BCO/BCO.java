package BCO;

import utils.Constants;

public class BCO {
    private static BeeColony colony;
    private static SchedulerFitnessFunction ff = new SchedulerFitnessFunction(true);

    // Add a tolerance for the change in best fitness
    private static final double FITNESS_CHANGE_TOLERANCE = 0.01;
    // Maximum number of iterations without improvement before stopping
    private static final int MAX_ITERATIONS_WITHOUT_IMPROVEMENT = 100;

    public double[] run() {
        colony = new BeeColony(Constants.POPULATION_SIZE, 0, Constants.NO_OF_DATA_CENTERS - 1, ff);
        initBees();

        double prevBestFitness = colony.getBestFitness();
        int iterationsWithoutImprovement = 0;

        for (int i = 0; i < 500; i++) {
            colony.run(1);

            double currentBestFitness = colony.getBestFitness();
            if (Math.abs(currentBestFitness - prevBestFitness) < FITNESS_CHANGE_TOLERANCE) {
                iterationsWithoutImprovement++;
                if (iterationsWithoutImprovement >= MAX_ITERATIONS_WITHOUT_IMPROVEMENT) {
                    System.out.println("Stopping early due to lack of improvement in best fitness.");
                    break;
                }
            } else {
                iterationsWithoutImprovement = 0;
            }
            prevBestFitness = currentBestFitness;

            if (i % 10 == 0) {
                System.out.printf("Global best at iteration (%d): %f\n", i, colony.getBestFitness());
            }
        }

        System.out.println("\nThe best fitness value: " + colony.getBestFitness() + "\nBest makespan: " + ff.calcMakespan(colony.getBestBee().getBestPosition()));

        System.out.println("The best solution is: ");
        SchedulerBee bestBee = colony.getBestBee();
        System.out.println(bestBee.toString());

        return colony.getBestPosition();
    }

    private void initBees() {
        for (int i = 0; i < Constants.POPULATION_SIZE; ++i)
            colony.getBees()[i].performRandomSearch(0, Constants.NO_OF_DATA_CENTERS - 1);
    }

    public void printBestFitness() {
        System.out.println("\nBest fitness value: " + colony.getBestFitness() +
                "\nBest makespan: " + ff.calcMakespan(colony.getBestBee().getBestPosition()));
    }
}
