package model;

import java.util.List;

public class Topology {
    List<Switch> switches;

    public Topology(){};

    public Topology(Topology topology) {
        this.switches = Switch.deepCopyUsingCopyConstructor(topology.getSwitches());
    }

    public List<Switch> getSwitches() {
        return switches;
    }

    public void setSwitches(List<Switch> switches) {
        this.switches = switches;
    }
}
