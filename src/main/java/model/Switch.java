package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Switch {
    String id;
    List<String> connectedSystemIds;
    List<String> connections;

    public Switch(){};

    public Switch(Switch aSwitch) {
        this.id = aSwitch.getId();
        this.connectedSystemIds = new ArrayList<>(aSwitch.getConnectedSystemIds());
        this.connections = new ArrayList<>(aSwitch.getConnections());
    }

    public static List<Switch> deepCopyUsingCopyConstructor(List<Switch> switches) {
        return switches.stream().map(Switch::new).collect(Collectors.toList());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getConnectedSystemIds() {
        return connectedSystemIds;
    }

    public void setConnectedSystemIds(List<String> connectedSystemIds) {
        this.connectedSystemIds = connectedSystemIds;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }
}
