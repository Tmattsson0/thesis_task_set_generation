package model;

public class Task {

    private String name;
    private int duration;
    private int period;
    private TaskType taskType;
    private int priority;
    private int deadline;

    public Task(String name, int duration, int period, TaskType taskType, int priority, int deadline) {
        this.name = name;
        this.duration = duration;
        this.period = period;
        this.taskType = taskType;
        this.priority = priority;
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", period=" + period +
                ", taskType=" + taskType +
                ", priority=" + priority +
                ", deadline=" + deadline +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
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

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }
}


//Todo: equals and toString