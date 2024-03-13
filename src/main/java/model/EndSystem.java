package model;

import java.util.List;
import java.util.stream.Collectors;

public class EndSystem {
    private String id;
    private List<CPU> cpuList;

    public EndSystem(){}

    public EndSystem(EndSystem es){
        this.id = es.getId();
        this.cpuList = CPU.deepCopyUsingCopyConstructor(es.getCpus());
    }

    public static List<EndSystem> deepCopyUsingCopyConstructor(List<EndSystem> endSystems) {
        return endSystems.stream().map(EndSystem::new).collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CPU> getCpus(){
        return cpuList;
    }

    public void setCpuList(List<CPU> cpuList) {
        this.cpuList = cpuList;
    }

    public boolean containsCpu(String cpuId) {
        return cpuList.stream().anyMatch(cpu -> cpu.getId().equals(cpuId));
    }
}
