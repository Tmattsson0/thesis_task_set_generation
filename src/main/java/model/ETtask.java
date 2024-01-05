package model;

import java.util.Arrays;

public class ETtask extends Task {
    private final TaskType taskType;
    private int priority;

    //MIT = period
    public ETtask(String id, int MIT, DeadlineType deadlineType) {
        super(id, MIT, deadlineType);
        this.taskType = TaskType.ET;
        this.priority = -1;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + super.getId() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", MIT=" + super.getPeriod() +
                ", deadline=" + super.getDeadline() +
                ", wcet=" + super.getWcet() +
                ", cpuId=" + super.getCpuId() +
                ", coreId=" + super.getCoreId() +
                ", maxJitter=" + super.getMaxJitter() +
                ", coreAffinity=" + Arrays.toString(super.getCoreAffinity()) +
                ", deadlineType=" + super.getDeadlineType() +
                ", taskType=" + taskType +
                ", priority=" + priority +
                '}';
    }
}
