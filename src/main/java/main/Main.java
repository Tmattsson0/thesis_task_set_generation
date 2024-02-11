package main;

import taskEngine.ChainGenerator;
import data.Singleton;
import model.*;
import taskEngine.TaskGenerator;
import taskEngine.TaskModifier;
import util.ConfigInitializer;
import util.LogUtil;
import util.XmlUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        ConfigInitializer.initialize("config/parameters.json", "config/config_small.xml");

        Singleton s = Singleton.getInstance();
        TaskGenerator t = new TaskGenerator();
        TaskModifier taskModifier = new TaskModifier();

        List<Task> tttasks = t.initializeTTtasks();
        List<Task> ettasks = t.initializeETtasks();

        List<Task> tasks = new ArrayList<>();
        tasks.addAll(tttasks);
        tasks.addAll(ettasks);

        Instant start = Instant.now();

        taskModifier.generateInitialConfiguration(tasks);

        LogUtil.deleteLogFile();

        taskModifier.modifyTasksUsingHeuristic();

        ChainGenerator chainGenerator = new ChainGenerator();
        chainGenerator.initializeChains();

        Instant end = Instant.now();

        XmlUtil.writeTaskListWithUtilAndChains(s.PLATFORMMODEL, "test_newFitRand");

        System.out.println("Elapsed time in seconds: " + Duration.between(start, end).getSeconds());
    }
}