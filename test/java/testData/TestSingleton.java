package testData;

import data.Singleton;
import model.DeadlineType;
import model.PlatformModel;

public class TestSingleton {
    // Static variable reference of single_instance
    // of type data.Singleton
    private static TestSingleton single_instance = null;

    // Environment variables:
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
    public double ALLOWED_JITTER;
    public double RELEASE_TIME;
    public DeadlineType DEADLINE_TYPE;
    public int NUM_OF_CHAINS;
    public int NUM_OF_TASKS_IN_CHAINS;
    public int NUM_OF_LOW;
    public int NUM_OF_HIGH;
    public int NUM_OF_HOST_TRANSITIONS;
    public double LATENCY;
    public PlatformModel PLATFORMMODEL;

    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself
    private TestSingleton() {

    }

    // Static method to create instance of data.Singleton class
    public static synchronized TestSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new TestSingleton();

        return single_instance;
    }
}
