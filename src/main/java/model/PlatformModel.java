package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "PlatformModel")
public class PlatformModel {
    private CPUList cpus;

    public PlatformModel(CPUList cpus) {
        this.cpus = cpus;
    }

    public PlatformModel() {
    }

    @XmlElement(name = "Cpus")
    public CPUList getCpus() {
        return cpus;
    }

    public List<Core> getAllCores(){
        List<Core> allCores = new ArrayList<>();

        for (CPU cpu : this.cpus.getCPUs()) {
            allCores.addAll(cpu.getCoreList().getCores());
        }
        return allCores;
    }

    public Core getCoreById(String coreId){
        return getAllCores().stream().filter(core -> coreId.equals(core.getId())).findAny().orElse(null);
    }

    public Task getTaskById(String taskId){
        Task temp = null;
        for (Core c: getAllCores()){
            temp = c.getTasks().stream().filter(task -> taskId.equals(task.getId())).findAny().orElse(null);
            if (temp != null){
                return temp;
            }
        }
        return temp;
    }

    @Override
    public String toString() {
        return "PlatformModel{" + cpus +
                '}';
    }
}
