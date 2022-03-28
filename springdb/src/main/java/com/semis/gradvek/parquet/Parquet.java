package com.semis.gradvek.parquet;

import org.apache.parquet.example.data.Group;
import org.apache.parquet.schema.Type;

import java.util.List;

/**
 * An object combining the data and the schema for a single record from a Parquet file
 * @author ymachkasov
 *
 */
public class Parquet {
    private List<Group> data;
    private List<Type> schema;

    public Parquet(List<Group> data, List<Type> schema) {
        this.data = data;
        this.schema = schema;
    }

    public List<Group> getData() {
        return data;
    }

    public List<Type> getSchema() {
        return schema;
    }
}