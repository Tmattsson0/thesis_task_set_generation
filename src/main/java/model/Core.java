package model;

import javax.xml.bind.annotation.XmlElement;

public class Core {
    private String name;
    private String id;
    private int microTick;
    private ScheduleType scheduleType;

    public Core(String name, String id, int microTick, ScheduleType scheduleType) {
        this.name = name;
        this.id = id;
        this.microTick = microTick;
        this.scheduleType = scheduleType;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }

    @XmlElement(name = "Id")
    public String getId() {
        return id;
    }

    @XmlElement(name = "MicroTick")
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

    @Override
    public String toString() {
        return "Core{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", microTick=" + microTick +
                ", scheduleType=" + scheduleType +
                '}';
    }
}
