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
 * new CommandBuilder().getWeights("JAK3").forActionTypes(List.of("OPENER", "INHIBITOR")).toCypher();
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
        StringBuilder pathway = new StringBuilder();    // build this in parallel to include after UNION if necessary

        // Find active datasets
        String enableDatasets = "MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets";
        command.append(enableDatasets);
        pathway.append(enableDatasets);

        // Don't include pathways here, otherwise there may be multiple matching paths for each ASSOCIATED_WITH
        command.append(" MATCH path=(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[rt:TARGETS]-(nt:Target)");
        // Get pathways here
        pathway.append(" MATCH path=(nt:Target)-[rpi:PARTICIPATES_IN]-(np:Pathway)");

        // Limit to active datasets
        command.append(" WHERE nae.dataset IN enabledSets")
                .append(" AND raw.dataset IN enabledSets")
                .append(" AND nd.dataset IN enabledSets")
                .append(" AND rt.dataset IN enabledSets")
                .append(" AND nt.dataset IN enabledSets");
        pathway.append(" WHERE nt.dataset IN enabledSets")
                .append(" AND rpi.dataset IN enabledSets")
                .append(" AND np.dataset IN enabledSets");

        // Limit to target
        String targetLimit = " AND toUpper(nt.symbol) = '" + target.toUpperCase(Locale.ROOT) + "'";
        command.append(targetLimit);
        pathway.append(targetLimit);

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

        // Summarize the total weights along relevant paths
        if (goal == Goal.WEIGHTS) {

            // If we already know the adverse event, then summarize by drug instead of adverse event.
            if (adverseEvent != null) {
                command.append(" RETURN nd, sum(toFloat(raw.llr))");
            } else {
                command.append(" RETURN nae, sum(toFloat(raw.llr))");
            }

            command.append(" ORDER BY sum(toFloat(raw.llr)) desc");
        }

        // List all paths
        if (goal == Goal.PATHS) {
            String returnPath = " RETURN path";
            command.append(returnPath);
            pathway.append(returnPath);
        }

        if (count > 0) {
            String limitCount = " LIMIT " + count;
            command.append(limitCount);
            pathway.append(limitCount);
        }

        // There may or may not be a pathway linked to the target, if you need the full path, segment it into
        // full path = path from the adverse event to the target + path from target to pathway
        if (goal == Goal.PATHS) {
            command.append(" UNION ").append(pathway);
        }

        return command.toString();
    }

    public static void main(String[] args) {
        System.out.println(new CommandBuilder().getWeights("JAK3").toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getWeights("JAK3").forAdverseEvent("10042868").toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getWeights("JAK3").forActionTypes(List.of("OPENER", "INHIBITOR")).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getWeights("JAK3").forAdverseEvent("10042868").forActionTypes(List.of("OPENER", "INHIBITOR")).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getWeights("JAK3").limit(3).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getWeights("JAK3").forAdverseEvent("10042868").limit(3).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getWeights("JAK3").forActionTypes(List.of("OPENER", "INHIBITOR")).limit(3).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getWeights("JAK3").forAdverseEvent("10042868").forActionTypes(List.of("OPENER", "INHIBITOR")).limit(3).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").forAdverseEvent("10042868").toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").forActionTypes(List.of("OPENER", "INHIBITOR")).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").forAdverseEvent("10042868").forActionTypes(List.of("OPENER", "INHIBITOR")).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").limit(3).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").forAdverseEvent("10042868").limit(3).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").forActionTypes(List.of("OPENER", "INHIBITOR")).limit(3).toCypher());
        System.out.println();
        System.out.println(new CommandBuilder().getPaths("JAK3").forAdverseEvent("10042868").forActionTypes(List.of("OPENER", "INHIBITOR")).limit(3).toCypher());
    }
}