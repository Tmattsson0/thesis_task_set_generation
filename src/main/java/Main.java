import taskEngine.TaskAssignmentManager;
import data.Singleton;
import model.*;
import taskEngine.TaskGenerator;
import taskEngine.TaskModifier;
import util.ConfigInitializer;

import java.util.List;


public class Main {
    public static void main(String[] args) {

        ConfigInitializer.initialize();

        Singleton s = Singleton.getInstance();

        TaskGenerator t = new TaskGenerator(s.NUM_OF_TT_TASKS, s.TT_UTILIZATION, s.PERIODS, s.coreAffinityDist, s.PLATFORMMODEL);

        TaskModifier taskModifier = new TaskModifier();

        List<TTtask> tasks = t.initializeTTtasks();

        taskModifier.generateInitialConfiguration(tasks);

        s.PLATFORMMODEL.getAllCores().forEach(System.out::println);

        System.out.println("Fitness: " + taskModifier.calculateFitness(s.PLATFORMMODEL, s.TT_UTILIZATION));

        taskModifier.modifyTasksUsingHeuristic();

        System.out.println("\n\n\n");

        s.PLATFORMMODEL.getAllCores().forEach(System.out::println);

        System.out.println("Fitness: " + taskModifier.calculateFitness(s.PLATFORMMODEL, s.TT_UTILIZATION));


    }
}