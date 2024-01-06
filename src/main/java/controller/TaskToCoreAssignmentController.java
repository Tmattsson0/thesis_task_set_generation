package controller;

import data.Singleton;
import model.Core;
import model.Task;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TaskToCoreAssignmentController {
    //To contain helper methods to move tasks around the different cores. No checks are done with regard to if the core can run the task or not. These must happen elsewhere.
    Singleton s = Singleton.getInstance();

    public void addTaskToCore(Task task, String coreId){
        //Change CPU and Core assignment on task object in singleton and add task ID to list on core in singleton.'
        task.setCoreId(coreId);
        task.setCpuId(s.PLATFORMMODEL.getCpuByCoreId(coreId).getId());
        s.PLATFORMMODEL.getCoreById(coreId).addTask(task);
    }

    public void deleteTask(String taskId){
        s.PLATFORMMODEL.getCoreById(getCoreByTaskId(taskId).getId()).deleteTask(taskId);
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
        return s.PLATFORMMODEL.getAllCores().stream().map(Core::getTasks).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Task> getTasksFromCore(String coreId){
        //returns all tasks from a specific core
        return s.PLATFORMMODEL.getCoreById(coreId).getTasks();
    }

    public Task getTaskById(String taskId){
        //gets task by looking up the id
        return s.PLATFORMMODEL.getTaskById(taskId);
    }

    public Core getCoreByTaskId (String taskId){
        return s.PLATFORMMODEL.getAllCores().stream().filter(core -> core.containsTask(taskId)).findAny().orElse(null);
    }

    public void assignTasksToPlatform(List<Task> tasks){
        
    }
}
