package BCO;

import JSwarmBCO.FitnessFunction;
import utils.Constants;
import utils.GenerateMatrices;

public class SchedulerFitnessFunction extends FitnessFunction {
    private static double[][] execMatrix, commMatrix;

    SchedulerFitnessFunction() {
        super(false);
        commMatrix = GenerateMatrices.getCommMatrix();
        execMatrix = GenerateMatrices.getExecMatrix();
    }

    @Override
    public double evaluate(double[] position) {
        double alpha = 0.3;
        return alpha * calcTotalTime(position) + (1 - alpha) * calcMakespan(position);
//        return calcMakespan(position);
    }

    private double calcTotalTime(double[] position) {
        double totalCost = 0;
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            totalCost += execMatrix[i][dcId] + commMatrix[i][dcId];
        }
        return totalCost;
    }

    public double calcMakespan(double[] position) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            if(dcWorkingTime[dcId] != 0) --dcWorkingTime[dcId];
            dcWorkingTime[dcId] += execMatrix[i][dcId] + commMatrix[i][dcId];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }

    public double evaluateBee(int index, double[] position) {
        double alpha = 0.3;
        return alpha * calcTotalTimeBee(index, position) + (1 - alpha) * calcMakespanBee(index, position);
//        return calcMakespanBee(index, position);
    }

    private double calcTotalTimeBee(int index, double[] position) {
        double totalCost = 0;
        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            if (dcId == index) {
                totalCost += execMatrix[i][dcId];
            } else {
                totalCost += commMatrix[i][dcId];
            }
        }
        return totalCost;
    }

    public double calcMakespanBee(int index, double[] position) {
        double makespan = 0;
        double dcWorkingTime = 0;

        for (int i = 0; i < Constants.NO_OF_TASKS; i++) {
            int dcId = (int) position[i];
            if(dcId == index) {
                dcWorkingTime += execMatrix[i][dcId];
            } else {
                dcWorkingTime += commMatrix[i][dcId];
            }
            makespan = Math.max(makespan, dcWorkingTime);
        }
        return makespan;
    }
}
