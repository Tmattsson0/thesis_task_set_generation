package main;

import taskEngine.ChainGenerator;
import data.Singleton;
import model.*;
import taskEngine.TaskGenerator;
import taskEngine.TaskModifier;
import taskEngine.TaskModifierHC;
import util.ConfigInitializer;
import util.LogUtil;
import util.XmlUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
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


        //For using only parameters file.
//        ConfigInitializer.initialize("./config/parameters.json");

        Singleton s = Singleton.getInstance();
        TaskGenerator t = new TaskGenerator();
        TaskModifier taskModifier = new TaskModifier();
        TaskModifierHC taskModifierHC = new TaskModifierHC();

        List<Task> tttasks = t.initializeTTtasks();
        List<Task> ettasks = t.initializeETtasks();

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(tttasks);
        tasks.addAll(ettasks);

        Instant start = Instant.now();

        taskModifierHC.generateInitialConfiguration(tasks);

        LogUtil.deleteLogFile();

//        taskModifierHC.modifyTasksUsingHeuristicBasicHC();
        taskModifierHC.modifyTasksUsingHeuristicSteepestAscentHC();

        ChainGenerator chainGenerator = new ChainGenerator();
        chainGenerator.initializeChains();

        Instant end = Instant.now();

        XmlUtil.writeTaskListWithUtilAndChains(s.PLATFORMMODEL, "task_set2");

        System.out.println("Elapsed time in seconds: " + Duration.between(start, end).getSeconds());
    }
}