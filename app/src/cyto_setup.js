import cytoscape from 'cytoscape';

let initialElements = [
    { // node a
        data: { id: 'a' }
    },
    { // node b
        data: { id: 'b' }
    },
    { // edge ab
        data: { id: 'ab', source: 'a', target: 'b' }
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
                    'curve-style': 'bezier'
                }
            }
        ],

        layout: {
            name: 'grid',
            rows: 1
        }

    });
}

export default setupCy;