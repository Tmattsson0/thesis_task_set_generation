package taskEngine;

import model.DeadlineType;
import model.PlatformModel;
import model.TTtask;
import model.Task;
import util.SpecificPeriodToll;

import java.util.*;

public class TaskGenerator {
    Random random = new Random();
    int numOfTasks;
    double utilization;
    double[][] periodsDist;
    double [] coreAffinityDist;
    PlatformModel platform;
    WcetGenerator wcetGenerator;

    public TaskGenerator(int numOfTasks, double utilization, double[][] periodsDist, double[] coreAffinityDist, PlatformModel platform) {
        this.numOfTasks = numOfTasks;
        this.utilization = utilization;
        this.periodsDist = periodsDist;
        this.coreAffinityDist = coreAffinityDist;
        this.platform = platform;
        this.wcetGenerator = new WcetGenerator(SpecificPeriodToll.getSpecificPeriods(periodsDist,
                numOfTasks),
                utilization,
                numOfTasks,
                0.01,
                0.5);
    }

    public List<Task> initializeTTtasks() {
        List<Task> initialTTtasks = new ArrayList<>();
        int[] specificPeriods = SpecificPeriodToll.getSpecificPeriods(periodsDist, numOfTasks);
        String[][] specificCoreAffinity = CoreAffinityDistributionTool.specificCoreAffinity(numOfTasks, coreAffinityDist, platform.getAllCores());

        for (int i = 0; i < numOfTasks; i++) {
            Task temp = new TTtask(String.valueOf(i), specificPeriods[i], new DeadlineType());
            initialTTtasks.add(temp);
        }

        Collections.shuffle(initialTTtasks);

        for (int i = 0; i < numOfTasks; i++) {
            initialTTtasks.get(i).setCoreAffinity(specificCoreAffinity[i]);
        }

        initialTTtasks.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getId())));

        return initialTTtasks;
    }



//    private Task generateRandomTask() {
//
//        return null;
//    }

//    WcetGenerator wcetGenerator = new WcetGenerator()

//    public List<Task> genTTTaskSet(int numOfTTTasks, double[][] periods, DeadlineType deadlineType) {
//        List<Task> taskset = new ArrayList<>();
//        int[] specificPeriods = util.SpecificPeriodToll.getSpecificPeriods(periods, numOfTTTasks);
//        List<Integer> randomWCETBasedOnTTUtil = wcetGenerator.generateRandomWCETValuesHC();
//
//        for (int i = 0; i < numOfTTTasks; i++) {
//            taskset.add(new Task(String.valueOf(i + 1), "Task" + (i + 1), randomWCETBasedOnTTUtil.get(i), specificPeriods[i], calculateDeadline(deadlineType, specificPeriods[i]), TaskType.TT));
//        }
//        return taskset;
//    }

//    public List<Task> genETTaskSet(double etUtilization, int numOfETTasks, double[][] periods, DeadlineType deadlineType) {
//        List<Task> taskset = new ArrayList<>();
//        int[] specificPeriods = util.SpecificPeriodToll.getSpecificPeriods(periods, numOfETTasks);
//        int[] randomMITBasedOnETUtil = getRandomMITBasedOnETUtil(numOfETTasks, etUtilization, Arrays.stream(specificPeriods).sum());
//
//        for (int i = 0; i < numOfETTasks; i++) {
//            taskset.add(new Task(String.valueOf(i + 1), "Task" + (i + 1), randomMITBasedOnETUtil[i], specificPeriods[i], calculateDeadline(deadlineType, specificPeriods[i]), TaskType.ET));
//        }
//        return taskset;
//    }



    private int calculateDeadline(DeadlineType deadlineType, int specificPeriod) {
        if (deadlineType.isArbitrary()) {
            double deadlineMultiplier = random.ints(1, deadlineType.getX(), deadlineType.getY()).sum();
            return (int) ((deadlineMultiplier / 100) * specificPeriod);
        } else {
            return specificPeriod;
        }
    }
}




//    private int[] getRandomWCETBasedOnTTUtil(int numOfTTTasks, double ttUtil, int sumOfSpecificPeriods) {
//        int targetWCETSum = (int) (sumOfSpecificPeriods / ttUtil);
//        int[] finalRandomNumbers = new int[numOfTTTasks];
//
//        //Add numOfTasks amount of random numbers between 0 and targetWCETSum to initialRandomNumbers and also add 0 and targetWCETSum
//        ArrayList<Integer> initialRandomNumbers = new ArrayList<>();
//
//        for (Object o: random.ints(0, targetWCETSum + 1).limit(numOfTTTasks - 1).boxed().toArray()) {
//            initialRandomNumbers.add(Integer.parseInt(o.toString()));
//        }
//
//        initialRandomNumbers.add(0);
//        initialRandomNumbers.add(targetWCETSum);
//        Collections.sort(initialRandomNumbers);
//
//        for (int i = 0; i < initialRandomNumbers.size() - 1 ; i++) {
//            finalRandomNumbers[i] = initialRandomNumbers.get(i + 1) - initialRandomNumbers.get(i);
//        }
//        return finalRandomNumbers;
//    }

//    private int[] getRandomMITBasedOnETUtil(int numOfETTasks, double etUtil, int sumOfSpecificPeriods) {
//        int targetMITSum = (int) (sumOfSpecificPeriods/etUtil);
//        int[] finalRandomNumbers = new int[numOfETTasks];
//
//        ArrayList<Integer> initialRandomNumbers = new ArrayList<>();
//
//        for (Object o: random.ints(0, targetMITSum + 1).limit(numOfETTasks - 1).boxed().toArray()) {
//            initialRandomNumbers.add(Integer.parseInt(o.toString()));
//        }
//        initialRandomNumbers.add(0);
//        initialRandomNumbers.add(targetMITSum);
//        Collections.sort(initialRandomNumbers);
//
//        for (int i = 0; i < initialRandomNumbers.size() - 1 ; i++) {
//            finalRandomNumbers[i] = initialRandomNumbers.get(i + 1) - initialRandomNumbers.get(i);
//        }
//        return finalRandomNumbers;
//    }}

//    public int[] generateRandomWCETorMITValues(int numOfTasks, double util, int[] specificPeriods, double individualTaskUtilLowerBound, double individualTaskUtilUpperBound) {
//        int targetSum = (int) (Arrays.stream(specificPeriods).sum() * util);
//        double[] initialRandomNumbers = new double[numOfTasks];
//        Arrays.setAll(initialRandomNumbers, i -> random.doubles(1, 0.01, 1).sum());
//        double sumOfinitialRandomNumbers = Arrays.stream(initialRandomNumbers).sum();
//        int[] finalRandomNumbers = new int[numOfTasks];
//        List<UtilPair> utilPairs = new ArrayList<>();
//
//        for (int i = 0; i < initialRandomNumbers.length; i++) {
//            finalRandomNumbers[i] = (int) Math.ceil((initialRandomNumbers[i] / sumOfinitialRandomNumbers) * targetSum);
//        }
//
//        for (int i = 0; i < finalRandomNumbers.length; i++) {
//            utilPairs.add(new UtilPair(specificPeriods[i], finalRandomNumbers[i]));
//        }
//
//        while (!utilsAreWithinBounds(utilPairs, individualTaskUtilLowerBound, individualTaskUtilUpperBound)) {
//            //Find utilPair with the biggest delta
//            UtilPair biggestDelta = utilPairs.stream().max(Comparator.comparing(
//                    utilPair -> utilPair.calculateUtilDelta(
//                            individualTaskUtilLowerBound, individualTaskUtilUpperBound))).orElseThrow();
//            List<UtilPair> utilPairsWithinBounds = utilPairs.stream().filter(utilPair -> utilPair.calculateUtilDelta(individualTaskUtilLowerBound, individualTaskUtilUpperBound) == 0).toList();
//
//            if (biggestDelta.getUtil() < individualTaskUtilLowerBound) {
//                //Util too low. Raise wcet of Utilapair. Steal from others.
//                int modifierValue = (int) (biggestDelta.getWcet() * (Math.random() + 1) - biggestDelta.getWcet());
//                biggestDelta.setWcet(biggestDelta.getWcet() + modifierValue);
//                for (UtilPair u: utilPairsWithinBounds) {
//                    u.setWcet(u.getWcet() - modifierValue/utilPairsWithinBounds.size());
//                }
//            } else if (biggestDelta.getUtil() > individualTaskUtilUpperBound) {
//                //Util to high. Lower wcet of utilpaur. Pass on to others.
//                int modifierValue = (int) (biggestDelta.getWcet() * (Math.random()) - biggestDelta.getWcet());
//                biggestDelta.setWcet(biggestDelta.getWcet() + modifierValue);
//                for (UtilPair u: utilPairsWithinBounds) {
//                    u.setWcet(u.getWcet() - modifierValue/utilPairsWithinBounds.size());
//                }
//            }
//        }
//
//        for (UtilPair u: utilPairs) {
//            System.out.println("util: " + u.getUtil());
//        }
//
//        for (int i = 0; i < finalRandomNumbers.length; i++) {
//            finalRandomNumbers[i] = utilPairs.get(i).getWcet();
//        }
//
//        return finalRandomNumbers;
//    }
//
//    private int[] generateRandomWCETorMITValuesSA(){
//        //Simulated Annealing based numbergenerator that will ensure that all individual task util are within bounds.
//        //Basic flow:
//        //1: Generate the random numbers as initial solution.
//        //2: Calculate fitness using a fitness function.
//        //2a: If within bounds end.
//        //3: randomly add or subtract 1 from a utilPair that is not within bounds.
//        //3a: Number of new solutions: (amountOfUtilPairsUnderBound * amountOfUtilPairsOverBound) * 2
//        //3b: Maybe add a "random" number to outOfBounds utilPairs. Number based on wcet avg over entire set. Number of new
//        //solutions is same as amount of outOfBounds.
//
//        //Maybe bipartite graph? Maybe a flow problem?
//        return null;
//    }
//
//    private boolean utilsAreWithinBounds(List<UtilPair> utilPairs, double lowerBound, double upperBound) {
//        return utilPairs.stream().noneMatch(utilPair -> utilPair.getUtil() >= lowerBound || utilPair.getUtil() <= upperBound);
//    }
//}
