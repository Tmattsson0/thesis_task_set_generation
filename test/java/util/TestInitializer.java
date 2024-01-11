package util;

import model.*;
import org.xml.sax.SAXException;
import testData.TestSingleton;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestInitializer {

    public static void initialize() {

        TestSingleton singleton = TestSingleton.getInstance();

        try {
            singleton.PLATFORMMODEL = XmlUtil.readPlatformModelConfig("config/proposed_config_file.xml");
            ParametersJsonReader paramReader = new ParametersJsonReader("config/test_parameters.json");

            singleton.NUMBER_OF_HOSTS = paramReader.getNumOfHosts();
            singleton.SCHEDULE_TYPE = paramReader.getScheduleType();
            singleton.PREEMPTIBLE = paramReader.getIsPreemptible();
            singleton.NUM_OF_CORES = paramReader.getNumOfCores();
            singleton.MICROTICK_VALUES = paramReader.getMicrotickValues();
            singleton.TT_UTILIZATION = paramReader.getTtUtilization();
            singleton.ET_UTILIZATION = paramReader.getEtUtilization();
            singleton.NUM_OF_TT_TASKS = paramReader.getNumOfTtTasks();
            singleton.NUM_OF_ET_TASKS = paramReader.getNumOfEtTasks();
            singleton.PERIODS = paramReader.getPeriods();
            singleton.ALLOWED_JITTER = paramReader.getAllowedJitter();
            singleton.RELEASE_TIME = paramReader.getReleaseTime();
            singleton.DEADLINE_TYPE = paramReader.getDeadlineType();
            singleton.NUM_OF_CHAINS = paramReader.getNumOfChains();
            singleton.NUM_OF_TASKS_IN_CHAINS = paramReader.getNumOfTasksInChains();
            singleton.NUM_OF_LOW = paramReader.getNumOfLow();
            singleton.NUM_OF_HIGH = paramReader.getNumOfHigh();
            singleton.NUM_OF_HOST_TRANSITIONS = paramReader.getNumOfHostTransitions();
            singleton.LATENCY = paramReader.getLatency();

        } catch (FileNotFoundException e) {
            initializeOnlyParametersFile();
        } catch (IOException | JAXBException e){
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initializeOnlyParametersFile() {
        TestSingleton singleton = TestSingleton.getInstance();

        try {

            ParametersJsonReader paramReader = new ParametersJsonReader("config/test_parameters.json");

            singleton.NUMBER_OF_HOSTS = paramReader.getNumOfHosts();
            singleton.SCHEDULE_TYPE = paramReader.getScheduleType();
            singleton.PREEMPTIBLE = paramReader.getIsPreemptible();
            singleton.NUM_OF_CORES = paramReader.getNumOfCores();
            singleton.MICROTICK_VALUES = paramReader.getMicrotickValues();
            singleton.TT_UTILIZATION = paramReader.getTtUtilization();
            singleton.ET_UTILIZATION = paramReader.getEtUtilization();
            singleton.NUM_OF_TT_TASKS = paramReader.getNumOfTtTasks();
            singleton.NUM_OF_ET_TASKS = paramReader.getNumOfEtTasks();
            singleton.PERIODS = paramReader.getPeriods();
            singleton.ALLOWED_JITTER = paramReader.getAllowedJitter();
            singleton.RELEASE_TIME = paramReader.getReleaseTime();
            singleton.DEADLINE_TYPE = paramReader.getDeadlineType();
            singleton.NUM_OF_CHAINS = paramReader.getNumOfChains();
            singleton.NUM_OF_TASKS_IN_CHAINS = paramReader.getNumOfTasksInChains();
            singleton.NUM_OF_LOW = paramReader.getNumOfLow();
            singleton.NUM_OF_HIGH = paramReader.getNumOfHigh();
            singleton.NUM_OF_HOST_TRANSITIONS = paramReader.getNumOfHostTransitions();
            singleton.LATENCY = paramReader.getLatency();

            singleton.PLATFORMMODEL = createPlatformModelFromParameters(ConfigInitializer.CoreDistributionStrategy.Homogenous);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static PlatformModel createPlatformModelFromParameters(ConfigInitializer.CoreDistributionStrategy coreDistribution) {
        TestSingleton singleton = TestSingleton.getInstance();
        if (coreDistribution.equals(ConfigInitializer.CoreDistributionStrategy.Homogenous)) {

            EndSystem endSystem = new EndSystem();

            CPUList cpuList = new CPUList(singleton.NUMBER_OF_HOSTS);

            endSystem.setCpuList(cpuList);

            if (singleton.NUM_OF_CORES % singleton.NUMBER_OF_HOSTS == 0){
                for (CPU cpu : cpuList.getCPUs()) {
                    CoreList coreList = new CoreList(cpu.getId(), singleton.NUM_OF_CORES / singleton.NUMBER_OF_HOSTS);
                    cpu.setCoreList(coreList);
                }

            } else {
                int remainder = singleton.NUM_OF_CORES % singleton.NUMBER_OF_HOSTS;
                //Create dict
                Map<Integer, Integer> coreMap = new HashMap<>();

                for (int i = 0; i < singleton.NUMBER_OF_HOSTS; i++) {
                    if (i < remainder) {
                        coreMap.put(i, 1 + (singleton.NUM_OF_CORES - (remainder)) / singleton.NUMBER_OF_HOSTS);
                    } else {
                        coreMap.put(i, (singleton.NUM_OF_CORES - (remainder)) / singleton.NUMBER_OF_HOSTS);
                    }
                }

                for (int i = 0; i < singleton.NUMBER_OF_HOSTS; i++) {
                    CoreList coreList = new CoreList(cpuList.getCPUs().get(i).getId(), coreMap.get(i));
                    cpuList.get(i).setCoreList(coreList);
                }
            }

            PlatformModel platformModel = new PlatformModel();

            platformModel.setEndSystems(List.of(endSystem));

            AddMicroticks(platformModel.getAllCores());

            return platformModel;

        } else if (coreDistribution.equals(ConfigInitializer.CoreDistributionStrategy.Random)){
            //Not yet implemented
            return null;
        } else {
            return null;
        }
    }

    private static void AddMicroticks(List<Core> allCores){
        TestSingleton s = TestSingleton.getInstance();

        if (s.MICROTICK_VALUES.length == allCores.size()){
            for (int i = 0; i < allCores.size(); i++) {
                allCores.get(i).setMicroTick(s.MICROTICK_VALUES[i]);
            }
        } else {
            for (Core core: allCores) {
                core.setMicroTick(s.MICROTICK_VALUES[(int) Math.floor(Math.random() * s.MICROTICK_VALUES.length)]);
            }
        }
    }

    enum CoreDistributionStrategy{
        Homogenous,
        Random
    }
}
