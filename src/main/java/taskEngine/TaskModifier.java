package taskEngine;

import data.Singleton;
import model.Core;
import model.PlatformModel;
import model.TTtask;
import model.Task;

import java.util.*;

public class TaskModifier {
    //The heuristic class. Should access from singleton and parameters should be not be list of task.
    Random r = new Random();
    double utilization;
    int numOfTasks;
    Singleton s = Singleton.getInstance();

    public void generateInitialConfiguration(List<TTtask> taskList) {
        //Calculate a starting wcet for tasks
        //Assign tasks to cores that will take them. And do it after a "least utilized" order.

        for (TTtask t : taskList) {
            t.setWcet(r.ints(1, 1, t.getPeriod()).sum());
            s.PLATFORMMODEL.addTaskToCore(t, s.PLATFORMMODEL.getLeastUtilizedCore(s.PLATFORMMODEL.getCoresThatTaskCanBeAssignedTo(t)).getId());
        }
    }

    public void modifyTasksUsingHeuristic(){
        //Move generator - from a state, generate a move to a new state.
        // - Define possible moves - return only one move
        //      - Change WCET of a task
        //      - Change core allocation of a task either move or swap

        generateCandidateMove(s.PLATFORMMODEL, new double[]{0.5, 0.25, 0.25});

        //Fitness function
        // - Define fitness score
        // - Make methods to quickly calculate it and store it?
        // - In the beginning the util delta across cores
        //
        //SA approach
    }

    private PlatformModel generateCandidateMove(PlatformModel currentModel, double [] moveTypeProbability) {
        PlatformModel candidateModel = currentModel;
        double moveDice = r.doubles(1, 0, Arrays.stream(moveTypeProbability).sum()).sum();
        Task randomTask = candidateModel.getAllCores().stream().map(Core::getTasks).flatMap(Collection::stream).toList().get(r.ints(1, 0, numOfTasks).sum());

        //changeWCET of task
        if (moveDice <= moveTypeProbability[0]){
            //Common sense rules that wcet cannot be more than period.
            if ((moveDice > 0.5 || randomTask.getWcet() == 1) && randomTask.getWcet() != randomTask.getPeriod()){
                randomTask.setWcet(randomTask.getWcet() + 1);
            } else if (moveDice <= 0.5 && randomTask.getWcet() > 1){
                randomTask.setWcet(randomTask.getWcet() - 1);
            } else {
                randomTask.setWcet(randomTask.getWcet());
            }

        } else if (moveDice > moveTypeProbability[0] && moveDice <= moveTypeProbability[0] + moveTypeProbability[1]) {
        //Move task
            List<Core> validCores = s.PLATFORMMODEL.getCoresThatTaskCanBeAssignedTo(randomTask);
            Collections.shuffle(validCores);
            String coreIdToMoveTo = validCores.get(0).getId();


        } else {
        //Swap task
            //Needs logic for making "legal swaps"
        }

        return candidateModel;
    }
}
