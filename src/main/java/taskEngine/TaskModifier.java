package taskEngine;

import data.Singleton;
import model.*;

import java.util.*;

import static util.LogUtil.addLineToLog;
import static util.LogUtil.initialAddToLog;
import static taskEngine.FitnessCalculator.calculateFitness;

public class TaskModifier {
    Random r = new Random();
    Singleton s = Singleton.getInstance();


    public void generateInitialConfiguration(List<Task> taskList) {
        //Calculate a starting wcet for tasks
        //Assign tasks to cores that will take them. And do it after a "least utilized" order.

        for (Task t : taskList) {
            t.setWcet(r.ints(1, 1, t.getPeriod()).sum());
            if (t instanceof TTtask) {
                s.PLATFORMMODEL.addTaskToCore(t, s.PLATFORMMODEL.getLeastUtilizedCore(s.PLATFORMMODEL.getCoresThatTaskCanBeAssignedTo(t), ScheduleType.TT).getId());
            } else {
                s.PLATFORMMODEL.addTaskToCore(t, s.PLATFORMMODEL.getLeastUtilizedCore(s.PLATFORMMODEL.getCoresThatTaskCanBeAssignedTo(t), ScheduleType.ET).getId());
            }
        }
    }

    public void modifyTasksUsingHeuristic() {

        PlatformModel currentSolution = new PlatformModel(s.PLATFORMMODEL);
        currentSolution.setFitness(calculateFitness(currentSolution));
        initialAddToLog(currentSolution);

        // Initial and final temperature
        double T = 1;

        // Simulated Annealing parameters

        // Temperature at which iteration terminates
        final double Tmin = .001;

        // Decrease in temperature
        final double alpha = 0.99;

        // Number of iterations of annealing
        // before decreasing temperature
        final int numIterations = 100;

        double minFitness = Double.MAX_VALUE;
        PlatformModel bestSolution = null;


        while (T > Tmin) {
            for (int i = 0; i < numIterations; i++) {

                if (calculateFitness(currentSolution) == 0.0) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = currentSolution;
                    T = Double.MIN_VALUE;
                    break;
                }

                if (currentSolution.getFitness() < minFitness) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = currentSolution;
                }

                PlatformModel newSolution = generateCandidateMove(currentSolution, new double[]{0.75, 0.25});
                newSolution.setFitness(calculateFitness(newSolution));

                double ap = Math.pow(Math.E, (currentSolution.getFitness() - newSolution.getFitness() / T));

                if (newSolution.getFitness() < currentSolution.getFitness()) {
                    addLineToLog(newSolution, new PlatformModel(currentSolution));
                    currentSolution = newSolution;
                } else if (ap < Math.random()) {
                    addLineToLog(newSolution, new PlatformModel(currentSolution));
                    currentSolution = newSolution;
                }
            }
            T *= alpha;
        }

        assert bestSolution != null;

        s.PLATFORMMODEL = bestSolution;

        //Move generator - from a state, generate a move to a new state.
        // - Define possible moves - return only one move
        //      - Change WCET of a task
        //      - Change core allocation of a task either move or swap


        //Fitness function
        // - Define fitness score
        // - Make methods to quickly calculate it and store it?
        // - In the beginning the util delta across cores
        //
//        double fitness = calculateFitness(candidateMove, s.TT_UTILIZATION);



        //SA approach
    }

    //Neighbourhood function
    private PlatformModel generateCandidateMove(PlatformModel currentModel, double [] moveTypeProbability) {
        PlatformModel candidateModel = new PlatformModel(currentModel);

        double moveDice = r.doubles(1, 0, Arrays.stream(moveTypeProbability).sum()).sum();
        Task randomTask = candidateModel.getRandomTask();

        //changeWCET of task
        if (moveDice <= moveTypeProbability[0]){
            if (randomTask instanceof TTtask) {
                if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateTTUtil() > s.TT_UTILIZATION) {
                    //Lower util
                    randomTask.setWcet(r.ints(1, 1, randomTask.getWcet() + 1).sum());
                } else if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateTTUtil() < s.TT_UTILIZATION) {
                    //raise util
                    randomTask.setWcet(r.ints(1, randomTask.getWcet() - 1, randomTask.getPeriod()).sum());
                }
            } else {
                if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateETUtil() > s.ET_UTILIZATION) {
                    //Lower util
                    randomTask.setWcet(r.ints(1, 1, randomTask.getWcet() + 1).sum());
                } else if (candidateModel.getCoreByTaskId(randomTask.getId()).calculateETUtil() < s.ET_UTILIZATION) {
                    //raise util
                    randomTask.setWcet(r.ints(1, 1, randomTask.getWcet() + 1 ).sum());
                }
            }

        } else if (moveDice > moveTypeProbability[0] && moveDice <= moveTypeProbability[0] + moveTypeProbability[1]) {
            //Move task
            List<Core> validCores = candidateModel.getCoresThatTaskCanBeAssignedTo(randomTask);
            Collections.shuffle(validCores);
            candidateModel.getLeastUtilizedCore(validCores, ScheduleType.TTET);
            String coreIdToMoveTo = candidateModel.getLeastUtilizedCore(validCores, ScheduleType.TTET).getId();
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
}
