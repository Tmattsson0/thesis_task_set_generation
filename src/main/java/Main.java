import data.Singleton;
import model.PlatformModel;
import model.Task;
import util.ConfigInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {

//        XmlUtil.writePlatformModelConfig();

        ConfigInitializer.initialize();

        Singleton s = Singleton.getInstance();

        TaskGenerator t = new TaskGenerator();
        List<Task> ttTasks;
        ttTasks = t.genTTTaskSet(s.TT_UTILIZATION, s.NUM_OF_TT_TASKS, s.PERIODS, s.DEADLINE_TYPE);
        System.out.println(ttTasks);

        List<Task> etTasks;
        etTasks = t.genETTaskSet(s.ET_UTILIZATION, s.NUM_OF_ET_TASKS, s.PERIODS, s.DEADLINE_TYPE);
        System.out.println(etTasks);

//        System.out.println(singleton.PLATFORMMODEL);
//
//        System.out.println("NUMBER_OF_HOSTS: " + singleton.NUMBER_OF_HOSTS);
//        System.out.println("SCHEDULE_TYPE: " + singleton.SCHEDULE_TYPE);
//        System.out.println("PREEMPTIBLE: " + singleton.PREEMPTIBLE);
//        System.out.println("NUM_OF_CORES: " + singleton.NUM_OF_CORES);
//        System.out.println("MICROTICK_VALUES: " + singleton.MICROTICK_VALUES);
//        System.out.println("TT_UTILIZATION: " + singleton.TT_UTILIZATION);
//        System.out.println("ET_UTILIZATION: " + singleton.ET_UTILIZATION);
//        System.out.println("NUM_OF_TT_TASKS: " + singleton.NUM_OF_TT_TASKS);
//        System.out.println("NUM_OF_ET_TASKS: " + singleton.NUM_OF_ET_TASKS);
//        System.out.println("PERIODS: " + Arrays.deepToString(singleton.PERIODS));
//        System.out.println("ALLOWED_JITTER: " + singleton.ALLOWED_JITTER);
//        System.out.println("RELEASE_TIME: " + singleton.RELEASE_TIME);
//        System.out.println("DEADLINE_TYPE: " + singleton.DEADLINE_TYPE);
//        System.out.println("NUM_OF_CHAINS: " + singleton.NUM_OF_CHAINS);
//        System.out.println("NUM_OF_TASKS_IN_CHAINS: " + singleton.NUM_OF_TASKS_IN_CHAINS);
//        System.out.println("NUM_OF_LOW: " + singleton.NUM_OF_LOW);
//        System.out.println("NUM_OF_HIGH: " + singleton.NUM_OF_HIGH);
//        System.out.println("NUM_OF_HOST_TRANSITIONS: " + singleton.NUM_OF_HOST_TRANSITIONS);
//        System.out.println("LATENCY: " + singleton.LATENCY);
    }
}