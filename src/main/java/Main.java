import controller.TaskToCoreAssignmentController;
import data.Singleton;
import model.*;
import taskEngine.TaskGenerator;
import util.ConfigInitializer;


public class Main {
    public static void main(String[] args) {

        ConfigInitializer.initialize();

        Singleton s = Singleton.getInstance();

        TaskGenerator t = new TaskGenerator(s.NUM_OF_TT_TASKS, s.TT_UTILIZATION, s.PERIODS, s.coreAffinityDist, s.PLATFORMMODEL);

        TTtask tTtask = new TTtask("1", 1000, new DeadlineType());
        TTtask tTtask1 = new TTtask("2", 1000, new DeadlineType());

        TaskToCoreAssignmentController taskController = new TaskToCoreAssignmentController();

        String id1 = s.PLATFORMMODEL.getAllCores().get(0).getId();
        String id2 = s.PLATFORMMODEL.getAllCores().get(1).getId();

        taskController.addTaskToCore(tTtask, id1);
        taskController.addTaskToCore(tTtask1, id2);

        System.out.println(taskController.getTaskById(tTtask.getId()));
        System.out.println(taskController.getCoreByTaskId(tTtask.getId()));

        taskController.swapTasks(tTtask.getId(), tTtask1.getId());

        System.out.println(s.PLATFORMMODEL.getCoreById(id1));
        System.out.println(s.PLATFORMMODEL.getCoreById(id2));

        System.out.println(taskController.getAllTasks());




//        s.TTtasks = t.initializeTTtasks();
//        s.TTtasks.forEach(System.out::println);

    }
}