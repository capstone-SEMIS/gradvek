package com.semis.gradvek.graphdb;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandBuilderTest {
    final String filterTarget = " AND toUpper(nt.symbol) = 'JAK3'";
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
    final String filterAe = " AND nae.meddraCode = '10042868'";
    final String filterActions = " AND rt.actionType IN ['OPENER', 'INHIBITOR']";
    final String returnByAe = " RETURN nae, sum(toFloat(raw.llr))";
    final String returnByDrug = " RETURN nd, sum(toFloat(raw.llr))";
    final String returnPath = " RETURN path";
    final String union = " UNION";
    final String orderByWeights = " ORDER BY sum(toFloat(raw.llr)) desc";
    final String limits = " LIMIT 3";

    @Test
    void getWeightsByAe() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .toCypher();
        String expected = aePrefix
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
        String expected = aePrefix
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
        String expected = aePrefix
                + filterActions
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
        String expected = aePrefix
                + filterAe
                + filterActions
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
        String expected = aePrefix
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
        String expected = aePrefix
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
        String expected = aePrefix
                + filterActions
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
        String expected = aePrefix
                + filterAe
                + filterActions
                + returnByDrug
                + orderByWeights
                + limits;
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
                + returnPath;
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
                + returnPath;
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
                + returnPath;
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
                + returnPath;
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
                + limits;
        assertThat(command).isEqualTo(expected);
    }
}
