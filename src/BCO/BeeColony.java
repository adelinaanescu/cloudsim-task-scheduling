package BCO;

import utils.Role;

public class BeeColony {
    private SchedulerBee[] bees;
    private double minPosition;
    private double maxPosition;
    private SchedulerFitnessFunction fitnessFunction;
    private SchedulerBee bestBee;

    public BeeColony(int beeCount, double minPosition, double maxPosition, SchedulerFitnessFunction fitnessFunction) {
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
        this.fitnessFunction = fitnessFunction;
        bees = new SchedulerBee[beeCount];
        for (int i = 0; i < beeCount; i++) {
            bees[i] = new SchedulerBee();
            bees[i].performRandomSearch(minPosition, maxPosition);
        }
    }

    public void run(int iterations) {
        for (int i = 0; i < iterations; i++) {
            // Employed bees phase
            for (SchedulerBee bee : bees) {
                if(bee.getRole() == Role.EMPLOYED){
                    double[] newPosition = bee.exploreNeighborhood(minPosition, maxPosition);
                    double newFitness = fitnessFunction.evaluate(newPosition);
                    if (newFitness < bee.getFitness()) {
                        bee.setPosition(newPosition);
                        bee.setFitness(newFitness,true);
                    }
                }
            }

            // Onlooker bees phase
            for (SchedulerBee bee : bees) {
                if(bee.getRole() == Role.ONLOOKER){
                    SchedulerBee bestBee = getBestBee();
                    double[] newPosition = bestBee.copyPosition();
                    bee.setPosition(newPosition);
                    bee.setFitness(fitnessFunction.evaluate(newPosition),true);
                }
            }

            // Scout bees phase
            for (SchedulerBee bee : bees) {
                if(bee.getRole() == Role.SCOUT){
                    bee.performRandomSearch(minPosition, maxPosition);
                    bee.setFitness(fitnessFunction.evaluate(bee.getPosition()),true);
                }
            }

            // Print the best solution found so far
            SchedulerBee bestBee = getBestBee();
            System.out.println("Iteration " + (i+1) + ": Best fitness = " + bestBee.getFitness());
        }

        // Print the final solution
        SchedulerBee bestBee = getBestBee();
        System.out.println("Final solution: " + bestBee);
    }


    public SchedulerBee getBestBee() {
        SchedulerBee bestBee = null;
        for (SchedulerBee bee : bees) {
            if (bestBee == null || bee.getFitness() > bestBee.getFitness()) {
                bestBee = bee;
            }
        }
        return bestBee;
    }
    public SchedulerBee[] getBees() {
        return bees;
    }

    public double getBestFitness() {
        // Assume that fitness function has been applied to all bees and their fitness is available
        double bestFitness = Double.MAX_VALUE;

        for (SchedulerBee bee : bees) {
            if (bee.getFitness() < bestFitness) {
                bestFitness = bee.getFitness();
                bestBee = bee;
            }
        }

        return bestFitness;
    }

    public double[] getBestPosition() {
        SchedulerBee bestBee = getBestBee();
        if (bestBee != null) {
            return bestBee.getPosition();
        } else {
            return null;  // Or handle this case appropriately.
        }
    }

}
