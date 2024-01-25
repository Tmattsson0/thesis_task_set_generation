package util;

import data.Singleton;
import model.*;
import org.xml.sax.SAXException;


import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class ConfigInitializer {

    public static void initialize() {

        Singleton singleton = Singleton.getInstance();

        try {
            singleton.PLATFORMMODEL = XmlUtil.readPlatformModelConfig("config/config_small.xml");

            ParametersJsonReader paramReader = new ParametersJsonReader("config/parameters.json");

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
        } catch (ParseException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeOnlyParametersFile() {
        Singleton singleton = Singleton.getInstance();

        try {

        ParametersJsonReader paramReader = new ParametersJsonReader("config/parameters.json");

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

        singleton.PLATFORMMODEL = createPlatformModelFromParameters(CoreDistributionStrategy.Homogenous);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static PlatformModel createPlatformModelFromParameters(CoreDistributionStrategy coreDistribution) {
        Singleton singleton = Singleton.getInstance();
        if (coreDistribution.equals(CoreDistributionStrategy.Homogenous)) {

            EndSystem endSystem = new EndSystem();

            List<CPU> cpuList = new ArrayList<>();

            endSystem.setCpuList(cpuList);

            if (singleton.NUM_OF_CORES % singleton.NUMBER_OF_HOSTS == 0){
                for (CPU cpu : cpuList) {
                    List<Core> coreList = new ArrayList<>();

                    for (int i = 0; i < singleton.NUM_OF_CORES / singleton.NUMBER_OF_HOSTS; i++){
                        Core tempCore = new Core("Core" + (i + 1), cpu.getId() + (i + 1), -1, ScheduleType.TTET);
                        coreList.add(tempCore);
                    }

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
                    List<Core> coreList = new ArrayList<>();

                    for (int j = 0; j < coreMap.get(i); j++){
                        Core tempCore = new Core("Core" + (j + 1), cpuList.get(i).getId() + (j + 1), -1, ScheduleType.TTET);
                        coreList.add(tempCore);
                    }

                    cpuList.get(i).setCoreList(coreList);
                }
            }

            PlatformModel platformModel = new PlatformModel();

            platformModel.setEndSystems(List.of(endSystem));

            addMicroticks(platformModel.getAllCores());

            return platformModel;

        } else if (coreDistribution.equals(CoreDistributionStrategy.Random)){
            //Not yet implemented
            return null;
        } else {
            return null;
        }
    }

    private static void addMicroticks(List<Core> allCores){
        Singleton s = Singleton.getInstance();

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



