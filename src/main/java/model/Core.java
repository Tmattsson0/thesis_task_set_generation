package model;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class Core {
    private final String name;
    private final String id;
    private int microTick;
    private ScheduleType scheduleType;
    private List<Task> tasks = new ArrayList<>();

    public Core(String name, String id, int microTick, ScheduleType scheduleType) {
        this.name = name;
        this.id = id;
        this.microTick = microTick;
        this.scheduleType = scheduleType;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }

    @XmlElement(name = "Id")
    public String getId() {
        return id;
    }

    @XmlElement(name = "MicroTick")
    public int getMicroTick() {
        return microTick;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setMicroTick(int microTick) {
        this.microTick = microTick;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void deleteTask(String taskId) {
        tasks.remove(tasks.stream().filter(task -> taskId.equals(task.getId())).findAny().orElse(null));
    }

    public Task getTask(String id) {
        return tasks.stream().filter(task -> id.equals(task.getId())).findAny().orElse(null);
    }

    public boolean containsTask(String id){
        return tasks.stream().anyMatch(task -> task.getId().equals(id));
    }

    @Override
    public String toString() {
        return "Core{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", microTick=" + microTick +
                ", scheduleType=" + scheduleType +
                ", tasks=" + tasks.toString() +
                '}';
    }

    public double calculateUtil() {
        if (tasks.isEmpty()){
            return 0.0;
        } else {
            double util = 0.0;
            for (Task t : tasks){
                util += t.calculateUtil();
            }
            return new BigDecimal(util).setScale(3, RoundingMode.HALF_UP).doubleValue();
        }
    }
}
