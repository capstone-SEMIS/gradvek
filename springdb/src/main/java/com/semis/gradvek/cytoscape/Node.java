package com.semis.gradvek.cytoscape;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.semis.gradvek.entity.Constants.*;

public class Node extends CytoscapeEntity {

    public static final Map<String, List<Pair<String, String>>> propertyMap = Map.of(
            "AdverseEvent", List.of(
                    Pair.of(ADVERSE_EVENT_ID_STRING, ADVERSE_EVENT_ID_STRING),
                    Pair.of("name", "adverseEventId"),
                    Pair.of("adverseEventId", "adverseEventId")
            ),
            "Drug", List.of(
                    Pair.of(DRUG_ID_STRING, DRUG_ID_STRING),
                    Pair.of("name", "drugId"),
                    Pair.of("drugId", "drugId")
            ),
            "Target", List.of(
                    Pair.of(TARGET_ID_STRING, TARGET_ID_STRING),
                    Pair.of("name", "symbol"),
                    Pair.of("symbol", "symbol")
            ),
            "Pathway", List.of(
                    Pair.of(PATHWAY_ID_STRING, PATHWAY_ID_STRING),
                    Pair.of("name", "pathwayCode"),
                    Pair.of("term", "topLevelTerm")
            )
    );

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
