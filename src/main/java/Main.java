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

        Singleton s = Singleton.getInstance();

        TaskGenerator t = new TaskGenerator(s.NUM_OF_TT_TASKS, s.TT_UTILIZATION, s.PERIODS, s.coreAffinityDist, s.PLATFORMMODEL);

        TaskModifier taskModifier = new TaskModifier();

        List<Task> tasks = t.initializeTTtasks();

        taskModifier.generateInitialConfiguration(tasks);

        taskModifier.modifyTasksUsingHeuristic();

        XmlUtil.writeTaskListWithUtil(s.PLATFORMMODEL, "test");
    }
}