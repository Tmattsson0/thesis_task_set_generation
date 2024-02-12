package model;

import java.util.List;
import java.util.stream.Collectors;

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

    public CPU(CPU cpu) {
        this.name = cpu.getName();
        this.id = cpu.getId();
        this.coreList = Core.deepCopyUsingCopyConstructor(cpu.getCoreList());
    }

    public static List<CPU> deepCopyUsingCopyConstructor(List<CPU> cpus) {
        return cpus.stream().map(CPU::new).collect(Collectors.toList());
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
