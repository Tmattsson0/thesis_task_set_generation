import data.Singleton;
import model.DeadlineType;
import model.PlatformModel;
import model.Task;
import taskEngine.TaskGenerator;
import taskEngine.WcetGenerator;
import util.ConfigInitializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        ConfigInitializer.initialize();

        Singleton s = Singleton.getInstance();

        TaskGenerator t = new TaskGenerator(s.NUM_OF_TT_TASKS, s.TT_UTILIZATION, s.PERIODS);

        List<Task> tasks = t.genTTTaskSet(s.NUM_OF_TT_TASKS, s.PERIODS, new DeadlineType());

        System.out.println(tasks);

    }
}