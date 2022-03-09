import cytoscape from 'cytoscape';

let initialElements = [
    {
        group: "nodes",
        data: { 
            id: 'Acetaminophen',
            classes:["drug"],
            chembl_code: 'CHEMBL112'
        }
    },
    {
        group: "nodes",
        data: {
            id: 'Acute hepatic failure',
            classes: ["adverse event"],
            meddraCode: '10000804'
        }
    },
    {
        group: "nodes",
        data: {
            id: 'Toxicity to various agents',
            classes: ["adverse event"],
            meddraCode: '10070863'
        }
    },
    { 
        data: { id: 'edge_1', source: 'Acetaminophen', target: 'Acute hepatic failure', arrow: "vee" }
    }
]

function setupCy(){
    cytoscape({
        container: document.getElementById('CytoScape-canvas'), // container to render in

        elements: initialElements,

        style: [ // the stylesheet for the graph
            {
                selector: 'node',
                style: {
                    'background-color': '#666',
                    'label': 'data(id)'
                }
            },

            {
                selector: 'edge',
                style: {
                    'width': 3,
                    'line-color': '#ccc',
                    'target-arrow-color': '#ccc',
                    'target-arrow-shape': 'triangle',
                    'curve-style': 'bezier',
                    "label": "causes"
                }
            }
        ],

        layout: {
            name: 'grid',
            rows: 2
        }

    });
}

export default setupCy;