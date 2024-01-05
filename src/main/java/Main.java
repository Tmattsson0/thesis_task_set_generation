import data.Singleton;
import model.*;
import taskEngine.CoreAffinityDistributionTool;
import taskEngine.TaskGenerator;
import util.ConfigInitializer;

import java.util.Arrays;


public class Main {
    public static void main(String[] args) {

        ConfigInitializer.initialize();
//
        Singleton s = Singleton.getInstance();

//        System.out.println(Arrays.deepToString(CoreAffinityDistributionTool.specificCoreAffinity(s.NUM_OF_TT_TASKS, s.coreAffinityDist, s.PLATFORMMODEL.getAllCores())));



//
        TaskGenerator t = new TaskGenerator(s.NUM_OF_TT_TASKS, s.TT_UTILIZATION, s.PERIODS, s.coreAffinityDist, s.PLATFORMMODEL);

        s.TTtasks = t.initializeTTtasks();

        System.out.println(s.TTtasks);

//        System.out.println(Arrays.toString(CoreAffinityDistributionTool.calculateAmountsOfDifferentTypes(10, new double[]{0.75, 0.1, 0.15})));

    }


}