package model;

import util.RandomUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PlatformModel {
    private List<EndSystem> endSystems;
    private List<Chain> chains;
    private Topology topology;
    private double fitness;

    public PlatformModel() {
    }

    public PlatformModel(PlatformModel old) {
        this.endSystems = EndSystem.deepCopyUsingCopyConstructor(old.getEndSystems());
        this.topology = new Topology(old.getTopology());
        this.fitness = old.getFitness();
        this.chains = Chain.deepCopyUsingCopyConstructor(old.getChains());
    }

    public List<Chain> getChains() {
        return chains;
    }

    public void setChains(List<Chain> chains) {
        this.chains = chains;
    }

    public List<EndSystem> getEndSystems() {
        return endSystems;
    }

    public void setEndSystems(List<EndSystem> endSystems) {
        this.endSystems = endSystems;
    }

    public Topology getTopology() {
        return topology;
    }

    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    public List<Core> getAllCores(){
        List<Core> allCores = new ArrayList<>();

        for (CPU cpu : getCPUs()) {
            allCores.addAll(cpu.getCoreList());
        }
        return allCores;
    }

    public List<Core> getAllCores(ScheduleType scheduleType) {
        if (scheduleType == ScheduleType.TTET || scheduleType == ScheduleType.NONE) {
            List<Core> allCores = new ArrayList<>();

            for (CPU cpu : getCPUs()) {
                allCores.addAll(cpu.getCoreList());
            }

            return allCores;

        } else if (scheduleType == ScheduleType.TT){
            List<Core> allCores = new ArrayList<>();

            for (CPU cpu : getCPUs()) {

                List<Core> tempCoreList = cpu.getCoreList();

                for (Core core : tempCoreList){
                    if (core.getScheduleType().getValue().contains("TT")){
                        allCores.add(core);
                    }
                }
            }
            return allCores;

        } else {
            List<Core> allCores = new ArrayList<>();

            for (CPU cpu : getCPUs()) {

                List<Core> tempCoreList = cpu.getCoreList();

                for (Core core : tempCoreList){
                    if (core.getScheduleType().getValue().contains("ET")){
                        allCores.add(core);
                    }
                }
            }
            return allCores;
        }
    }

    private List<CPU> getCPUs() {
        return getEndSystems().stream().map(EndSystem::getCpus).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Core getCoreById(String coreId){
        return getAllCores().stream().filter(core -> coreId.equals(core.getId())).findAny().orElse(null);
    }

    public CPU getCpuByCoreId(String coreID){
        return getCPUs().stream().filter(cpu -> cpu.containsCore(coreID)).findAny().orElse(null);
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
        task.setCpuId(getCpuByCoreId(coreId).getId());
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

    public Core getLeastUtilizedCore(List<Core> cores, ScheduleType scheduleType) {
        if (scheduleType == ScheduleType.TTET || scheduleType == ScheduleType.NONE) {
            return cores.stream().min(Comparator.comparing(Core::calculateUtil)).orElse(null);
        } else if (scheduleType == ScheduleType.TT) {
            return cores.stream().min(Comparator.comparing(Core::calculateTTUtil)).orElse(null);
        } else if (scheduleType == ScheduleType.ET) {
            return cores.stream().min(Comparator.comparing(Core::calculateETUtil)).orElse(null);
        }
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

        return new ArrayList<>(coresWithCorrectScheduleType);
    }

    @Override
    public String toString() {
        return "PlatformModel{" + endSystems.toString() +
                '}';
    }

    public Task getRandomTask() {

        List<Task> temp = getAllCores().stream().map(Core::getTasks).flatMap(Collection::stream).toList();

        return temp.get(RandomUtil.getRandom().ints(1, 0, temp.size()).sum());
    }
}
