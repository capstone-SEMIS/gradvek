package com.semis.gradvek.cytoscape;

import java.util.ArrayList;
import java.util.Map;

public class Relationship extends CytoscapeEntity {

    private String id;
    String group = "edges";
    ArrayList<String> classes = new ArrayList<>();

    public Relationship(String id, Map<String, String> data) {
        super(id, data);
    }

    public Relationship(String id, String classes, Map<String, String> data) {
        super(id, data);
        this.classes.add(classes);
    }

    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public void setId(String id) {
        this.id = id;
        super.setId(id);
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }
}
