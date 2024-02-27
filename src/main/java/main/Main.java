package main;

import taskEngine.ChainGenerator;
import data.Singleton;
import model.*;
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


public class Main {
    public static void main(String[] args) {

         RandomUtil.setRandom(new Random(0));
        //arg[0] = "parameters.json"
        //arg[1] = "config.xml"
        //For using config file:

        if (args.length == 1 && args[0].equals("parameters.json")) {
            ConfigInitializer.initialize("./config/parameters.json");
        } else if (args.length == 2 && args[0].equals("parameters.json")) {
            ConfigInitializer.initialize("./config/" + args[0], "./config/" + args[1]);
        } else {
            throw new IllegalArgumentException("File names not correct.");
        }

        System.out.println("Doing the tasks");
        Instant startTasks= Instant.now();

        Singleton s = Singleton.getInstance();
        TaskGenerator t = new TaskGenerator();
        TaskModifier taskModifier = new TaskModifier();
        TaskModifierHC taskModifierHC = new TaskModifierHC();

        List<Task> tttasks = t.initializeTTtasks();
        List<Task> ettasks = t.initializeETtasks();

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(tttasks);
        tasks.addAll(ettasks);



        taskModifier.generateInitialConfiguration(tasks);
//        taskModifierHC.generateInitialConfiguration(tasks);

        LogUtil.fileName = "task-set_test";
        LogUtil.deleteLogFile();

        taskModifier.modifyTasksUsingHeuristic();
//        taskModifierHC.modifyTasksUsingHeuristicBasicHC();
//        taskModifierHC.modifyTasksUsingHeuristicSteepestAscentHC();

        Instant endTasks= Instant.now();

        System.out.println("Doing the Chains");
        Instant startChains = Instant.now();
        ChainGenerator chainGenerator = new ChainGenerator();
        chainGenerator.initializeChains();

//        s.PLATFORMMODEL.getChains().forEach(System.out::println);
        Instant endChains = Instant.now();

        int taskTimeTaken = (int) Duration.between(startTasks, endTasks).getSeconds();
        int chainTimeTaken = (int) Duration.between(startChains, endChains).getSeconds();

        XmlUtil.writeTaskListWithUtilAndChains(s.PLATFORMMODEL, "task_set_ChainTest", taskTimeTaken, chainTimeTaken);

        System.out.println("Task generation. Time elapsed in seconds: " + taskTimeTaken);
        System.out.println("Chain generation. Time elapsed in seconds: " + chainTimeTaken);
    }
}