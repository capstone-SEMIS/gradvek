export class EntityProperty {
    static get AdverseEventId() {
        return "meddraId";
    }

    static get DrugId() {
        return "chemblId";
    }

    static get TargetId() {
        return "ensembleId";
    }

    static get PathwayId() {
        return "pathwayId";
    }
}

export class Id {
    static ofAe(ae) {
        return ae[EntityProperty.AdverseEventId];
    }

    static ofDrug(drug) {
        return drug[EntityProperty.DrugId];
    }

    static ofTarget(target) {
        return target[EntityProperty.TargetId];
    }

    static ofPathway(pathway) {
        return pathway[EntityProperty.PathwayId];
    }
}
