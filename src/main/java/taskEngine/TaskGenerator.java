package taskEngine;

import model.*;
import util.SpecificPeriodToll;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskGenerator {
    Random random = new Random();
    int numOfTTTasks;
    int numOfETTasks;
    double utilization;
    double[][] periodsDist;
    double [] coreAffinityDist;
    PlatformModel platform;
    private final AtomicInteger seq = new AtomicInteger();

    public TaskGenerator(int numOfTTTasks, int numOfETTasks, double utilization, double[][] periodsDist, double[] coreAffinityDist, PlatformModel platform) {
        this.numOfTTTasks = numOfTTTasks;
        this.numOfETTasks = numOfETTasks;
        this.utilization = utilization;
        this.periodsDist = periodsDist;
        this.coreAffinityDist = coreAffinityDist;
        this.platform = platform;
    }

    public List<Task> initializeTTtasks() {
        List<Task> initialTTtasks = new ArrayList<>();
        int[] specificPeriods = SpecificPeriodToll.getSpecificPeriods(periodsDist, numOfTTTasks);
        String[][] specificCoreAffinity = CoreAffinityDistributionTool.specificCoreAffinity(numOfTTTasks,
                coreAffinityDist,
                platform.getAllCores()
                .stream()
                .filter(core -> core
                        .getScheduleType()
                        .getValue()
                        .contains("TT"))
                .toList());

        for (int i = 0; i < numOfTTTasks; i++) {
            Task temp = new TTtask(String.valueOf(seq.incrementAndGet()), specificPeriods[i], new DeadlineType());
            initialTTtasks.add(temp);
        }

        Collections.shuffle(initialTTtasks);

        for (int i = 0; i < numOfTTTasks; i++) {
            initialTTtasks.get(i).setCoreAffinity(specificCoreAffinity[i]);
        }

        initialTTtasks.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getId())));

        return initialTTtasks;
    }

    public List<Task> initializeETtasks() {
        List<Task> initialETTasks = new ArrayList<>();
        int[] specificPeriods = SpecificPeriodToll.getSpecificPeriods(periodsDist, numOfETTasks);
        String[][] specificCoreAffinity = CoreAffinityDistributionTool.specificCoreAffinity(numOfETTasks,
                coreAffinityDist,
                platform.getAllCores()
                        .stream()
                        .filter(core -> core
                                .getScheduleType()
                                .getValue()
                                .contains("ET"))
                        .toList());

        for (int i = 0; i < numOfETTasks; i++) {
            Task temp = new ETtask(String.valueOf(seq.incrementAndGet()), specificPeriods[i], new DeadlineType());
            initialETTasks.add(temp);
        }

        Collections.shuffle(initialETTasks);

        for (int i = 0; i < numOfETTasks; i++) {
            initialETTasks.get(i).setCoreAffinity(specificCoreAffinity[i]);
        }

        initialETTasks.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getId())));

        return initialETTasks;
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
