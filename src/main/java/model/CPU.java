package model;

import javax.xml.bind.annotation.XmlElement;

public class CPU {

    private String name;
    private String id;
    private CoreList coreList;

    public CPU(String name, String id, CoreList coreList) {
        this.name = name;
        this.id = id;
        this.coreList = coreList;
    }

    public CPU(String name, String id){
        this.name = name;
        this.id = id;
    }

    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "Id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "CoreList")
    public CoreList getCoreList() {
        return coreList;
    }

    public void setCoreList(CoreList coreList) {
        this.coreList = coreList;
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
