package taskEngine;

import data.Singleton;
import model.*;
import util.RandomUtil;

import java.util.*;

import static util.LogUtil.addLineToLog;
import static util.LogUtil.initialAddToLog;

public class TaskModifierHC {

    Singleton s = Singleton.getInstance();

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

    public void modifyTasksUsingHeuristicBasicHC() {
        boolean bool = true;
        PlatformModel currentSolution = new PlatformModel(s.PLATFORMMODEL);
        currentSolution.setFitness(FitnessCalculator.calculateFitness(currentSolution));

        initialAddToLog(currentSolution);

        PlatformModel bestSolution = null;

        if (currentSolution.getFitness() == 0) {
            bestSolution = new PlatformModel(currentSolution);
            bool = false;
        }

        while (bool) {
            List<PlatformModel> neighbours = generateCandidateMoves(currentSolution);

            //shuffle
            Collections.shuffle(neighbours, RandomUtil.getRandom());

            //Select first better solution or break if none exist
            PlatformModel firstBetterNeighbour = null;

            for (PlatformModel p : neighbours) {
                double fitness = FitnessCalculator.calculateFitness(p);

                if (fitness < currentSolution.getFitness()) {
                    firstBetterNeighbour = new PlatformModel(p);
                    firstBetterNeighbour.setFitness(fitness);
                    break;
                }
            }

            //Top of hill
            if (Objects.isNull(firstBetterNeighbour)) {
                bestSolution = new PlatformModel(currentSolution);
                System.out.println("Top of hill!");
                break;
            }

            //Perfect solution
            else if (firstBetterNeighbour.getFitness() == 0) {
                bestSolution = new PlatformModel(firstBetterNeighbour);
                break;
            }

            //Climb
            else {
                addLineToLog(firstBetterNeighbour, currentSolution);
                currentSolution = new PlatformModel(firstBetterNeighbour);
            }
        }
        s.PLATFORMMODEL = bestSolution;
    }

    public void modifyTasksUsingHeuristicSteepestAscentHC() {
        boolean bool = true;
        PlatformModel currentSolution = new PlatformModel(s.PLATFORMMODEL);
        currentSolution.setFitness(FitnessCalculator.calculateFitness(currentSolution));

        initialAddToLog(currentSolution);

        PlatformModel bestSolution = null;

        if (currentSolution.getFitness() == 0) {
            bestSolution = new PlatformModel(currentSolution);
            bool = false;
        }

        while (bool) {
            List<PlatformModel> neighbours = generateCandidateMoves(currentSolution);

            //Find best neighbour
            PlatformModel bestNeighbour;

            bestNeighbour = Collections.min(neighbours, Comparator.comparingDouble(FitnessCalculator::calculateFitness));
            bestNeighbour.setFitness(FitnessCalculator.calculateFitness(bestNeighbour));

            //Top of hill
            if (currentSolution.getFitness() < bestNeighbour.getFitness()) {
                bestSolution = new PlatformModel(currentSolution);
                System.out.println("Top of hill!");
                break;
            }

            //Perfect solution
            else if (bestNeighbour.getFitness() == 0) {
                bestSolution = new PlatformModel(bestNeighbour);
                break;
            }

            //Climb
            else {
                addLineToLog(bestNeighbour, currentSolution);
                currentSolution = new PlatformModel(bestNeighbour);
            }
        }
        s.PLATFORMMODEL = bestSolution;
    }

    private List<PlatformModel> generateCandidateMoves(PlatformModel currentModel) {
        List<PlatformModel> neighbours = new ArrayList<>();

        int taskListSize = currentModel.getAllTasks().size();

        for (int i = 0; i < taskListSize; i++) {
            PlatformModel candidateModel = new PlatformModel(currentModel);
            Task t = candidateModel.getAllTasks().get(i);

            //changeWCET of task
            if (t instanceof TTtask) {
                if (candidateModel.getCoreByTaskId(t.getId()).calculateTTUtil() > s.TT_UTILIZATION) {
                    //Lower util
                    t.setWcet(getRandomLowerWCET(t));
                    neighbours.add(new PlatformModel(candidateModel));
                } else if (candidateModel.getCoreByTaskId(t.getId()).calculateTTUtil() < s.TT_UTILIZATION) {
                    //raise util
                    t.setWcet(getRandomHigherWCET(t));
                    neighbours.add(new PlatformModel(candidateModel));
                }
            } else {
                if (candidateModel.getCoreByTaskId(t.getId()).calculateETUtil() > s.ET_UTILIZATION) {
                    //Lower util
                    t.setWcet(getRandomLowerWCET(t));
                    neighbours.add(new PlatformModel(candidateModel));
                } else if (candidateModel.getCoreByTaskId(t.getId()).calculateETUtil() < s.ET_UTILIZATION) {
                    //raise util
                    t.setWcet(getRandomHigherWCET(t));
                    neighbours.add(new PlatformModel(candidateModel));
                }
            }
        }

        //Make move for each task
        for (int i = 0; i < taskListSize; i++) {
            PlatformModel candidateModel = new PlatformModel(currentModel);
            Task t = candidateModel.getAllTasks().get(i);
            List<Core> validCores = candidateModel.getCoresThatTaskCanBeAssignedTo(t);
            String coreIdToMoveTo = candidateModel.getLeastUtilizedCore(validCores, ScheduleType.NONE).getId();
            candidateModel.moveTask(t.getId(), coreIdToMoveTo);
            neighbours.add(new PlatformModel(candidateModel));
        }
        return neighbours;
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
}
