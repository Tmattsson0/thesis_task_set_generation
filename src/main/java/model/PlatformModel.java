package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement(name = "PlatformModel")
public class PlatformModel {
    private CPUList cpus;

    public PlatformModel(CPUList cpus) {
        this.cpus = cpus;
    }

    public PlatformModel() {
    }

    @XmlElement(name = "Cpus")
    public CPUList getCpus() {
        return cpus;
    }

    public List<Core> getAllCores(){
        List<Core> allCores = new ArrayList<>();

        for (CPU cpu : this.cpus.getCPUs()) {
            allCores.addAll(cpu.getCoreList().getCores());
        }
        return allCores;
    }

    public Core getCoreById(String coreId){
        return getAllCores().stream().filter(core -> coreId.equals(core.getId())).findAny().orElse(null);
    }

    public CPU getCpuByCoreId(String coreID){
        return cpus.getCPUs().stream().filter(cpu -> cpu.containsCore(coreID)).findAny().orElse(null);
    }

    public Task getTaskById(String taskId){
        Task temp = null;
        for (Core c: getAllCores()){
            temp = c.getTasks().stream().filter(task -> taskId.equals(task.getId())).findAny().orElse(null);
            if (temp != null){
                return temp;
            }
        }
        return temp;
    }

    public void addTaskToCore(Task task, String coreId){
        //Change CPU and Core assignment on task object in singleton and add task ID to list on core in singleton.'
        task.setCoreId(coreId);
        task.setCpuId(this.getCpuByCoreId(coreId).getId());
        getCoreById(coreId).addTask(task);
    }

    public void deleteTask(String taskId){
        getCoreById(getCoreByTaskId(taskId).getId()).deleteTask(taskId);
    }

    public void moveTask(String taskId, String destinationCoreId){
        //Move task from one core to another
        Task task = getTaskById(taskId);
        deleteTask(task.getId());
        addTaskToCore(task, destinationCoreId);
    }

    public void swapTasks(String taskId1, String taskId2){
        //Swaps core assignment of two tasks
        Task taskOne = getTaskById(taskId1);
        Task taskTwo = getTaskById(taskId2);

        String taskOneOriginalCoreId = getCoreByTaskId(taskId1).getId();
        String taskTwoOriginalCoreId = getCoreByTaskId(taskId2).getId();

        deleteTask(taskId1);
        deleteTask(taskId2);

        addTaskToCore(taskOne, taskTwoOriginalCoreId);
        addTaskToCore(taskTwo, taskOneOriginalCoreId);
    }

    public List<Task> getAllTasks(){
        //Returns a list of all tasks from all cores
        return getAllCores().stream().map(Core::getTasks).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Task> getTasksFromCore(String coreId){
        //returns all tasks from a specific core
        return getCoreById(coreId).getTasks();
    }

    public Core getCoreByTaskId (String taskId){
        return getAllCores().stream().filter(core -> core.containsTask(taskId)).findAny().orElse(null);
    }

    public void assignTasksToPlatform(List<Task> tasks){

    }

    public Core getLeastUtilizedCore(List<Core> cores) {
        return cores.stream().min(Comparator.comparing(Core::calculateUtil)).orElse(null);
    }

    public List<Core> getCoresThatTaskCanBeAssignedTo(Task t) {
        //Create multiple sets of cores that satisfy the different constraints and then do union.
        List<Core> possibleCores = new ArrayList<>();

        //respect schedule type
        Set<Core> coresWithCorrectScheduleType = new HashSet<>();
        if (t instanceof TTtask){
            coresWithCorrectScheduleType.addAll(getAllCores().stream().filter(core -> core.getScheduleType().equals(ScheduleType.TTET)).toList());
            coresWithCorrectScheduleType.addAll(getAllCores().stream().filter(core -> core.getScheduleType().equals(ScheduleType.TT)).toList());
        } else if (t instanceof ETtask) {
            coresWithCorrectScheduleType.addAll(getAllCores().stream().filter(core -> core.getScheduleType().equals(ScheduleType.TTET)).toList());
            coresWithCorrectScheduleType.addAll(getAllCores().stream().filter(core -> core.getScheduleType().equals(ScheduleType.ET)).toList());
        }


        Set<Core> coresWithCorrectAffinity = new HashSet<>();

        if (t.getCoreAffinity().length == 0) {
            coresWithCorrectAffinity.addAll(getAllCores());
        } else {
            for (String s : t.getCoreAffinity()) {
                coresWithCorrectAffinity.add(getCoreById(s));
            }
        }

        //Intersection of sets
        coresWithCorrectScheduleType.retainAll(coresWithCorrectAffinity);

        return coresWithCorrectScheduleType.stream().toList();

    }

    @Override
    public String toString() {
        return "PlatformModel{" + cpus +
                '}';
    }
}
