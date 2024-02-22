package main;

import data.Singleton;
import model.Task;
import taskEngine.ChainGenerator;
import taskEngine.TaskGenerator;
import taskEngine.TaskModifier;
import taskEngine.TaskModifierHC;
import util.ConfigInitializer;
import util.LogUtil;
import util.RandomUtil;
import util.XmlUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestRunner {

    //SA vs. HC vs. Steepest Ascent HC - task set size - time limit 5 minutes
    public void runTest(){

        //SA
        for (int i = 0; i < 10; i++) {
            int numberOfRuns = 5;
            int seed = i;
            RandomUtil.setRandom(new Random(seed));

            for (int j = 0; j < numberOfRuns; j++) {
                ConfigInitializer.initialize("./config/parameters_" + (j + 1) + ".json", "./config/configuration_file_4.xml");

                System.out.println("Doing the tasks");
                Instant startTasks = Instant.now();

                Singleton s = Singleton.getInstance();

                TaskGenerator t = new TaskGenerator();
                TaskModifier taskModifier = new TaskModifier();

                List<Task> tttasks = t.initializeTTtasks();
                List<Task> ettasks = t.initializeETtasks();

                List<Task> tasks = new ArrayList<>();
                tasks.addAll(tttasks);
                tasks.addAll(ettasks);

                taskModifier.generateInitialConfiguration(tasks);

                LogUtil.fileName = "task_set_gen_test_SA_param" + (j + 1) +"_seed_" + seed;
                LogUtil.deleteLogFile();

                taskModifier.modifyTasksUsingHeuristic();

                Instant endTasks = Instant.now();

//                System.out.println("Doing the Chains");
//                Instant startChains = Instant.now();
//                ChainGenerator chainGenerator = new ChainGenerator();
//                chainGenerator.initializeChains();
//                Instant endChains = Instant.now();

                int taskTimeTaken = (int) Duration.between(startTasks, endTasks).getSeconds();
//                int chainTimeTaken = (int) Duration.between(startChains, endChains).getSeconds();

                XmlUtil.writeTaskListWithUtilAndChains(s.PLATFORMMODEL, "task_set_gen_SA_param" + (j + 1) +"_seed_" + seed, taskTimeTaken, 0);

                System.out.println("Task generation. Time elapsed in seconds: " + taskTimeTaken);
//                System.out.println("Chain generation. Time elapsed in seconds: " + 0);
            }
        }

        //BasicHC
        for (int i = 0; i < 10; i++) {
            int numberOfRuns = 5;
            int seed = i;
            RandomUtil.setRandom(new Random(seed));

            for (int j = 0; j < numberOfRuns; j++) {
                ConfigInitializer.initialize("./config/parameters_" + (j + 1) + ".json", "./config/configuration_file_4.xml");

                System.out.println("Doing the tasks");
                Instant startTasks = Instant.now();

                Singleton s = Singleton.getInstance();

                TaskGenerator t = new TaskGenerator();
                TaskModifierHC taskModifierHC = new TaskModifierHC();

                List<Task> tttasks = t.initializeTTtasks();
                List<Task> ettasks = t.initializeETtasks();

                List<Task> tasks = new ArrayList<>();
                tasks.addAll(tttasks);
                tasks.addAll(ettasks);

                taskModifierHC.generateInitialConfiguration(tasks);

                LogUtil.fileName = "task_set_gen_test_HC_param" + (j + 1) +"_seed_" + seed;
                LogUtil.deleteLogFile();

                taskModifierHC.modifyTasksUsingHeuristicBasicHC();

                Instant endTasks = Instant.now();

//                System.out.println("Doing the Chains");
//                Instant startChains = Instant.now();
//                ChainGenerator chainGenerator = new ChainGenerator();
//                chainGenerator.initializeChains();
//                Instant endChains = Instant.now();

                int taskTimeTaken = (int) Duration.between(startTasks, endTasks).getSeconds();
//                int chainTimeTaken = (int) Duration.between(startChains, endChains).getSeconds();

                XmlUtil.writeTaskListWithUtilAndChains(s.PLATFORMMODEL, "task_set_gen_HC_param" + (j + 1) +"_seed_" + seed, taskTimeTaken, 0);

                System.out.println("Task generation. Time elapsed in seconds: " + taskTimeTaken);
//                System.out.println("Chain generation. Time elapsed in seconds: " + chainTimeTaken);
            }
        }

        //Steepest Ascent HC
        for (int i = 0; i < 10; i++) {
            int numberOfRuns = 5;
            int seed = i;
            RandomUtil.setRandom(new Random(seed));

            for (int j = 0; j < numberOfRuns; j++) {
                ConfigInitializer.initialize("./config/parameters_" + (j + 1) + ".json", "./config/configuration_file_4.xml");

                System.out.println("Doing the tasks");
                Instant startTasks = Instant.now();

                Singleton s = Singleton.getInstance();

                TaskGenerator t = new TaskGenerator();
                TaskModifierHC taskModifierHC = new TaskModifierHC();

                List<Task> tttasks = t.initializeTTtasks();
                List<Task> ettasks = t.initializeETtasks();

                List<Task> tasks = new ArrayList<>();
                tasks.addAll(tttasks);
                tasks.addAll(ettasks);

                taskModifierHC.generateInitialConfiguration(tasks);

                LogUtil.fileName = "task_set_gen_test_SAHC_param" + (j + 1) +"_seed_" + seed;
                LogUtil.deleteLogFile();

                taskModifierHC.modifyTasksUsingHeuristicSteepestAscentHC();

                Instant endTasks = Instant.now();

//                System.out.println("Doing the Chains");
//                Instant startChains = Instant.now();
//                ChainGenerator chainGenerator = new ChainGenerator();
//                chainGenerator.initializeChains();
//                Instant endChains = Instant.now();

                int taskTimeTaken = (int) Duration.between(startTasks, endTasks).getSeconds();
//                int chainTimeTaken = (int) Duration.between(startChains, endChains).getSeconds();

                XmlUtil.writeTaskListWithUtilAndChains(s.PLATFORMMODEL, "task_set_gen_SAHC_param" + (j + 1) +"_seed_" + seed, taskTimeTaken, 0);

                System.out.println("Task generation. Time elapsed in seconds: " + taskTimeTaken);
//                System.out.println("Chain generation. Time elapsed in seconds: " + chainTimeTaken);
            }
        }
    }
}
