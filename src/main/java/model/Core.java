package model;

import util.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public Core(Core core) {
        this.name = core.getName();
        this.id = core.getId();
        this.microTick = core.getMicroTick();
        this.scheduleType = core.getScheduleType();
        this.tasks = Task.deepCopyUsingCopyConstructor(core.getTasks());
    }

    public static List<Core> deepCopyUsingCopyConstructor(List<Core> coreList) {
        return coreList.stream().map(Core::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

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

    public List<Task> getTasks(ScheduleType scheduleType) {
        if (scheduleType == ScheduleType.NONE || scheduleType == ScheduleType.TTET){
            return getTasks();
        } else if (scheduleType == ScheduleType.TT) {
            return getTasks().stream().filter(task -> task instanceof TTtask).toList();
        } else {
            return getTasks().stream().filter(task -> task instanceof ETtask).toList();
        }
    }

    public Task getRandomTask(ScheduleType scheduleType){
        if (scheduleType == ScheduleType.NONE || scheduleType == ScheduleType.TTET){
            return getTasks().get(RandomUtil.getRandom().ints(1, 0, getTasks().size()).sum());
        } else if (scheduleType == ScheduleType.TT) {
            List<Task> temp = getTasks(ScheduleType.TT);
            return temp.get(RandomUtil.getRandom().ints(1, 0, temp.size()).sum());
        } else {
            List<Task> temp = getTasks(ScheduleType.ET);
            return temp.get(RandomUtil.getRandom().ints(1, 0, temp.size()).sum());        }
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

    public double calculateTTUtil() {
        if (tasks.isEmpty()){
            return 0.0;
        } else {
            double util = 0.0;
            for (Task t : tasks){
                if (t instanceof TTtask) {
                    util += t.calculateUtil();
                }
            }
            return new BigDecimal(util).setScale(3, RoundingMode.HALF_UP).doubleValue();
        }
    }

    public double calculateETUtil() {
        if (tasks.isEmpty()){
            return 0.0;
        } else {
            double util = 0.0;
            for (Task t : tasks){
                if (t instanceof ETtask) {
                    util += t.calculateUtil();
                }
            }
            return new BigDecimal(util).setScale(3, RoundingMode.HALF_UP).doubleValue();
        }
    }
}
