package com.semis.gradvek.cytoscape;

import java.util.ArrayList;
import java.util.Map;

public abstract class CytoscapeEntity {

    private Long id;
    private String group;
    private Map<String, String> data;
    private ArrayList<String> classes = new ArrayList<>();

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

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }
}
