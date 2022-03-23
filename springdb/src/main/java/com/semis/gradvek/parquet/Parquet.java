package com.semis.gradvek.parquet;

import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.schema.Type;

import java.util.List;

/**
 * An object combining the data and the schema for a single record from a Parquet file
 * @author ymachkasov
 *
 */
public class Parquet {
    private List<SimpleGroup> data;
    private List<Type> schema;

    public Parquet(List<SimpleGroup> data, List<Type> schema) {
        this.data = data;
        this.schema = schema;
    }

    public List<SimpleGroup> getData() {
        return data;
    }

    public List<Type> getSchema() {
        return schema;
    }
}