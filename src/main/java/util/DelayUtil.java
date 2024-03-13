package util;

import model.Chain;
import model.PlatformModel;
import model.Task;

public class DelayUtil {

    //Simple: if next ES != current ES then n++
    public static int calculateDelayInMilliseconds(Chain c, PlatformModel platformmodel) {
        int n = 0;
        Task currentTask;
        Task nextTask;

        for (int i = 0; i < c.getTasks().size() - 2; i++) {
            currentTask = c.getTasks().get(i);
            nextTask = c.getTasks().get(i + 1);

            if (!platformmodel.getEndSystemByCPUId(currentTask.getCpuId()).getId()
                    .equals(platformmodel.getEndSystemByCPUId(nextTask.getCpuId()).getId())){
                n++;
            }
        }
        return Math.max((12 * (n + 1) + n + 10)/1000, 1);
    }
}
