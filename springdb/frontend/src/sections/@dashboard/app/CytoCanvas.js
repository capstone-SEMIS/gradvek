// material
import { styled } from '@mui/material/styles';
import { Card, Stack, TextField } from '@mui/material';

import cytoscape from 'cytoscape';

import {Component} from 'react';

// ----------------------------------------------------------------------

export default class CytoCanvas extends Component {
    initialElements = [
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
            classes: ["adverse event"],
            data: {
                id: 'Acute hepatic failure',
                meddraCode: '10000804'
            }
        },
        {
            group: "nodes",
            classes: ["adverse event"],
            data: {
                id: 'Toxicity to various agents',
                meddraCode: '10070863'
            }
        },
        {
            group: "nodes",
            classes: ["proteinTarget"],
            data: {
                id: 'Vanilloid receptor',
                parent: "pathway_xyz",
            }
        },
        {
            group: "nodes",
            classes: ["proteinTarget"],
            data: {
                id: 'XYZ receptor',
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
                target: 'Acute hepatic failure',
                arrow: "vee",
                critval: 123
            }
        },
        {
            group: "edges",
            data: {
                id: 'edge_2',
                source: 'Acetaminophen',
                target: 'Toxicity to various agents',
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
                target: 'Vanilloid receptor',
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
    ]
    
    initialStyles = [ // the stylesheet for the graph
            {
                selector: 'node',
                style: {
                    'background-color': '#666',
                    'label': 'data(id)',
                }
            },
            {
                selector: '.pathway',
                style: {
                    "background-color": "red",
                    "opacity": "0.3"
                }
            },
            {
                selector: '.proteinTarget',
                style: {
                    "background-color": "blue",
                    "color": "blue",
                    "opacity": "1"
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
                    "label": "causes",
                }
            },
            
            {
                selector: '.drug_target',
                style: {
                    "label": "data(action)",
                }
            },   
        ]


    canvasStyle = {
        width: '500px',
        height: '500px',
    }

    render(){
        
        return (
                <div id="cyto_canvas" style={this.canvasStyle} ref={el => this.el = el} /> 
        );
    }

    componentDidMount() {
        let cytoInstance = cytoscape({
            elements: this.initialElements,
            container: this.el,
            style: this.initialStyles,

            layout: {
                name: 'circle',
            }
        })
        this.setState({
            cytoInstance: cytoInstance
        })
    }

    componentWillUnmount(){
        this.state.cytoInstance.unmount();
    }
 
}