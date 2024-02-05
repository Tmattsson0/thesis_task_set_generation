package taskEngine;

import data.Singleton;
import model.Chain;
import model.Core;
import model.PlatformModel;
import model.Task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;

public class FitnessCalculator {

    static Singleton s = Singleton.getInstance();

    public static double calculateFitness(PlatformModel candidate){
        //Sum deltas of tt and et util.
        //Add large penalty value if core has less than 0.1% util of tt or et.
        //Add penalty of variance is small

        double fitness = 0;

        for (Core c : candidate.getAllCores()){
            if (c.getScheduleType().getValue().contains("TT")) {
                fitness += Math.abs(c.calculateTTUtil() - s.TT_UTILIZATION);
            } else if (c.getScheduleType().getValue().contains("ET")) {
                fitness += Math.abs(c.calculateETUtil() - s.ET_UTILIZATION);
            }
        }

        for (Core c : candidate.getAllCores()) {
            if (isWithinPenaltyValue(c.calculateETUtil()) && c.getScheduleType().getValue().contains("ET")) {
                fitness += calculateBadUtilPenalty(c.calculateETUtil());
            } else if (isWithinPenaltyValue(c.calculateTTUtil()) && c.getScheduleType().getValue().contains("TT")) {
                fitness += calculateBadUtilPenalty(c.calculateTTUtil());
            }
        }

        fitness += calculateVariancePenalty(candidate);

        return new BigDecimal(fitness).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

//    private static double calculateVariancePenalty(PlatformModel candidate) {
//
//        double meanWCET = candidate.getAllTasks().stream().mapToDouble(Task::getWcet).average().orElse(0);
//        int numOfTasks = candidate.getAllTasks().size();
//        double variance = 0;
//
//        for (Task t : candidate.getAllTasks()){
//            variance += Math.abs(t.getWcet() - meanWCET);
//        }
//
//        variance = variance/numOfTasks;
//
////        double theoreticalMaxVariance = (Math.pow((candidate.getAllTasks().stream().mapToDouble(Task::getPeriod).average().orElse(0)), 2))/4;
//        double theoreticalMaxVariance = (Math.pow((s.NUM_OF_ET_TASKS + s.NUM_OF_TT_TASKS), 2))/4;
//
//        double coefficient = (variance - 0)/(theoreticalMaxVariance - 0);
//
//        return 100 * (1 - coefficient);
//    }

    private static double calculateVariancePenalty(PlatformModel candidate){
        List<Integer> wcets = candidate.getAllTasks().stream().map(Task::getWcet).toList();
        HashSet<Integer> hs = new HashSet<>(wcets);

        int numOfDuplicates = wcets.size() - hs.size();

        double penalty = (double) wcets.size() * ((double) (numOfDuplicates)/(wcets.size()));

        if (numOfDuplicates >= wcets.size() * 0.1) {
            return Math.pow(numOfDuplicates, 0.9);
        } else if (numOfDuplicates >= wcets.size() * 0.2) {
            return Math.pow(numOfDuplicates, 1.1);
        } else {
            return 0;
        }
    }

    private static boolean isWithinPenaltyValue(double util) {
        return util <= 0.01 || util >= 0.99;
    }

    private static double calculateBadUtilPenalty(double util) {
        return Math.max(5, 5 * util + 1);
    }

    public static double calculateChainFitness(Chain c) {
        double fitness = 0;

        //Task in chain = essential
        //Host transitions = important
        //Period trans = not as important
        int specificNumberOfTasksInChain = c.getTasks().size();
        int specificNumOfHostTransitions = calculateNumOfHostTransitions(c);
        int specificNumOfLowToHighPeriodTransitions = calculateNumOfPeriodTransitions(c, PeriodTransitionType.LOWHIGH);
        int specificNumOfHighToLowPeriodTransitions = calculateNumOfPeriodTransitions(c, PeriodTransitionType.HIGHLOW);

        if (c.getDict().get("desiredNumTasksInChain") != specificNumberOfTasksInChain) {
            fitness += Math.abs(c.getDict().get("desiredNumTasksInChain") - specificNumberOfTasksInChain) * 5;
        }

        if(c.getDict().get("desiredNumOfHostTransitions") != specificNumOfHostTransitions) {
            fitness += Math.abs(c.getDict().get("desiredNumOfHostTransitions") - specificNumOfHostTransitions) * 3;
        }

        if(c.getDict().get("desiredNumOfLowToHighPeriodTransitions") != specificNumOfLowToHighPeriodTransitions) {
            fitness += Math.abs(c.getDict().get("desiredNumOfLowToHighPeriodTransitions") - specificNumOfLowToHighPeriodTransitions);
        }

        if(c.getDict().get("desiredNumOfHighToLowPeriodTransitions") != specificNumOfHighToLowPeriodTransitions) {
            fitness += Math.abs(c.getDict().get("desiredNumOfHighToLowPeriodTransitions") - specificNumOfHighToLowPeriodTransitions);
        }
        return fitness;
    }

    private static int calculateNumOfPeriodTransitions(Chain c, PeriodTransitionType periodTransitionType) {
        int transitions = 0;
        int previousPeriod = c.getTasks().get(0).getPeriod();

        if (periodTransitionType.equals(PeriodTransitionType.HIGHLOW)) {
            for (Task t : c.getTasks()) {
                if (t.getPeriod() > previousPeriod) {
                    transitions++;
                }
                previousPeriod = t.getPeriod();
            }
        } else {
            for (Task t : c.getTasks()) {
                if (t.getPeriod() < previousPeriod) {
                    transitions++;
                }
                previousPeriod = t.getPeriod();
            }
        }
        return transitions;
    }

    private static int calculateNumOfHostTransitions(Chain c) {
        int transitions = 0;
        String previousHost = c.getTasks().get(0).getCoreId();

        for (Task t : c.getTasks()) {
            if (!t.getCoreId().equals(previousHost)) {
                transitions++;
            };
            previousHost = t.getCoreId();
        }
        return transitions;
    }

    enum PeriodTransitionType {
        LOWHIGH,
        HIGHLOW
    }
}
