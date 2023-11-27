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

    @Override
    public String toString() {
        return "PlatformModel{" + cpus +
                '}';
    }
}
