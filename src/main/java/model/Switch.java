package model;

import java.util.List;

public class Switch {
    String id;
    List<String> connectedSystemIds;
    List<String> connections;

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
