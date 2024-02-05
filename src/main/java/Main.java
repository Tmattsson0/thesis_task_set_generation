import org.xml.sax.SAXException;
import taskEngine.ChainGenerator;
import taskEngine.TaskAssignmentManager;
import data.Singleton;
import model.*;
import taskEngine.TaskGenerator;
import taskEngine.TaskModifier;
import util.ConfigInitializer;
import util.LogUtil;
import util.XmlUtil;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        ConfigInitializer.initialize();

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
        Instant end = Instant.now();



        ChainGenerator chainGenerator = new ChainGenerator();
        chainGenerator.initializeChains();

        XmlUtil.writeTaskListWithUtilAndChains(s.PLATFORMMODEL, "test_newFitRand");

        System.out.println("Elapsed time in seconds: " + Duration.between(start, end).getSeconds());
    }
}