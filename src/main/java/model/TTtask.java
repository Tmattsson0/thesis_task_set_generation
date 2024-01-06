package model;

import java.util.Arrays;

public class TTtask extends Task {
    private int offset;
    private final TaskType taskType;
    private final int priority;

    public TTtask(String id, int period, DeadlineType deadlineType) {
        super(id, period, deadlineType);
        this.taskType = TaskType.TT;
        this.priority = 1;
        this.offset = -1;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + super.getId() + '\'' +
                ", name='" + super.getName() + '\'' +
                ", period=" + super.getPeriod() +
                ", deadline=" + super.getDeadline() +
                ", wcet=" + super.getWcet() +
                ", cpuId=" + super.getCpuId() +
                ", coreId=" + super.getCoreId() +
                ", maxJitter=" + super.getMaxJitter() +
                ", coreAffinity=" + Arrays.toString(super.getCoreAffinity()) +
                ", deadlineType=" + super.getDeadlineType() +
                ", offset=" + offset +
                ", taskType=" + taskType +
                ", priority=" + priority +
                "}";
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getPriority() {
        return priority;
    }
}
