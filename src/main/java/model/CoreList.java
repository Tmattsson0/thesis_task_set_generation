package model;

import java.util.ArrayList;
import java.util.List;

public class CoreList {

    private final List<Core> cores;

    public CoreList(List<Core> cores) {
        this.cores = cores;
    }

    public CoreList(String cpuId, int amount) {
        this.cores = new ArrayList<>();

        for (int i = 0; i < amount; i++){
            Core tempCore = new Core("Core" + (i + 1), cpuId + (i + 1), -1, ScheduleType.TTET);
            this.cores.add(tempCore);
        }
    }

    public List<Core> getCores() {
        return cores;
    }

    @Override
    public String toString() {
        return "CoreList{" + cores +
                '}';
    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 'A' - 1)) : null;
    }

}
