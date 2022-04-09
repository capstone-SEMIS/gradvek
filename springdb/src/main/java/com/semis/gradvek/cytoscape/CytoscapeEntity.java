package com.semis.gradvek.cytoscape;

import java.util.Map;

public abstract class CytoscapeEntity {

    private Long id;
    private String group;
    private Map<String, String> data;

    protected CytoscapeEntity(Long id, Map<String, String> data) {
        this.id = id;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
