package com.semis.gradvek.entity;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.Group;

import com.semis.gradvek.parquet.ParquetUtils;
import com.semis.gradvek.springdb.Importer;
public class MousePhenotype extends NamedEntity {

    private final String mMousePhenotypeId;

    public MousePhenotype (String name, String code) {
        super (name);
        mMousePhenotypeId = code;
    }

    public MousePhenotype(Importer importer, Group data) {
        super(data.getString ("label", 0));
        mMousePhenotypeId = data.getString ("id", 0);
        setDataset ("$" + DB_VERSION_PARAM);
    }

    @Override
    public final List<String> addCommands () {
        return Collections.singletonList("CREATE (:MousePhenotype" + " {" + "mousePhenotypeLabel:\'" + StringEscapeUtils.escapeEcmaScript (getName ()) + "\', "
                + getDatasetCommandString () + ", "
                + MOUSE_PHENOTYPE_ID + ":\'" + StringEscapeUtils.escapeEcmaScript (mMousePhenotypeId) + "\'})");
    }

    @Override
    public int hashCode () {
        return (mMousePhenotypeId.hashCode ());
    }

    @Override
    public String getId() {
        return mMousePhenotypeId;
    }


    @Override
    public EntityType getType() {
        return EntityType.MousePhenotype;
    }

    @Override
    public boolean equals (Object otherObj) {
        if (otherObj instanceof MousePhenotype) {
            return ((MousePhenotype) otherObj).mMousePhenotypeId.equals (mMousePhenotypeId);
        } else {
            return (false);
        }
    }


}
