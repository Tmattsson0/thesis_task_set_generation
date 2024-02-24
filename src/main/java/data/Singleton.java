package data;

import model.DeadlineType;
import model.PlatformModel;

public class Singleton {

    private static Singleton single_instance = null;

    public int NUMBER_OF_HOSTS;
    public String SCHEDULE_TYPE;
    public boolean PREEMPTIBLE;
    public int NUM_OF_CORES;
    public int[] MICROTICK_VALUES;
    public double TT_UTILIZATION;
    public double ET_UTILIZATION;
    public int NUM_OF_TT_TASKS;
    public int NUM_OF_ET_TASKS;
    public double[][] PERIODS;
    public double[] ALLOWED_JITTER;
    public double[] RELEASE_TIME;
    public DeadlineType DEADLINE_TYPE;
    public int NUM_OF_CHAINS;
    public int NUM_OF_TASKS_IN_CHAINS;
    public int NUM_OF_LOW;
    public int NUM_OF_HIGH;
    public int NUM_OF_HOST_TRANSITIONS;
    public double LATENCY_TIGHTNESS;
    public PlatformModel PLATFORMMODEL;
    public double[] coreAffinityDist = {0.75, 0.1, 0.15};

    //Testing vars: "none", "simple", "current"
    public String variance;

    private Singleton() {}

    public static synchronized Singleton getInstance()
    {
        if (single_instance == null)
            single_instance = new Singleton();

        return single_instance;
    }
}
