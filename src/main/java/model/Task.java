package model;

import java.util.Arrays;

public class Task {
    private final String id;
    private final String name;
    private int period; //MIT for ET tasks
    private int deadline;
    private int wcet;
    private String cpuId;
    private String coreId;
    private int maxJitter;
    private String[] coreAffinity;
    private DeadlineType deadlineType;

    public Task(String id, int period, DeadlineType deadlineType) {
        this.id = id;
        this.name = generateName(id);
        this.period = period;
        this.deadlineType = deadlineType;
        this.deadline = generateDeadline(deadlineType);
        this.wcet = -1;
        this.cpuId = "";
        this.coreId = "";
        this.maxJitter = -1;
        this.coreAffinity = new String[]{};
    }

    private int generateDeadline(DeadlineType deadlineType) {
        if (deadlineType.isImplicit()) {
            return this.period;
        } else {
            return deadlineType.calculateDeadline(this.period);
        }
    }

    private String generateName(String id) {
        return "Task" + id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public DeadlineType getDeadlineType() {
        return deadlineType;
    }

    public void setDeadlineType(DeadlineType deadlineType) {
        this.deadlineType = deadlineType;
    }

    public int getWcet() {
        return wcet;
    }

    public void setWcet(int wcet) {
        this.wcet = wcet;
    }

    public String getCpuId() {
        return cpuId;
    }

    public void setCpuId(String cpuId) {
        this.cpuId = cpuId;
    }

    public String getCoreId() {
        return coreId;
    }

    public void setCoreId(String coreId) {
        this.coreId = coreId;
    }

    public int getMaxJitter() {
        return maxJitter;
    }

    public void setMaxJitter(int maxJitter) {
        this.maxJitter = maxJitter;
    }

    public String[] getCoreAffinity() {
        return coreAffinity;
    }

    public void setCoreAffinity(String[] coreAffinity) {
        this.coreAffinity = coreAffinity;
    }
}


//Todo: equals and toString