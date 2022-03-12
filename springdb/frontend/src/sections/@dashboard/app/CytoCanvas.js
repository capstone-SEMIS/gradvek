import cytoscape from 'cytoscape';

import {Component} from 'react';

// ----------------------------------------------------------------------

export default class CytoCanvas extends Component {
    
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
            elements: this.props.graphNodes,
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