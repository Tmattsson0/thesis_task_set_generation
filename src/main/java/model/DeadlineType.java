package model;

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
}


