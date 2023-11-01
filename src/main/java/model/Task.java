package model;

public class Task {
    private String id;
    private String name;
    private int wcet;
    private int period;
    private int deadline;
    private int maxJitter;
    private int offset;
    private int cpuId;
    private int coreId;
    private TaskType taskType;
    private int priority;

//    public Task(String name, int duration, int period, TaskType taskType, int priority, int deadline) {
//        this.name = name;
//        this.wcet = duration;
//        this.period = period;
//        this.taskType = taskType;
//        this.priority = priority;
//        this.deadline = deadline;
//    }

    public Task(String id, String name, int wcet, int period, int deadline, int maxJitter, int offset, int cpuId, int coreId, TaskType taskType, int priority) {
        this.id = id;
        this.name = name;
        this.wcet = wcet;
        this.period = period;
        this.deadline = deadline;
        this.maxJitter = maxJitter;
        this.offset = offset;
        this.cpuId = cpuId;
        this.coreId = coreId;
        this.taskType = taskType;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWcet() {
        return wcet;
    }

    public void setWcet(int wcet) {
        this.wcet = wcet;
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

    public int getMaxJitter() {
        return maxJitter;
    }

    public void setMaxJitter(int maxJitter) {
        this.maxJitter = maxJitter;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCpuId() {
        return cpuId;
    }

    public void setCpuId(int cpuId) {
        this.cpuId = cpuId;
    }

    public int getCoreId() {
        return coreId;
    }

    public void setCoreId(int coreId) {
        this.coreId = coreId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", wcet=" + wcet +
                ", period=" + period +
                ", deadline=" + deadline +
                ", maxJitter=" + maxJitter +
                ", offset=" + offset +
                ", cpuId=" + cpuId +
                ", coreId=" + coreId +
                ", taskType=" + taskType +
                ", priority=" + priority +
                '}';
    }
}


//Todo: equals and toString