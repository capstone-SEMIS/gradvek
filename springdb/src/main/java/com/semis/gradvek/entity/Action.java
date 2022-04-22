package com.semis.gradvek.entity;

import com.semis.gradvek.parquet.ParquetUtils;

import java.util.List;
import java.util.Map;

public class Action extends Edge {

    public Action(String from, String to, Map<String, String> params) {
        super(from, to, params);
        setDataset("MechanismOfAction");
    }

    @Override
    public List<String> addCommands() {
        String jsonMap = ParquetUtils.paramsAsJSON(getParams());
        String cmd = "MATCH (from:Drug), (to:Target) "
                + "WHERE from." + DRUG_ID_STRING + "='" + getFrom() + "' "
                + "AND to." + TARGET_ID_STRING + "='" + getTo() + "' "
                + "CREATE (from)-[:TARGETS "
                + "{ dataset: '" + getDataset() + "' "
                + (jsonMap != null ? (", " + jsonMap) : "")
                + "} "
                + "]->(to)";
        return (List.of(cmd));
    }

    @Override
    public EntityType getType() {
        return EntityType.Action;
    }

    @Override
    public String getId() {
        return getFrom() + String.join("", getParams().values()) + getTo();
    }
}
