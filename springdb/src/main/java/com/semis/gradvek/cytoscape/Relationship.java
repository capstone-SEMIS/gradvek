package com.semis.gradvek.cytoscape;

import java.util.Map;

public class Relationship extends CytoscapeEntity {

    private String group = "edges";
//    private ArrayList<String> classes = new ArrayList<>();

    public Relationship(Long id, Map<String, String> data) {
        super(id, data);
    }

    public Relationship(Long id, String classes, Map<String, String> data) {
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

//    public ArrayList<String> getClasses() {
//        return classes;
//    }
//
//    public void setClasses(ArrayList<String> classes) {
//        super.setClasses(classes);
//    }
}
