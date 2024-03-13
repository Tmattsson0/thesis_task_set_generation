package model;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class Chain {

    private final String name;
    private int priority;
    private int latency;
    private List<Task> tasks;
    private double fitness;
    private int delay;
    Dictionary<String, Integer> dict;

    public Chain(String name) {
        this.name = name;
        this.priority = -1;
        this.tasks = new ArrayList<>();
        this.fitness = -1;
        this.latency = -1;
        this.delay = 0;
        this.dict = new Hashtable<>();
        dict.put("desiredNumTasksInChain", -1);
        dict.put("desiredNumOfHostTransitions", -1);
        dict.put("desiredNumOfLowToHighPeriodTransitions", -1);
        dict.put("desiredNumOfHighToLowPeriodTransitions", -1);
    }

    public Chain(Chain chain) {
        this.name = chain.getName();
        this.priority = chain.getPriority();
        this.latency = chain.getLatency();
        this.tasks = Task.deepCopyUsingCopyConstructor(chain.getTasks());
        this.fitness = chain.getFitness();
        this.dict = chain.getDict();
    }

    public static List<Chain> deepCopyUsingCopyConstructor(List<Chain> chains) {
        if (chains == null) {
            return new ArrayList<>();
        }
        return chains.stream().map(Chain::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Dictionary<String, Integer> getDict() {
        return dict;
    }

    public void setDict(Dictionary<String, Integer> dict) {
        this.dict = dict;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    //latency in milliseconds
    public void calculateAndSetLatency(double tightness){
        //low bound (sum(wcet))
        int lowBound = (int) Math.round(tasks.stream().mapToDouble(Task::getWcet).sum())/1000;

        //high bound (2*sum(period) - sum(wcet))
        int highBound = (int) Math.round((2 * tasks.stream().mapToDouble(Task::getPeriod).sum()) - (tasks.stream().mapToDouble(Task::getWcet).sum()/1000));

        if (tightness == 0) {
            setLatency(lowBound);
        } else if (tightness == 100) {
            setLatency(highBound);
        } else {
            //the tightness refers to where on the number line between the lobound and hibound the latency will be.
            //Example: if tightness is 33. Then the latency will be 33% along the numberline between lobound and hibound.
            //lobound 100, hibound 1000 and tightness 33 = ((1000 - 100) * 0.33) + 100 = 397.
            int tightLatency = (int) Math.round(((highBound - lowBound) * (tightness * 0.01)) + lowBound);
            setLatency(tightLatency);
        }
    }

    @Override
    public String toString() {
        return "Chain{" +
                "name='" + name + '\'' +
                ", priority=" + priority +
                ", latency=" + latency +
                ", fitness=" + fitness +
                ", dict=" + dict +
                ", tasks=" + tasks +
                '}';
    }
}
