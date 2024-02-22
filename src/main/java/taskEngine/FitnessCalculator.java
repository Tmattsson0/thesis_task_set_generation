package taskEngine;

import data.Singleton;
import model.Chain;
import model.Core;
import model.PlatformModel;
import model.Task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            }

            if (c.getScheduleType().getValue().contains("ET")) {
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
        List<Integer> wcets = candidate.getAllTasks().stream().map(Task::getWcet).collect(Collectors.toCollection(ArrayList::new));
        Set<Integer> wcetSet = new HashSet<>(wcets);
        int penalty = 0;
        double sameWCETsAllowed = wcets.size() * 0.05;

        Map<Integer, Long> occurrences =
                wcets.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );

        for (int wcet : wcetSet) {
            if (occurrences.get(wcet) > sameWCETsAllowed) {
                penalty += occurrences.get(wcet);
            }
        }
        return penalty;
    }

    private static boolean isWithinPenaltyValue(double util) {
        return util <= 0.01 || util >= 0.99;
    }

    private static double calculateBadUtilPenalty(double util) {
        return Math.max(5, 5 * util + 1);
    }

    public static double calculateChainFitness(Chain c) {
        double fitness = 0;

        //Host transitions = important
        //Period trans = not as important
        //Repeat tasks = repeat (1 * repeat) / amount: penalty after 10%

        int specificNumberOfTasksInChain = c.getTasks().size();
        int specificNumOfHostTransitions = calculateNumOfHostTransitions(c);
        int specificNumOfLowToHighPeriodTransitions = calculateNumOfPeriodTransitions(c, PeriodTransitionType.LOWHIGH);
        int specificNumOfHighToLowPeriodTransitions = calculateNumOfPeriodTransitions(c, PeriodTransitionType.HIGHLOW);

        if(c.getDict().get("desiredNumOfHostTransitions") != specificNumOfHostTransitions) {
            fitness += Math.abs(c.getDict().get("desiredNumOfHostTransitions") - specificNumOfHostTransitions) * 3;
        }

        if(c.getDict().get("desiredNumOfLowToHighPeriodTransitions") != specificNumOfLowToHighPeriodTransitions) {
            fitness += Math.abs(c.getDict().get("desiredNumOfLowToHighPeriodTransitions") - specificNumOfLowToHighPeriodTransitions);
        }

        if(c.getDict().get("desiredNumOfHighToLowPeriodTransitions") != specificNumOfHighToLowPeriodTransitions) {
            fitness += Math.abs(c.getDict().get("desiredNumOfHighToLowPeriodTransitions") - specificNumOfHighToLowPeriodTransitions);
        }

//        fitness += calculateRepeatedTaskPenalty(c);
//        fitness += calculatePercentageOfSameTaskPenalty(c);
        fitness += calculateChainVariancePenalty(c);


        return fitness;
    }

    private static double calculateChainVariancePenalty(Chain c) {
        double penalty = 0;

        List<Integer> taskIds = c.getTasks().stream().map(t -> Integer.parseInt(t.getId())).collect(Collectors.toCollection(ArrayList::new));

        Map<Integer, Long> result =
                taskIds.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );

        Set<Integer> taskSet = new HashSet<>(taskIds);

        //Any one instance of 3 or more tasks in a row.
        if (taskIds.size() >= 3) {
            for (int i = 0; i < taskIds.size() - 2; i++) {
                if (Objects.equals(taskIds.get(i), taskIds.get(i + 1)) && Objects.equals(taskIds.get(i + 1), taskIds.get(i + 2))) {
                    penalty += 1;
                }
            }
        }

        //Any one instance of 2 same tasks in a row more than once
        int counter = 0;
        for (int taskId : taskSet) {
            for (int i = 0; i < taskIds.size() - 1; i++) {
                if (Objects.equals(taskIds.get(i), taskId) && Objects.equals(taskIds.get(i + 1), taskId)) {
                    counter++;
                }

                if(counter > taskIds.size()/10) {
                    penalty++;
                }
            }
            //Any one task in chain more than 25%
            if (result.get(taskId) > taskIds.size() * 0.25) {
                penalty += 5;
            }
        }

        //For task sets more than 50: more than 20% duplicates in chain. For less it’s 10%
        int difference = Math.abs(taskIds.size() - taskSet.size());;
        if (taskIds.size() > 50) {
            if ((double) (difference / taskIds.size()) >= 0.2) {
                penalty = Math.round(100 * (double) (difference / taskIds.size()));
            }
        } else {
            if ((double) (difference / taskIds.size()) >= 0.1) {
                penalty += 5;
            }
        }
        return penalty;
    }

    private static double calculatePercentageOfSameTaskPenalty(Chain c) {
        double penalty = 0;

        List<String> taskNames = c.getTasks().stream().map(Task::getName).toList();

        Set<String> taskSet = new HashSet<>(taskNames);

        int difference = Math.abs(taskNames.size() - taskSet.size());

        if ((double) (difference / taskNames.size()) > 0.1) {
            penalty = Math.round(100 * (double) (difference / taskNames.size()));
        }
        return penalty;
    }

    private static double calculateRepeatedTaskPenalty(Chain c) {
        double penalty = 0;

        List<String> taskIds = c.getTasks().stream().map(Task::getId).collect(Collectors.toCollection(ArrayList::new));

        Map<String, Long> result =
                taskIds.stream().collect(
                        Collectors.groupingBy(
                                Function.identity(), Collectors.counting()
                        )
                );

        for (String taskId : taskIds) {
            if (result.get(taskId) > 2) {
                penalty += (double) result.get(taskId);
            }
        }

        return penalty;
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
