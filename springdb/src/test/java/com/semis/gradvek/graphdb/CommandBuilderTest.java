package com.semis.gradvek.graphdb;

import com.semis.gradvek.entity.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandBuilderTest implements Constants {
    final String filterTarget = " AND toUpper(nt.symbol) = 'JAK3'";
    final String weightsPrefix = "MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets"
            + " MATCH (nd:Drug)-[rt:TARGETS]-(nt:Target)"
            + " WHERE nd.dataset IN enabledSets"
            + " AND rt.dataset IN enabledSets"
            + " AND nt.dataset IN enabledSets"
            + filterTarget;
    final String weightsInfix = " WITH enabledSets, COLLECT(nd) AS targetingDrugs"
            + " MATCH (nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)"
            + " WHERE nae.dataset IN enabledSets"
            + " AND raw.dataset IN enabledSets"
            + " AND nd.dataset IN enabledSets"
            + " AND nd in targetingDrugs";
    final String aePrefix = "MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets"
            + " MATCH path=(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[rt:TARGETS]-(nt:Target)"
            + " WHERE nae.dataset IN enabledSets"
            + " AND raw.dataset IN enabledSets"
            + " AND nd.dataset IN enabledSets"
            + " AND rt.dataset IN enabledSets"
            + " AND nt.dataset IN enabledSets"
            + filterTarget;
    final String pathwayPrefix = " MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets"
            + " MATCH path=(nt:Target)-[rpi:PARTICIPATES_IN]-(np:Pathway)"
            + " WHERE nt.dataset IN enabledSets"
            + " AND rpi.dataset IN enabledSets"
            + " AND np.dataset IN enabledSets"
            + filterTarget;
    final String filterAe = " AND nae." + ADVERSE_EVENT_ID_STRING + " = '10042868'";
    final String filterActions = " AND rt.actionType IN ['OPENER', 'INHIBITOR']";
    final String filterDrug = " AND nd." + DRUG_ID_STRING + " = 'CHEMBL221959'";
    final String returnByAe = " RETURN nae, sum(toFloat(raw.llr))";
    final String returnByDrug = " RETURN nd, sum(toFloat(raw.llr))";
    final String returnPath = " RETURN path";
    final String union = " UNION";
    final String orderByWeights = " ORDER BY sum(toFloat(raw.llr)) desc";
    final String limits = " LIMIT 3";
    final String includeSoloTarget = " UNION MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets"
            + " MATCH path=(nt:Target) WHERE nt.dataset IN enabledSets AND toUpper(nt.symbol) = 'JAK3' RETURN path";
    final String drugPrefix = " UNION MATCH (nd:Dataset {enabled: true})"
            + " WITH COLLECT(nd.dataset) AS enabledSets"
            + " MATCH path=(nd:Drug)-[rt:TARGETS]-(nt:Target)"
            + " WHERE nd.dataset IN enabledSets AND rt.dataset IN enabledSets AND nt.dataset IN enabledSets"
            + filterTarget;

    @Test
    void getWeightsByAe() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .toCypher();
        String expected = weightsPrefix
                + weightsInfix
                + returnByAe
                + orderByWeights;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsByDrug() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forAdverseEvent("10042868")
                .toCypher();
        String expected = weightsPrefix
                + weightsInfix
                + filterAe
                + returnByDrug
                + orderByWeights;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsByAeFilterActions() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .toCypher();
        String expected = weightsPrefix
                + filterActions
                + weightsInfix
                + returnByAe
                + orderByWeights;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsByDrugFilterActions() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forAdverseEvent("10042868")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .toCypher();
        String expected = weightsPrefix
                + filterActions
                + weightsInfix
                + filterAe
                + returnByDrug
                + orderByWeights;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsByAeLimit() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .limit(3)
                .toCypher();
        String expected = weightsPrefix
                + weightsInfix
                + returnByAe
                + orderByWeights
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsByDrugLimit() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forAdverseEvent("10042868")
                .limit(3)
                .toCypher();
        String expected = weightsPrefix
                + weightsInfix
                + filterAe
                + returnByDrug
                + orderByWeights
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsByAeFilterActionsLimit() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .limit(3)
                .toCypher();
        String expected = weightsPrefix
                + filterActions
                + weightsInfix
                + returnByAe
                + orderByWeights
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsByDrugFilterActionsLimit() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forAdverseEvent("10042868")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .limit(3)
                .toCypher();
        String expected = weightsPrefix
                + filterActions
                + weightsInfix
                + filterAe
                + returnByDrug
                + orderByWeights
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsFilterDrug() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forDrug("CHEMBL221959")
                .toCypher();
        String expected = weightsPrefix
                + filterDrug
                + weightsInfix
                + filterDrug
                + returnByAe
                + orderByWeights;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getWeightsFilterAeActionDrug() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .forAdverseEvent("10042868")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .forDrug("CHEMBL221959")
                .toCypher();
        String expected = weightsPrefix
                + filterDrug
                + filterActions
                + weightsInfix
                + filterAe
                + filterDrug
                + returnByDrug
                + orderByWeights;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPaths() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .toCypher();
        String expected = aePrefix
                + returnPath
                + union
                + pathwayPrefix
                + returnPath
                + drugPrefix
                + returnPath
                + includeSoloTarget;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsAe() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forAdverseEvent("10042868")
                .toCypher();
        String expected = aePrefix
                + filterAe
                + returnPath
                + union
                + pathwayPrefix
                + returnPath
                + includeSoloTarget;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsFilterActions() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .toCypher();
        String expected = aePrefix
                + filterActions
                + returnPath
                + union
                + pathwayPrefix
                + returnPath
                + drugPrefix
                + filterActions
                + returnPath
                + includeSoloTarget;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsFilterAeActions() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forAdverseEvent("10042868")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .toCypher();
        String expected = aePrefix
                + filterAe
                + filterActions
                + returnPath
                + union
                + pathwayPrefix
                + returnPath
                + includeSoloTarget;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsLimit() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .limit(3)
                .toCypher();
        String expected = aePrefix
                + returnPath
                + limits
                + union
                + pathwayPrefix
                + returnPath
                + limits
                + drugPrefix
                + returnPath
                + limits
                + includeSoloTarget
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsAeLimit() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forAdverseEvent("10042868")
                .limit(3)
                .toCypher();
        String expected = aePrefix
                + filterAe
                + returnPath
                + limits
                + union
                + pathwayPrefix
                + returnPath
                + limits
                + includeSoloTarget
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsFilterActionsLimit() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .limit(3)
                .toCypher();
        String expected = aePrefix
                + filterActions
                + returnPath
                + limits
                + union
                + pathwayPrefix
                + returnPath
                + limits
                + drugPrefix
                + filterActions
                + returnPath
                + limits
                + includeSoloTarget
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsFilterAeActionsLimit() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forAdverseEvent("10042868")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .limit(3)
                .toCypher();
        String expected = aePrefix
                + filterAe
                + filterActions
                + returnPath
                + limits
                + union
                + pathwayPrefix
                + returnPath
                + limits
                + includeSoloTarget
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsFilterAeActionDrugLimit() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forAdverseEvent("10042868")
                .forActionTypes(List.of("OPENER", "INHIBITOR"))
                .forDrug("CHEMBL221959")
                .limit(3)
                .toCypher();
        String expected = aePrefix
                + filterAe
                + filterDrug
                + filterActions
                + returnPath
                + limits
                + union
                + pathwayPrefix
                + returnPath
                + limits
                + includeSoloTarget
                + limits;
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void getPathsFilterAeDrug() {
        String command = new CommandBuilder()
                .getPaths("JAK3")
                .forAdverseEvent("10042868")
                .forDrug("CHEMBL221959")
                .toCypher();
        String expected = aePrefix
                + filterAe
                + filterDrug
                + returnPath
                + union
                + pathwayPrefix
                + returnPath
                + includeSoloTarget;
        assertThat(command).isEqualTo(expected);
    }
}
