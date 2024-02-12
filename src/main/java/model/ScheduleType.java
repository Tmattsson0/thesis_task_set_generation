package model;

public enum ScheduleType {
    TT("TT"),
    ET("ET"),
    TTET("TT + ET"),
    NONE("NONE");
    private final String value;

    ScheduleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}