package model;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class CPU {

    private String name;
    private String id;
    private List<Core> coreList;

    public CPU(String name, String id, List<Core> coreList) {
        this.name = name;
        this.id = id;
        this.coreList = coreList;
    }

    public CPU(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Core> getCoreList() {
        return coreList;
    }

    public void setCoreList(List<Core> coreList) {
        this.coreList = coreList;
    }

    public boolean containsCore(String id){
        return getCoreList().stream().anyMatch(core -> core.getId().equals(id));
    }

    @Override
    public String toString() {
        return "CPU{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", coreList=" + coreList +
                '}';
    }
}
