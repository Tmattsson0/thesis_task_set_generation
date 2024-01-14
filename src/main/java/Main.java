import org.xml.sax.SAXException;
import taskEngine.TaskAssignmentManager;
import data.Singleton;
import model.*;
import taskEngine.TaskGenerator;
import taskEngine.TaskModifier;
import util.ConfigInitializer;
import util.XmlUtil;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) throws JAXBException, IOException, ParserConfigurationException, SAXException {

        ConfigInitializer.initialize();
//
        Singleton s = Singleton.getInstance();
//
        TaskGenerator t = new TaskGenerator(s.NUM_OF_TT_TASKS, s.TT_UTILIZATION, s.PERIODS, s.coreAffinityDist, s.PLATFORMMODEL);
//
        TaskModifier taskModifier = new TaskModifier();
//
        List<Task> tasks = t.initializeTTtasks();
//
        taskModifier.generateInitialConfiguration(tasks);
//
//        s.PLATFORMMODEL.getAllCores().forEach(System.out::println);
//
//        System.out.println("Fitness: " + taskModifier.calculateFitness(s.PLATFORMMODEL, s.TT_UTILIZATION));
//
        taskModifier.modifyTasksUsingHeuristic();
//
//        System.out.println("\n\n\n");
//
//        s.PLATFORMMODEL.getAllCores().forEach(System.out::println);
//
//        System.out.println("Fitness: " + taskModifier.calculateFitness(s.PLATFORMMODEL, s.TT_UTILIZATION));


//        s.PLATFORMMODEL = XmlUtil.readPlatformModelConfig("config/proposed_config_file.xml");
//
        s.PLATFORMMODEL.getAllCores().forEach(System.out::println);

//        XmlUtil.writePlatformModelConfig(s.PLATFORMMODEL.getTaskById("0"), "/Users/thomasmattsson/Documents/GitHub/thesis_task_set_generation/testCasesTest/2023-11-01_testParameterName/tasklist_proposed1.xml");
        XmlUtil.writeTaskListWithUtil
                (s.PLATFORMMODEL, "test");
    }
}