import model.DeadlineType;
import model.Task;
import model.TaskType;

import java.util.*;

public class TaskGenerator {
    Random random = new Random();

    private Task generateRandomTask(){

        return null;
    };

    public List<Task> genTTTaskSet(double ttUtilization, int numOfTTTasks, double[][] periods, DeadlineType deadlineType) {
        List<Task> taskset = new ArrayList<>();
        int [] specificPeriods = getSpecificPeriods(periods, numOfTTTasks);
        int [] randomWCETBasedOnTTUtil = getRandomWCETBasedOnTTUtil(numOfTTTasks, ttUtilization, Arrays.stream(specificPeriods).sum());

        for (int i = 0; i < numOfTTTasks; i++) {
            taskset.add(new Task(String.valueOf(i + 1), "Task" + (i + 1), randomWCETBasedOnTTUtil[i], specificPeriods[i], calculateDeadline(deadlineType, specificPeriods[i]), TaskType.TT));
        }
        return taskset;
    }

    public List<Task> genETTaskSet(double etUtilization, int numOfETTasks, double[][] periods, DeadlineType deadlineType){
        List<Task> taskset = new ArrayList<>();
        int [] specificPeriods = getSpecificPeriods(periods, numOfETTasks);
        int [] randomMITBasedOnETUtil = getRandomMITBasedOnETUtil(numOfETTasks, etUtilization, Arrays.stream(specificPeriods).sum());

        for (int i = 0; i < numOfETTasks; i++) {
            taskset.add(new Task(String.valueOf(i + 1), "Task" + (i + 1), randomMITBasedOnETUtil[i], specificPeriods[i], calculateDeadline(deadlineType, specificPeriods[i]), TaskType.ET));
        }
        return taskset;
    }


    private int calculateDeadline(DeadlineType deadlineType, int specificPeriod) {
        if (deadlineType.isArbitrary()) {
            double deadlineMultiplier = random.ints(1, deadlineType.getX(), deadlineType.getY()).sum();
            return (int) ((deadlineMultiplier/100) * specificPeriod);
        } else {
            return specificPeriod;
        }
    }


    private int[] getSpecificPeriods(double[][] periods, int numOfTasks){
        ArrayList<Integer> periodDistributionArray = generatePeriodDistributionArray(periods);
        int[] specificPeriods = new int[numOfTasks];
        for (int i = 0; i < specificPeriods.length; i++) {
            specificPeriods[i] = periodDistributionArray.get((int)(Math.random() * periodDistributionArray.size()));
        }
        return specificPeriods;
    }

    private ArrayList<Integer> generatePeriodDistributionArray(double[][] periods){
        int numOfDecimalNumbers = 0;
        for (double[] period : periods) {
            if (Double.toString(period[1]).split("\\.")[1].length() > numOfDecimalNumbers) {
                numOfDecimalNumbers = Double.toString(period[1]).length();
            }
        }

        ArrayList<Integer> periodDistribution = new ArrayList<>();

        for (double[] period : periods) {
            for (int k = 0; k < period[1] * (10 ^ numOfDecimalNumbers); k++) {
                periodDistribution.add((int) period[0]);
            }
        }

        //Create an array of size 10^numOfDecimalNumbers and allocate the periods 0,X * 10^3 times in the array.
        // Pick random value from array.

        return periodDistribution;
    }

    private int[] getRandomWCETBasedOnTTUtil(int numOfTTTasks, double ttUtil, int sumOfSpecificPeriods) {
        int targetWCETSum = (int) (sumOfSpecificPeriods / ttUtil);
        int[] finalRandomNumbers = new int[numOfTTTasks];

        //Add numOfTasks amount of random numbers between 0 and targetWCETSum to initialRandomNumbers and also add 0 and targetWCETSum
        ArrayList<Integer> initialRandomNumbers = new ArrayList<>();

        for (Object o: random.ints(0, targetWCETSum + 1).limit(numOfTTTasks - 1).boxed().toArray()) {
            initialRandomNumbers.add(Integer.parseInt(o.toString()));
        }

        initialRandomNumbers.add(0);
        initialRandomNumbers.add(targetWCETSum);
        Collections.sort(initialRandomNumbers);

        for (int i = 0; i < initialRandomNumbers.size() - 1 ; i++) {
            finalRandomNumbers[i] = initialRandomNumbers.get(i + 1) - initialRandomNumbers.get(i);
        }
        return finalRandomNumbers;
    }

    private int[] getRandomMITBasedOnETUtil(int numOfETTasks, double etUtil, int sumOfSpecificPeriods) {
        int targetMITSum = (int) (sumOfSpecificPeriods/etUtil);
        int[] finalRandomNumbers = new int[numOfETTasks];

        ArrayList<Integer> initialRandomNumbers = new ArrayList<>();

        for (Object o: random.ints(0, targetMITSum + 1).limit(numOfETTasks - 1).boxed().toArray()) {
            initialRandomNumbers.add(Integer.parseInt(o.toString()));
        }
        initialRandomNumbers.add(0);
        initialRandomNumbers.add(targetMITSum);
        Collections.sort(initialRandomNumbers);

        for (int i = 0; i < initialRandomNumbers.size() - 1 ; i++) {
            finalRandomNumbers[i] = initialRandomNumbers.get(i + 1) - initialRandomNumbers.get(i);
        }
        return finalRandomNumbers;
    }
}
