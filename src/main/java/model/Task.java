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
}


//Todo: equals and toString