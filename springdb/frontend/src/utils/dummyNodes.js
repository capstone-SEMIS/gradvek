const dummyNodes = [
    {
        group: "nodes",
        classes: ["pathway"],
        data: {
            id: "pathway_xyz"
        }
    },
    {
        group: "nodes",
        classes: ["drug"],
        data: {
            id: 'Acetaminophen',
            chembl_code: 'CHEMBL112'
        }
    },
    {
        group: "nodes",
        classes: ["drug"],
        data: {
            id: 'Metformin',
            chembl_code: 'CHEMBL1431'
        }
    },
    {
        group: "nodes",
        classes: ["adverse event"],
        data: {
            id: 'Acute_hepatic_failure',
            meddraCode: '10000804'
        }
    },
    {
        group: "nodes",
        classes: ["adverse event"],
        data: {
            id: 'Toxicity_to_various_agents',
            meddraCode: '10070863'
        }
    },
    {
        group: "nodes",
        classes: ["proteinTarget"],
        data: {
            id: 'Vanilloid_receptor',
            parent: "pathway_xyz",
        }
    },
    {
        group: "nodes",
        classes: ["proteinTarget"],
        data: {
            id: 'XYZ_receptor',
            parent: "pathway_xyz",
        }
    },
    {
        group: "nodes",
        classes: ["proteinTarget"],
        data: {
            id: 'Cyclooxygenase',
        },

    },
    {
        group: "edges",
        data: {
            id: 'edge_1',
            source: 'Acetaminophen',
            target: 'Acute_hepatic_failure',
            arrow: "vee",
            critval: 123
        }
    },
    {
        group: "edges",
        data: {
            id: 'edge_2',
            source: 'Acetaminophen',
            target: 'Toxicity_to_various_agents',
            arrow: "vee",
            critval: 123
        }
    },
    {
        group: "edges",
        classes: ["drug_target"],
        data: {
            id: 'edge_3',
            source: 'Acetaminophen',
            target: 'Vanilloid_receptor',
            arrow: "vee",
            action: "inhibits",
        }
    },
    {
        group: "edges",
        classes: ["drug_target"],
        data: {
            id: 'edge_4',
            source: 'Acetaminophen',
            target: 'Cyclooxygenase',
            arrow: "vee",
            action: "opener",
        }
    },
    {
        group: "edges",
        classes: ["drug_target"],
        data: {
            id: 'edge_5',
            source: 'Metformin',
            target: 'Cyclooxygenase',
            arrow: "vee",
            action: "opener",
        }
    },
    {
        group: "edges",
        classes: ["drug_target"],
        data: {
            id: 'edge_6',
            source: 'Metformin',
            target: 'XYZ_receptor',
            arrow: "vee",
            action: "opener",
        }
    },
    {
        group: "edges",
        data: {
            id: 'edge_7',
            source: 'Metformin',
            target: 'Toxicity_to_various_agents',
            arrow: "vee",
            action: "opener",
        }
    },
];

export {dummyNodes as default};