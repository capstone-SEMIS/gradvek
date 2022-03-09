import cytoscape from 'cytoscape';
import popper from 'cytoscape-popper';


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
        group: "edges",
        data: { 
            id: 'edge_1', 
            source: 'Acetaminophen', 
            target: 'Acute hepatic failure', 
            arrow: "vee",
            critval: 123 
        }
    },
]

function setupCy(){
    cytoscape.use(popper);
    let cy = cytoscape({
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

    cy.on("tap","node", function(hoverEvent) {
        let node = hoverEvent.target;
        let popup = node.popper({
            content: () => {
                let div = document.createElement('div');

                div.innerHTML = "Meddra code: " + node.data("meddraCode");

                document.body.appendChild(div);

                return div;
            },
            popper: {} // my popper options here
        });

        let update = () => {
            popup.update();
        };

        node.on('position', update);

        cy.on('pan zoom resize', update);
    })

    
    
}

export default setupCy;