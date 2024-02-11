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
import java.util.stream.Collectors;

public class ConfigInitializer {

    public static void initialize(String parameters, String config) {

        Singleton singleton = Singleton.getInstance();

        try {
            ParametersJsonReader paramReader = new ParametersJsonReader(parameters);

            singleton.PLATFORMMODEL = XmlUtil.readPlatformModelConfig(config);

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
            singleton.NUM_OF_CHAINS = normalizeNumOfChainsWithinBounds(singleton.NUM_OF_TT_TASKS + singleton.NUM_OF_ET_TASKS, paramReader.getNumOfChains());
            singleton.NUM_OF_TASKS_IN_CHAINS = paramReader.getNumOfTasksInChains();
            singleton.NUM_OF_LOW = paramReader.getNumOfLow();
            singleton.NUM_OF_HIGH = paramReader.getNumOfHigh();
            singleton.NUM_OF_HOST_TRANSITIONS = paramReader.getNumOfHostTransitions();
            singleton.LATENCY_TIGHTNESS = paramReader.getLatency();

        } catch (IOException | ParseException | ParserConfigurationException | SAXException | JAXBException e){
            throw new RuntimeException(e);
        }
    }

    private static void initialize(String parameters) {
        Singleton singleton = Singleton.getInstance();

        try {

            ParametersJsonReader paramReader = new ParametersJsonReader(parameters);

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
            singleton.NUM_OF_CHAINS = normalizeNumOfChainsWithinBounds(singleton.NUM_OF_TT_TASKS + singleton.NUM_OF_ET_TASKS, paramReader.getNumOfChains());
            singleton.NUM_OF_TASKS_IN_CHAINS = paramReader.getNumOfTasksInChains();
            singleton.NUM_OF_LOW = paramReader.getNumOfLow();
            singleton.NUM_OF_HIGH = paramReader.getNumOfHigh();
            singleton.NUM_OF_HOST_TRANSITIONS = paramReader.getNumOfHostTransitions();
            singleton.LATENCY_TIGHTNESS = paramReader.getLatency();


            singleton.PLATFORMMODEL = createPlatformModelFromParameters(CoreDistributionStrategy.Homogenous);

        } catch (ParseException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static PlatformModel createPlatformModelFromParameters(CoreDistributionStrategy coreDistribution) {
        Singleton singleton = Singleton.getInstance();

        if (coreDistribution.equals(CoreDistributionStrategy.Homogenous)) {

            List<EndSystem> endSystems = new ArrayList<>();

            for (int i = 0; i < singleton.NUMBER_OF_HOSTS; i++) {
                EndSystem endSystem = new EndSystem();
                endSystem.setId("ES" + i);
                endSystems.add(endSystem);
            }

            for (EndSystem es : endSystems) {
                List<CPU> cpuList = new ArrayList<>();
                CPU cpu = new CPU("Cpu " + endSystems.indexOf(es) + 1, getCharForNumber(endSystems.indexOf(es) + 1));
                cpuList.add(cpu);
                es.setCpuList(cpuList);
            }

            if (singleton.NUM_OF_CORES % singleton.NUMBER_OF_HOSTS == 0){
                for (EndSystem es : endSystems) {
                    List<Core> coreList = new ArrayList<>();

                    for (int i = 0; i < singleton.NUM_OF_CORES / singleton.NUMBER_OF_HOSTS; i++){
                        Core tempCore = new Core("Core" + (i + 1), es.getCpus().get(0).getId() + (i + 1), -1, ScheduleType.TTET);
                        coreList.add(tempCore);
                    }

                    es.getCpus().get(0).setCoreList(coreList);
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
                        Core tempCore = new Core("Core" + (j + 1), endSystems.get(i).getCpus().get(0).getId() + (j + 1), -1, ScheduleType.TTET);
                        coreList.add(tempCore);
                    }

                    endSystems.get(i).getCpus().get(0).setCoreList(coreList);
                }
            }

            PlatformModel platformModel = new PlatformModel();

            platformModel.setEndSystems(endSystems);

            addMicroticks(platformModel.getAllCores());

            Topology topology = new Topology();
            Switch sw = new Switch();

            sw.setConnections(new ArrayList<>());
            sw.setConnectedSystemIds(new ArrayList<>(platformModel.getEndSystems().stream().map(EndSystem::getId).collect(Collectors.toList())));

            topology.setSwitches(new ArrayList<>(List.of(sw)));

            platformModel.setTopology(topology);

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
                core.setMicroTick(s.MICROTICK_VALUES[(int) Math.floor(RandomUtil.getRandom().doubles(1, 0, 1).sum() * s.MICROTICK_VALUES.length)]);
            }
        }
    }

    private static int normalizeNumOfChainsWithinBounds(int numOfTotalTasks, int numOfChainsParam) {
        if (Math.round(numOfTotalTasks * 0.3) < numOfChainsParam) {
            System.out.println("Number of desired chains: " + numOfChainsParam + " is too high. Regulating to the maximum of 30% of total tasks: " + (Math.round(numOfTotalTasks * 0.3)));
            return (int) Math.round(numOfTotalTasks * 0.3);
        } else {
            return numOfChainsParam;
        }
    }

    private static String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'A' - 1)) : null;
    }

    enum CoreDistributionStrategy{
        Homogenous,
        Random
    }

}



