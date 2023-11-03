import util.ParametersJsonReader;

public class Singleton {
    // Static variable reference of single_instance
    // of type Singleton
    private static Singleton single_instance = null;

    // Environment variables:
    public int NUMBER_OF_HOSTS;
    public String SCHEDULE_TYPE;
    public boolean PREEMPTIBLE;
    public int NUM_OF_CORES;
    public int MICROTICK_VALUES;
    public double TT_UTILIZATION;
    public double ET_UTILIZATION;
    public int NUM_OF_TT_TASKS;
    public int NUM_OF_ET_TASKS;
    public double[][] PERIODS;
    public double ALLOWED_JITTER;
    public double RELEASE_TIME;
    public String DEADLINE_TYPE;
    public int NUM_OF_CHAINS;
    public int NUM_OF_TASKS_IN_CHAINS;
    public int NUM_OF_LOW;
    public int NUM_OF_HIGH;
    public int NUM_OF_HOST_TRANSITIONS;
    public double LATENCY;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private Singleton() {
        ParametersJsonReader paramReader = new ParametersJsonReader("config/parameters.json");
        NUMBER_OF_HOSTS = paramReader.getNumOfHosts();
        SCHEDULE_TYPE = paramReader.getScheduleType();
        PREEMPTIBLE = paramReader.getIsPreemptible();
        NUM_OF_CORES = paramReader.getNumOfCores();
        MICROTICK_VALUES = paramReader.getMicrotickValues();
        TT_UTILIZATION = paramReader.getTtUtilization();
        ET_UTILIZATION = paramReader.getEtUtilization();
        NUM_OF_TT_TASKS = paramReader.getNumOfTtTasks();
        NUM_OF_ET_TASKS = paramReader.getNumOfEtTasks();
        PERIODS = paramReader.getPeriods();
        ALLOWED_JITTER = paramReader.getAllowedJitter();
        RELEASE_TIME = paramReader.getReleaseTime();
        DEADLINE_TYPE = paramReader.getDeadlineType();
        NUM_OF_CHAINS = paramReader.getNumOfChains();
        NUM_OF_TASKS_IN_CHAINS = paramReader.getNumOfTasksInChains();
        NUM_OF_LOW = paramReader.getNumOfLow();
        NUM_OF_HIGH = paramReader.getNumOfHigh();
        NUM_OF_HOST_TRANSITIONS = paramReader.getNumOfHostTransitions();
        LATENCY = paramReader.getLatency();
    }

    // Static method to create instance of Singleton class
    public static synchronized Singleton getInstance()
    {
        if (single_instance == null)
            single_instance = new Singleton();

        return single_instance;
    }
}
