package taskEngine;

import data.Singleton;
import model.*;
import util.RandomUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static util.LogUtil.addLineToLog;
import static util.LogUtil.initialAddToLog;
import static taskEngine.FitnessCalculator.calculateFitness;

public class TaskModifier {
    Singleton s = Singleton.getInstance();
    int timeLimit = 1200;


    public void generateInitialConfiguration(List<Task> taskList) {
        //Calculate a starting wcet for tasks
        //Assign tasks to cores that will take them. And do it after a "least utilized" order.

        for (Task t : taskList) {
            t.setWcet(RandomUtil.getRandom().ints(1, 1, t.getPeriod()).sum());
            if (t instanceof TTtask) {
                s.PLATFORMMODEL.addTaskToCore(t, s.PLATFORMMODEL.getLeastUtilizedCore(s.PLATFORMMODEL.getCoresThatTaskCanBeAssignedTo(t), ScheduleType.TT).getId());
            } else {
                s.PLATFORMMODEL.addTaskToCore(t, s.PLATFORMMODEL.getLeastUtilizedCore(s.PLATFORMMODEL.getCoresThatTaskCanBeAssignedTo(t), ScheduleType.ET).getId());
            }
        }
    }

    public void modifyTasksUsingHeuristic() {
        Instant start = Instant.now();
        PlatformModel currentSolution = new PlatformModel(s.PLATFORMMODEL);
        currentSolution.setFitness(calculateFitness(currentSolution));

        initialAddToLog(currentSolution);

        // Initial and final temperature
        double T = 500;

        // Temperature at which iteration terminates
        final double Tmin = .001;

        // Decrease in temperature
        final double alpha = 0.9;

        // Number of iterations of annealing before decreasing temperature
        final int numIterations = Math.min((s.NUM_OF_ET_TASKS + s.NUM_OF_TT_TASKS), 200);

        double minFitness = Double.MAX_VALUE;
        PlatformModel bestSolution = null;


        while (T > Tmin) {
            for (int i = 0; i < numIterations; i++) {

                if (calculateFitness(currentSolution) == 0) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = new PlatformModel(currentSolution);
                    T = 0;
                    break;
                }

                if (currentSolution.getFitness() < minFitness) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = new PlatformModel(currentSolution);
                }

                if(Duration.between(start, Instant.now()).getSeconds() >= timeLimit){
                    System.out.println("Time limit of " + timeLimit + " seconds reached");
                    T = 0;
                    break;
                }

                PlatformModel newSolution = generateCandidateMove(currentSolution, new double[]{0.75, 0.25});
                newSolution.setFitness(calculateFitness(newSolution));

//                double probabilityFunction = (-Math.abs(currentSolution.getFitness() - newSolution.getFitness())) / T;
                double probabilityFunction = (newSolution.getFitness() - currentSolution.getFitness()) / T;
                double ap = Math.pow(Math.E, -probabilityFunction);

//                System.out.println("AP value is: " + ap);

                if (newSolution.getFitness() < currentSolution.getFitness()) {
                    addLineToLog(newSolution, currentSolution);
                    currentSolution = new PlatformModel(newSolution);
                } else if (ap < RandomUtil.getRandom().doubles(1, 0, 1).sum()) {
                    addLineToLog(newSolution, currentSolution);
                    currentSolution = new PlatformModel(newSolution);
                }
            }
            T *= alpha;
        }

        assert bestSolution != null;

        s.PLATFORMMODEL = bestSolution;
    }

    //Neighbourhood function
    private PlatformModel generateCandidateMove(PlatformModel currentModel, double [] moveTypeProbability) {
        PlatformModel candidateModel = new PlatformModel(currentModel);

        double moveDice = RandomUtil.getRandom().doubles(1, 0, Arrays.stream(moveTypeProbability).sum()).sum();
        Task randomTask = getIdealRandomTask(candidateModel);

        //changeWCET of task
        if (moveDice <= moveTypeProbability[0]){
            if (randomTask instanceof TTtask) {
                if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateTTUtil() > s.TT_UTILIZATION) {
                    //Lower util
                    randomTask.setWcet(getRandomLowerWCET(randomTask));
                } else if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateTTUtil() < s.TT_UTILIZATION) {
                    //raise util
                    randomTask.setWcet(getRandomHigherWCET(randomTask));
                }
            } else {
                if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateETUtil() > s.ET_UTILIZATION) {
                    //Lower util
                    randomTask.setWcet(getRandomLowerWCET(randomTask));
                } else if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateETUtil() < s.ET_UTILIZATION) {
                    //raise util
                    randomTask.setWcet(getRandomHigherWCET(randomTask));
                }
            }
        } else if (moveDice > moveTypeProbability[0] && moveDice <= moveTypeProbability[0] + moveTypeProbability[1]) {
            //Move task
            List<Core> validCores = candidateModel.getCoresThatTaskCanBeAssignedTo(randomTask);
            String coreIdToMoveTo = candidateModel.getLeastUtilizedCore(validCores, ScheduleType.NONE).getId();
            candidateModel.moveTask(randomTask.getId(), coreIdToMoveTo);
        }
        //todo make swap
//        else {
//        //Swap task
//            //Needs logic for making "legal swaps"
//
//
//        }

        return candidateModel;
    }

    private Task getIdealRandomTask(PlatformModel model) {
        //get a random task from the core with the biggest util delta
        double rand = RandomUtil.getRandom().doubles(1, 0, 1).sum();
        double percentageOfTTtasks = (double) s.NUM_OF_TT_TASKS / (s.NUM_OF_TT_TASKS + s.NUM_OF_ET_TASKS);
        if (rand <= percentageOfTTtasks) {
            //TT
            return getRandomTaskFromWorstUtilizedCore(model, ScheduleType.TT);
        } else {
            //ET
            return getRandomTaskFromWorstUtilizedCore(model, ScheduleType.ET);
        }
    }

    private int getRandomLowerWCET(Task t) {
        if (t.getWcet() == 1) {
            return 1;
        } else {
            return (RandomUtil.getRandom().ints(1, 1, t.getWcet()).sum());
        }
    }

    private int getRandomHigherWCET(Task t) {
        if (t.getWcet() == t.getPeriod()) {
            return t.getWcet();
        } else {
            return RandomUtil.getRandom().ints(1, t.getWcet(), t.getPeriod()).sum();
        }
    }

    private Task getRandomTaskFromWorstUtilizedCore(PlatformModel model, ScheduleType scheduleType) {
        List<Core> cores = model.getAllCores(scheduleType);
        if (scheduleType == ScheduleType.TT) {
            cores.sort(Comparator.comparing(core -> (Math.abs(core.calculateTTUtil() - s.TT_UTILIZATION)), Collections.reverseOrder()));
            for (Core c : cores) {
                if (c.getTasks().stream().anyMatch(t -> t instanceof TTtask)){
                    List<Task> tasks = c.getTasks().stream().filter(t -> t instanceof TTtask).toList();
                    return tasks.get(RandomUtil.getRandom().ints(1, 0, tasks.size()).sum());
                }
            }
        } else {
            cores.sort(Comparator.comparing(core -> (Math.abs(core.calculateETUtil() - s.ET_UTILIZATION)), Collections.reverseOrder()));
            for (Core c : cores) {
                if (c.getTasks().stream().anyMatch(t -> t instanceof ETtask)) {
                    List<Task> tasks = c.getTasks().stream().filter(t -> t instanceof ETtask).toList();
                    return tasks.get(RandomUtil.getRandom().ints(1, 0, tasks.size()).sum());
                }
            }
        }
        return model.getRandomTask();
    }
}
