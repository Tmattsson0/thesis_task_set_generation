package taskEngine;

import data.Singleton;
import model.*;
import util.SpecificPeriodToll;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskGenerator {
    Random random = new Random();
    Singleton s = Singleton.getInstance();
    int numOfTTTasks = s.NUM_OF_TT_TASKS;
    int numOfETTasks = s.NUM_OF_ET_TASKS;
    double[][] periodsDist = s.PERIODS;
    double [] coreAffinityDist = s.coreAffinityDist;
    double[] allowedJitterDist = s.ALLOWED_JITTER;
    double[] releaseTimeDist = s.RELEASE_TIME;
    PlatformModel platform = s.PLATFORMMODEL;
    DeadlineType deadlineType = s.DEADLINE_TYPE;
    private final AtomicInteger seq = new AtomicInteger();

    public List<Task> initializeTTtasks() {
        List<Task> initialTTtasks = new ArrayList<>();
        int[] specificPeriods = SpecificPeriodToll.getSpecificPeriods(periodsDist, numOfTTTasks);
        String[][] specificCoreAffinity = CoreAffinityDistributionTool.specificCoreAffinity(numOfTTTasks,
                coreAffinityDist,
                platform.getAllCores()
                .stream()
                .filter(core -> core
                        .getScheduleType()
                        .getValue()
                        .contains("TT"))
                .toList());

        for (int i = 0; i < numOfTTTasks; i++) {
            Task temp = new TTtask(String.valueOf(seq.incrementAndGet()), specificPeriods[i], deadlineType);
            initialTTtasks.add(temp);
        }

        Collections.shuffle(initialTTtasks);

        for (int i = 0; i < numOfTTTasks; i++) {
            initialTTtasks.get(i).setCoreAffinity(specificCoreAffinity[i]);
            initialTTtasks.get(i).calculateAndSetMaxJitter(allowedJitterDist);
            ((TTtask) initialTTtasks.get(i)).calculateAndSetOffset(releaseTimeDist);
        }

        initialTTtasks.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getId())));

        return initialTTtasks;
    }

    public List<Task> initializeETtasks() {
        List<Task> initialETTasks = new ArrayList<>();
        int[] specificPeriods = SpecificPeriodToll.getSpecificPeriods(periodsDist, numOfETTasks);
        String[][] specificCoreAffinity = CoreAffinityDistributionTool.specificCoreAffinity(numOfETTasks,
                coreAffinityDist,
                platform.getAllCores()
                        .stream()
                        .filter(core -> core
                                .getScheduleType()
                                .getValue()
                                .contains("ET"))
                        .toList());

        for (int i = 0; i < numOfETTasks; i++) {
            Task temp = new ETtask(String.valueOf(seq.incrementAndGet()), specificPeriods[i], deadlineType);
            initialETTasks.add(temp);
        }

        Collections.shuffle(initialETTasks);

        for (int i = 0; i < numOfETTasks; i++) {
            initialETTasks.get(i).setCoreAffinity(specificCoreAffinity[i]);
            initialETTasks.get(i).calculateAndSetMaxJitter(allowedJitterDist);
        }

        initialETTasks.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getId())));

        return initialETTasks;
    }
}