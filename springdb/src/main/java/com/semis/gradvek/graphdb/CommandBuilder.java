package com.semis.gradvek.graphdb;

public class CommandBuilder {

    public CommandBuilder getWeights(String target) {
        return this;
    }

    public CommandBuilder forAdverseEvent(String event) {
        return this;
    }

    public CommandBuilder limit(int count) {
        return this;
    }

    public String toCypher() {
        return null;
    }
}
