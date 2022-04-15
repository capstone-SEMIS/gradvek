package com.semis.gradvek.graphdb;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommandBuilderTest {
    final String expectedPrefix = "MATCH (nd:Dataset {enabled: true}) WITH COLLECT(nd.dataset) AS enabledSets"
            + " MATCH path=(nae:AdverseEvent)-[raw:ASSOCIATED_WITH]-(nd:Drug)-[rt:TARGETS]-(nt:Target)"
            + " WHERE nae.dataset IN enabledSets"
            + " AND raw.dataset IN enabledSets"
            + " AND nd.dataset IN enabledSets"
            + " AND rt.dataset IN enabledSets"
            + " AND nt.dataset IN enabledSets"
            + " AND toUpper(nt.symbol) = 'JAK3'";
    final String filterAe = " AND nae.meddraCode = '10042868'";
    final String filterActions = " AND rt.actionType IN ['OPENER', 'INHIBITOR']";
    final String returnByAe = " RETURN nae, sum(toFloat(raw.llr))";
    final String returnByDrug = " RETURN nd, sum(toFloat(raw.llr))";
    final String orderByWeights = " ORDER BY sum(toFloat(raw.llr)) desc";
    final String limits = " LIMIT 3";

    @Test
    void getWeightsByAe() {
        String command = new CommandBuilder()
                .getWeights("JAK3")
                .toCypher();
        String expected = expectedPrefix
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
        String expected = expectedPrefix
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
        String expected = expectedPrefix
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
        String expected = expectedPrefix
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
        String expected = expectedPrefix
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
        String expected = expectedPrefix
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
        String expected = expectedPrefix
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
        String expected = expectedPrefix
                + filterAe
                + filterActions
                + returnByDrug
                + orderByWeights
                + limits;
        assertThat(command).isEqualTo(expected);
    }
}