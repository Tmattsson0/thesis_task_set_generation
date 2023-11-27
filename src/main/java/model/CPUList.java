package model;

import java.util.ArrayList;
import java.util.List;

public class CPUList {

    private final List<CPU> cpus;

    public CPUList(List<CPU> cpus) {
        this.cpus = cpus;
    }

    /**
     * Adds a list of type CPU with names and ids, but no cores to the CPUList object.
     * @param amount the amount of CPUList that will be generated.
     */
    public CPUList(int amount){
        this.cpus = new ArrayList<>();

        for (int i = 0; i < amount; i++){
            CPU tempCpu = new CPU("CPU" + (i+ 1), getCharForNumber(i + 1));
            this.cpus.add(tempCpu);
        }
    }

    public List<CPU> getCPUs() {
        return cpus;
    }

    public CPU get(int i){
        return cpus.get(i);
    }

    @Override
    public String toString() {
        return "CPUList{" +
                cpus +
                '}';
    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'A' - 1)) : null;
    }
}
