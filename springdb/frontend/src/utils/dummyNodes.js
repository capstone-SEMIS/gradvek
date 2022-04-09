// const dummyNodes = [
//     {
//         group: "nodes",
//         classes: ["pathway"],
//         data: {
//             id: "pathway_xyz"
//         }
//     },
//     {
//         group: "nodes",
//         classes: ["drug"],
//         data: {
//             id: 'Acetaminophen',
//             chembl_code: 'CHEMBL112'
//         }
//     },
//     {
//         group: "nodes",
//         classes: ["drug"],
//         data: {
//             id: 'Metformin',
//             chembl_code: 'CHEMBL1431'
//         }
//     },
//     {
//         group: "nodes",
//         classes: ["adverse event"],
//         data: {
//             id: 'Acute_hepatic_failure',
//             meddraCode: '10000804'
//         }
//     },
//     {
//         group: "nodes",
//         classes: ["adverse event"],
//         data: {
//             id: 'Toxicity_to_various_agents',
//             meddraCode: '10070863'
//         }
//     },
//     {
//         group: "nodes",
//         classes: ["proteinTarget"],
//         data: {
//             id: 'Vanilloid_receptor',
//             parent: "pathway_xyz",
//         }
//     },
//     {
//         group: "nodes",
//         classes: ["proteinTarget"],
//         data: {
//             id: 'XYZ_receptor',
//             parent: "pathway_xyz",
//         }
//     },
//     {
//         group: "nodes",
//         classes: ["proteinTarget"],
//         data: {
//             id: 'Cyclooxygenase',
//         },
//
//     },
//     {
//         group: "edges",
//         data: {
//             id: 'edge_1',
//             source: 'Acetaminophen',
//             target: 'Acute_hepatic_failure',
//             arrow: "vee",
//             critval: 123
//         }
//     },
//     {
//         group: "edges",
//         data: {
//             id: 'edge_2',
//             source: 'Acetaminophen',
//             target: 'Toxicity_to_various_agents',
//             arrow: "vee",
//             critval: 123
//         }
//     },
//     {
//         group: "edges",
//         classes: ["drug_target"],
//         data: {
//             id: 'edge_3',
//             source: 'Acetaminophen',
//             target: 'Vanilloid_receptor',
//             arrow: "vee",
//             action: "inhibits",
//         }
//     },
//     {
//         group: "edges",
//         classes: ["drug_target"],
//         data: {
//             id: 'edge_4',
//             source: 'Acetaminophen',
//             target: 'Cyclooxygenase',
//             arrow: "vee",
//             action: "opener",
//         }
//     },
//     {
//         group: "edges",
//         classes: ["drug_target"],
//         data: {
//             id: 'edge_5',
//             source: 'Metformin',
//             target: 'Cyclooxygenase',
//             arrow: "vee",
//             action: "opener",
//         }
//     },
//     {
//         group: "edges",
//         classes: ["drug_target"],
//         data: {
//             id: 'edge_6',
//             source: 'Metformin',
//             target: 'XYZ_receptor',
//             arrow: "vee",
//             action: "opener",
//         }
//     },
//     {
//         group: "edges",
//         data: {
//             id: 'edge_7',
//             source: 'Metformin',
//             target: 'Toxicity_to_various_agents',
//             arrow: "vee",
//             action: "opener",
//         }
//     },
// ];


const dummyNodes =
    [
        {
            "id": "2112",
            "group": "edges",
            "data": {
                "llr": "1329.8594595958712",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2112",
                "source": "ACETAMINOPHEN",
                "target": "poisoning deliberate"
            },
            "classes": []
        },
        {
            "id": "liver injury",
            "group": "nodes",
            "data": {
                "adverseEventId": "liver injury",
                "id": "liver injury",
                "meddraCode": "10067125"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "acute hepatic failure",
            "group": "nodes",
            "data": {
                "adverseEventId": "acute hepatic failure",
                "id": "acute hepatic failure",
                "meddraCode": "10000804"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "somnolence",
            "group": "nodes",
            "data": {
                "adverseEventId": "somnolence",
                "id": "somnolence",
                "meddraCode": "10041349"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "2183",
            "group": "edges",
            "data": {
                "llr": "3326.1001694500737",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2183",
                "source": "ACETAMINOPHEN",
                "target": "toxicity to various agents"
            },
            "classes": []
        },
        {
            "id": "2215",
            "group": "edges",
            "data": {
                "llr": "1095.7840498979203",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2215",
                "source": "ACETAMINOPHEN",
                "target": "vomiting"
            },
            "classes": []
        },
        {
            "id": "1962",
            "group": "edges",
            "data": {
                "llr": "1116.4224342512025",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "1962",
                "source": "ACETAMINOPHEN",
                "target": "metabolic acidosis"
            },
            "classes": []
        },
        {
            "id": "2347",
            "group": "edges",
            "data": {
                "llr": "1023.48710510417",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2347",
                "source": "ACETAMINOPHEN",
                "target": "somnolence"
            },
            "classes": []
        },
        {
            "id": "toxicity to various agents",
            "group": "nodes",
            "data": {
                "adverseEventId": "toxicity to various agents",
                "id": "toxicity to various agents",
                "meddraCode": "10070863"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "ACETAMINOPHEN",
            "group": "nodes",
            "data": {
                "drugId": "ACETAMINOPHEN",
                "id": "ACETAMINOPHEN",
                "chembl_code": "CHEMBL112"
            },
            "classes": [
                "drug"
            ]
        },
        {
            "id": "drug hypersensitivity",
            "group": "nodes",
            "data": {
                "adverseEventId": "drug hypersensitivity",
                "id": "drug hypersensitivity",
                "meddraCode": "10013700"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "TRPV1",
            "group": "nodes",
            "data": {
                "symbol": "TRPV1",
                "targetId": "ENSG00000196689",
                "name": "transient receptor potential cation channel subfamily V member 1",
                "id": "TRPV1"
            },
            "classes": [
                "target"
            ]
        },
        {
            "id": "2256",
            "group": "edges",
            "data": {
                "llr": "875.4104851199081",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2256",
                "source": "ACETAMINOPHEN",
                "target": "drug hypersensitivity"
            },
            "classes": []
        },
        {
            "id": "1777",
            "group": "edges",
            "data": {
                "llr": "2427.8046000653994",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "1777",
                "source": "ACETAMINOPHEN",
                "target": "suicide attempt"
            },
            "classes": []
        },
        {
            "id": "vomiting",
            "group": "nodes",
            "data": {
                "adverseEventId": "vomiting",
                "id": "vomiting",
                "meddraCode": "10047700"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "2131",
            "group": "edges",
            "data": {
                "llr": "2625.441938379692",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2131",
                "source": "ACETAMINOPHEN",
                "target": "acute hepatic failure"
            },
            "classes": []
        },
        {
            "id": "hepatocellular injury",
            "group": "nodes",
            "data": {
                "adverseEventId": "hepatocellular injury",
                "id": "hepatocellular injury",
                "meddraCode": "10019837"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "metabolic acidosis",
            "group": "nodes",
            "data": {
                "adverseEventId": "metabolic acidosis",
                "id": "metabolic acidosis",
                "meddraCode": "10027417"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "2198",
            "group": "edges",
            "data": {
                "llr": "2043.2140439224313",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2198",
                "source": "ACETAMINOPHEN",
                "target": "hepatocellular injury"
            },
            "classes": []
        },
        {
            "id": "suicide attempt",
            "group": "nodes",
            "data": {
                "adverseEventId": "suicide attempt",
                "id": "suicide attempt",
                "meddraCode": "10042464"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "poisoning deliberate",
            "group": "nodes",
            "data": {
                "adverseEventId": "poisoning deliberate",
                "id": "poisoning deliberate",
                "meddraCode": "10036000"
            },
            "classes": [
                "adverse event"
            ]
        },
        {
            "id": "101432",
            "group": "edges",
            "data": {
                "actionType": "OPENER",
                "arrow": "vee",
                "action": "TARGETS",
                "id": "101432",
                "source": "ACETAMINOPHEN",
                "target": "TRPV1"
            },
            "classes": [
                "drug_target"
            ]
        },
        {
            "id": "2206",
            "group": "edges",
            "data": {
                "llr": "1140.5485013005673",
                "arrow": "vee",
                "critval": "379.3971731864498",
                "action": "ASSOCIATED_WITH",
                "id": "2206",
                "source": "ACETAMINOPHEN",
                "target": "liver injury"
            },
            "classes": []
        }
    ];

export {dummyNodes as default};