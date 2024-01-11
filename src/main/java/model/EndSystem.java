package model;

import java.util.List;

public class EndSystem {
    private String id;
    private List<CPU> cpuList;

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
}
