package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Chain {

    private final String name;
    private int priority;
    private int latency;
    private List<Task> tasks;

    public Chain(String name) {
        this.name = name;
        this.priority = -1;
    }

    public Chain(Chain chain) {
        this.name = chain.getName();
        this.priority = chain.getPriority();
        this.latency = chain.getLatency();
        this.tasks = Task.deepCopyUsingCopyConstructor(chain.getTasks());
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

    public void calculateAndSetLatency(double tightness){
        if (tightness == 0) {
            //lobound (sum(wcet))
            setLatency((int) Math.round(tasks.stream().mapToDouble(Task::getWcet).sum()));
        } else if (tightness == 100) {
            //hibound (2*sum(period) - sum(wcet))
            setLatency((int) (Math.round((2 * tasks.stream().mapToDouble(Task::getPeriod).sum()) - tasks.stream().mapToDouble(Task::getWcet).sum())));
        } else {
            //the tightness refers to where on the number line between the lobound and hibound the latency will be.
            //Example: if tightness is 33. Then the latency will be 33% along the numberline between lobound and hibound.
            //lobound 100, hibound 1000 and tightness 33 = ((1000 - 100) * 0.33) + 100 = 397.
            int lobound = (int) Math.round(tasks.stream().mapToDouble(Task::getWcet).sum());
            int hibound = (int) (Math.round((2 * tasks.stream().mapToDouble(Task::getPeriod).sum()) - tasks.stream().mapToDouble(Task::getWcet).sum()));
            int tightLatency = (int) Math.round(((hibound - lobound) * tightness) + lobound);
            setLatency(tightLatency);
        }
    }
}
