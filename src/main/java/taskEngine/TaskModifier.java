package taskEngine;

import data.Singleton;
import model.Core;
import model.PlatformModel;
import model.Task;

import java.util.*;

import static util.LogUtil.addLineToLog;
import static util.LogUtil.initialAddToLog;

public class TaskModifier {
    //The heuristic class. Should access from singleton and parameters should be not be list of task.
    Random r = new Random();
    double utilization;
    int numOfTasks;
    Singleton s = Singleton.getInstance();


    public void generateInitialConfiguration(List<Task> taskList) {
        //Calculate a starting wcet for tasks
        //Assign tasks to cores that will take them. And do it after a "least utilized" order.

        for (Task t : taskList) {
            t.setWcet(r.ints(1, 1, t.getPeriod()).sum());
            s.PLATFORMMODEL.addTaskToCore(t, s.PLATFORMMODEL.getLeastUtilizedCore(s.PLATFORMMODEL.getCoresThatTaskCanBeAssignedTo(t)).getId());
        }
    }

    public void modifyTasksUsingHeuristic() {

        PlatformModel currentSolution;
        currentSolution = s.PLATFORMMODEL;
        currentSolution.setFitness(calculateFitness(currentSolution, s.TT_UTILIZATION));
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

                if (calculateFitness(currentSolution, s.TT_UTILIZATION) == 0.0) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = currentSolution;
                    T = Double.MIN_VALUE;
                    break;
                }

                if (currentSolution.getFitness() < minFitness) {
                    minFitness = currentSolution.getFitness();
                    bestSolution = currentSolution;
                }

                PlatformModel newSolution = generateCandidateMove(s.PLATFORMMODEL, new double[]{0.75, 0.25});
                newSolution.setFitness(calculateFitness(newSolution, s.TT_UTILIZATION));

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

    private PlatformModel generateCandidateMove(PlatformModel currentModel, double [] moveTypeProbability) {
        PlatformModel candidateModel = new PlatformModel(currentModel);

        double moveDice = r.doubles(1, 0, Arrays.stream(moveTypeProbability).sum()).sum();
        Task randomTask = candidateModel.getAllCores().stream().map(Core::getTasks).flatMap(Collection::stream).toList().get(r.ints(1, 0, s.NUM_OF_TT_TASKS).sum());

        //todo find better than just +- 1
        //changeWCET of task
        if (moveDice <= moveTypeProbability[0]){
            int moveSize = 10;
            //Common sense rules that wcet cannot be more than period.
            if ((moveDice > 0.5 || randomTask.getWcet() == 1) && randomTask.getWcet() < randomTask.getPeriod() - moveSize){
                randomTask.setWcet(randomTask.getWcet() + moveSize);
            } else if (moveDice <= 0.5 && randomTask.getWcet() > moveSize){
                randomTask.setWcet(randomTask.getWcet() - moveSize);
            } else {
                randomTask.setWcet(randomTask.getWcet());
            }

        } else if (moveDice > moveTypeProbability[0] && moveDice <= moveTypeProbability[0] + moveTypeProbability[1]) {
            //Move task
            List<Core> validCores = candidateModel.getCoresThatTaskCanBeAssignedTo(randomTask);
            Collections.shuffle(validCores);
            String coreIdToMoveTo = validCores.get(0).getId();
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

    public double calculateFitness(PlatformModel candidate, double desiredUtilization){

        double utilDelta = 0;

        for (Core c : candidate.getAllCores()){
            utilDelta += Math.abs(c.calculateUtil() - desiredUtilization);
        }

        return utilDelta;
    }
}
