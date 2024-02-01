package model;

import util.RandomUtil;

public class DeadlineType {
    private final int x;
    private final int y;
    private final boolean isArbitrary;
    private final boolean isImplicit;

    public DeadlineType(int x, int y) {
        this.x = x;
        this.y = y;
        this.isArbitrary = true;
        this.isImplicit = false;
    }

    public DeadlineType(){
        this.x = 0;
        this.y = 0;
        this.isImplicit = true;
        this.isArbitrary = false;
    }

    public DeadlineType(DeadlineType deadlineType) {
        this.x = deadlineType.getX();
        this.y = deadlineType.getY();
        this.isArbitrary = deadlineType.isArbitrary();
        this.isImplicit = deadlineType.isImplicit();
    }

    public static DeadlineType getConstrainedDeadline(DeadlineType deadlineType) {
        if (deadlineType.y > 100) {
            return new DeadlineType(deadlineType.getX(), 100);
        }
        return deadlineType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isArbitrary() {
        return isArbitrary;
    }

    public boolean isImplicit() {
        return isImplicit;
    }

    public int calculateDeadline(int period) {
        return RandomUtil.getRandom().ints(1, (int)(period * ((double) this.x/100)), (int)(period * ((double) this.y/100))).sum();
    }

    @Override
    public String toString() {
        if(isImplicit) {
            return "DeadlineType{Implicit}";
        } else
            return "DeadlineType{Arbitrary, " + x + " ," + y;
    }
}



