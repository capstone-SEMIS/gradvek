package com.semis.gradvek.cytoscape;

import java.util.Map;

public abstract class CytoscapeEntity {

    private String id;
    private String group;
    private Map<String, String> data;

    protected CytoscapeEntity(String id, Map<String, String> data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
