package com.semis.gradvek.cytoscape;

import java.util.ArrayList;
import java.util.Map;

public class Node extends CytoscapeEntity {

    String group = "nodes";
//    ArrayList<String> classes = new ArrayList<>();

    public Node(Long id, String classes, Map<String, String> data) {
        super(id, data);
        super.getClasses().add(classes);
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
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
        return super.getClasses();
    }
//
//    public void setClasses(ArrayList<String> classes) {
//        this.classes = classes;
//    }

    public Map<String, String> getData() {
        return super.getData();
    }
}
