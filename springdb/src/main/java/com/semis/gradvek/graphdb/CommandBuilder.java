package com.semis.gradvek.graphdb;

import java.util.List;
import java.util.Locale;

/**
 * Find the total weights for each adverse event linked to a particular target.
 * new CommandBuilder().getWeights("JAK3").toCypher();
 * <p>
 * Find the total weights for one adverse event linked to a target by drug.
 * new CommandBuilder().getWeights("JAK3").forAdverseEvent("10042868").toCypher();
 * <p>
 * Find the total weights for each adverse event linked to a particular target through a set of action types.
 * new CommandBuilder().getWeights("JAK3").forActionTypes(["OPENER", "INHIBITOR"]).toCypher();
 * <p>
 * Find the total weights for one adverse event linked to a target through a set of action types by drug.
 * new CommandBuilder().getWeights("JAK3").forAdverseEvent("10042868").forActionTypes(["OPENER", "INHIBITOR"]).toCypher();
 * <p>
 * Substitute getPaths() for getWeights() in the examples above to find paths that include the given target.
 * They can also be limited by action type and adverse event.
 */
public class CommandBuilder {

    private String target = null;
    private String adverseEvent = null;
    private List<String> actionTypes = null;
    private Goal goal = null;
    private int count = 0;

    private enum Goal {WEIGHTS, PATHS}

    public CommandBuilder getWeights(String target) {
        this.target = target;
        this.goal = Goal.WEIGHTS;
        return this;
    }

    public CommandBuilder getPaths(String target) {
        this.target = target;
        this.goal = Goal.PATHS;
        return this;
    }

    public CommandBuilder forAdverseEvent(String adverseEvent) {
        this.adverseEvent = adverseEvent;
        return this;
    }

    public CommandBuilder forActionTypes(List<String> actionTypes) {
        this.actionTypes = actionTypes;
        return this;
    }

    public CommandBuilder limit(int count) {
        this.count = count;
        return this;
    }

    public String toCypher() {
        StringBuilder command = new StringBuilder();

        // Find active datasets
        command.append("MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets");

        // Find paths
        if (goal == Goal.WEIGHTS) {
            command.append(" MATCH path=(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[rt:TARGETS]-(nt:Target)");
        }

        if (goal == Goal.PATHS) {
            command.append(" MATCH path=(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[rt:TARGETS]-(nt:Target)-[rpi:PARTICIPATES_IN]-(np:Pathway)");
        }

        // Limit to active datasets
        command.append(" WHERE nae.dataset IN enabledSets")
                .append(" AND raw.dataset IN enabledSets")
                .append(" AND nd.dataset IN enabledSets")
                .append(" AND rt.dataset IN enabledSets")
                .append(" AND nt.dataset IN enabledSets");

        if (goal == Goal.PATHS) {
            command.append(" AND rpi.dataset IN enabledSets")
                    .append(" AND np.dataset IN enabledSets");
        }

        // Limit to target
        command.append(" AND toUpper(nt.symbol) = '").append(target.toUpperCase(Locale.ROOT)).append("'");

        // Limit to adverse event
        if (adverseEvent != null) {
            command.append(" AND nae.meddraCode = '").append(adverseEvent).append("'");
        }

        // Limit to action types
        if (actionTypes != null && actionTypes.size() > 0) {
            command.append(" AND rt.actionType IN ");
            for (int i = 0; i < actionTypes.size(); ++i) {
                if (i == 0) {
                    command.append("['").append(actionTypes.get(i)).append("'");
                } else {
                    command.append(", '").append(actionTypes.get(i)).append("'");
                }
            }
            command.append("]");
        }

        // Weights vs paths
        if (goal == Goal.WEIGHTS) {
            command.append(" RETURN nae, sum(toFloat(raw.llr)) ORDER BY sum(toFloat(raw.llr)) desc");
        }

        // May result in non-unique nodes and non-unique relations.  Can we find an easier way to parse this?
        if (goal == Goal.PATHS) {
            command.append(" RETURN path");
        }

        // With or without pathway
//        command.append("(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[nt:TARGETS]-(rt:Target)-[rpi:PARTICIPATES_IN]-(np:Pathway)");
//        command.append("(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[nt:TARGETS]-(rt:Target) WHERE NOT EXISTS {(rt)-[rpi:PARTICIPATES_IN]-(np:Pathway)}");

        if (count > 0) {
            command.append(" LIMIT ").append(count);
        }

        return command.toString();
    }

    public static void main(String[] args) {
        System.out.println(new CommandBuilder().getWeights(("JAK3")).toCypher());
    }
}