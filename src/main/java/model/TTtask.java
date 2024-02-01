package model;

import util.RandomUtil;

import java.util.Arrays;

public class TTtask extends Task {
    private int offset;
    private final TaskType taskType;
    private final int priority;

    public TTtask(String id, int period, DeadlineType deadlineType) {
        super(id, period, DeadlineType.getConstrainedDeadline(deadlineType));
        this.taskType = TaskType.TT;
        this.priority = 1;
        this.offset = -1;
    }

    public TTtask(TTtask t) {

        super(t.getId(), t.getPeriod(), new DeadlineType(t.getDeadlineType()));

        super.setName(t.getName());
        super.setDeadline(t.getDeadline());
        super.setWcet(t.getWcet());
        super.setCpuId(t.getCpuId());
        super.setCoreId(t.getCoreId());
        super.setMaxJitter(t.getMaxJitter());
        super.setCoreAffinity(t.getCoreAffinity().clone());

        this.taskType = t.getTaskType();
        this.priority = t.getPriority();
        this.offset = t.getOffset();
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

    public void calculateAndSetOffset(double[] releaseTimeDist) {
        double diceRoll = RandomUtil.getRandom().doubles(1, 0, 1).sum();
        if (diceRoll <= releaseTimeDist[0]){
            //0 releaseTimeDist[0]
            setOffset(0);
        } else {
            //up to 10% of period releaseTimeDist[1]
            setOffset((int) (Math.round((RandomUtil.getRandom().doubles(1, 0, 1).sum() * 0.1) * super.getPeriod())));
        }
    }
}
