package com.semis.gradvek.graphdb;

import com.semis.gradvek.entity.Constants;

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
public class CommandBuilder implements Constants {

    // private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private String target = null;
    private String adverseEvent = null;
    private List<String> actionTypes = null;
    private Goal goal = null;
    private int count = 0;
    private String drug = null;

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

    public CommandBuilder forDrug(String drug) {
        this.drug = drug;
        return this;
    }

    public CommandBuilder limit(int count) {
        this.count = count;
        return this;
    }

    public String toCypher() {
        if (goal == Goal.WEIGHTS) {
            return toCypherWeights();
        }
        return toCypherPaths();
    }

    private void appendEnabledDatasets(StringBuilder command) {
        command.append("MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets");
    }

    private void appendTargetLimit(StringBuilder command) {
        command.append(" AND toUpper(nt.symbol) = '").append(target.toUpperCase(Locale.ROOT)).append("'");
        // command.append(" AND nt.symbol = '").append(target.toUpperCase(Locale.ROOT)).append("'");
    }

    private void appendAdverseEventLimit(StringBuilder command) {
        if (adverseEvent != null) {
            command.append(" AND nae.").append(ADVERSE_EVENT_ID_STRING).append(" = '").append(adverseEvent).append("'");
        }
    }

    private void appendDrugLimit(StringBuilder command) {
        if (drug != null) {
            command.append(" AND nd.").append(DRUG_ID_STRING).append(" = '").append(drug).append("'");
        }
    }

    private void appendActionLimit(StringBuilder command) {
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
    }

    private void appendCountLimit(StringBuilder command) {
        if (count > 0) {
            command.append(" LIMIT ").append(count);
        }
    }

    private String toCypherWeights() {
        StringBuilder command = new StringBuilder();

        // Find active datasets
        appendEnabledDatasets(command);

        // First find the TARGETS segment
        command.append(" MATCH (nd:Drug)-[rt:TARGETS]-(nt:Target)"
                + " WHERE nd.dataset IN enabledSets"
                + " AND rt.dataset IN enabledSets"
                + " AND nt.dataset IN enabledSets");
        appendTargetLimit(command);
        appendDrugLimit(command);
        appendActionLimit(command);

        // Forward enabledSets and targetingDrugs to next MATCH clause
        command.append(" WITH enabledSets, COLLECT(nd) AS targetingDrugs");

        // Now find the ASSOCIATED_WITH segment
        command.append(" MATCH (nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)"
                + " WHERE nae.dataset IN enabledSets"
                + " AND raw.dataset IN enabledSets"
                + " AND nd.dataset IN enabledSets"
                + " AND nd in targetingDrugs");
        appendAdverseEventLimit(command);
        appendDrugLimit(command);

        // Summarize the total weights along relevant paths
        if (adverseEvent != null) {
            // If we know the adverse event, then summarize by drug, otherwise summarize by adverse event.
            command.append(" RETURN nd, sum(toFloat(raw.llr))");
        } else {
            command.append(" RETURN nae, sum(toFloat(raw.llr))");
        }

        command.append(" ORDER BY sum(toFloat(raw.llr)) desc");

        appendCountLimit(command);

        return command.toString();
    }

    private String toCypherPaths() {
        StringBuilder command = new StringBuilder();

        // build these in parallel to include after UNION
        StringBuilder pathway = new StringBuilder();    // just the path from target to pathway
        StringBuilder drug = new StringBuilder();       // just the path from target to drug
        StringBuilder target = new StringBuilder();     // just the target

        // Find active datasets
        appendEnabledDatasets(command);
        appendEnabledDatasets(pathway);
        appendEnabledDatasets(drug);
        appendEnabledDatasets(target);

        // Don't include pathways here, otherwise there may be multiple matching paths for each ASSOCIATED_WITH
        command.append(" MATCH path=(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[rt:TARGETS]-(nt:Target)");

        // Get pathways here
        pathway.append(" MATCH path=(nt:Target)-[rpi:PARTICIPATES_IN]-(np:Pathway)");

        // Include drugs even if there are no adverse events
        drug.append(" MATCH path=(nd:Drug)-[rt:TARGETS]-(nt:Target)");

        // Include the target even if nothing else matches
        target.append(" MATCH path=(nt:Target)");

        // Limit to active datasets
        command.append(" WHERE nae.dataset IN enabledSets")
                .append(" AND raw.dataset IN enabledSets")
                .append(" AND nd.dataset IN enabledSets")
                .append(" AND rt.dataset IN enabledSets")
                .append(" AND nt.dataset IN enabledSets");
        pathway.append(" WHERE nt.dataset IN enabledSets")
                .append(" AND rpi.dataset IN enabledSets")
                .append(" AND np.dataset IN enabledSets");
        drug.append(" WHERE nd.dataset IN enabledSets")
                .append(" AND rt.dataset IN enabledSets")
                .append(" AND nt.dataset IN enabledSets");
        target.append(" WHERE nt.dataset IN enabledSets");

        // Limit to target
        appendTargetLimit(command);
        appendTargetLimit(pathway);
        appendTargetLimit(drug);
        appendTargetLimit(target);

        // Limit to adverse event
        appendAdverseEventLimit(command);

        // Limit to drug
        appendDrugLimit(command);
        appendDrugLimit(drug);

        // Limit to action types
        appendActionLimit(command);
        appendActionLimit(drug);

        // List all paths
        String returnPath = " RETURN path";
        command.append(returnPath);
        pathway.append(returnPath);
        drug.append(returnPath);
        target.append(returnPath);

        appendCountLimit(command);
        appendCountLimit(pathway);
        appendCountLimit(drug);
        appendCountLimit(target);

        // There may or may not be a pathway linked to the target, if you need the full path, segment it into
        // full path = path from the adverse event to the target + path from target to pathway
        command.append(" UNION ").append(pathway);

        // If no adverse event is specified, also include paths that terminate in drugs
        if (adverseEvent == null) {
            command.append(" UNION ").append(drug);
        }

        // Finally, always include at least the target
        command.append(" UNION ").append(target);

        // logger.info(command.toString());
        return command.toString();
    }
}