import cytoscape from 'cytoscape';
import avsdf from 'cytoscape-avsdf';

import {Component} from 'react';

// ----------------------------------------------------------------------
cytoscape.use( avsdf );
export default class CytoCanvas extends Component {
    constructor(props) {
        super(props);
        this.state = {};
    }
    
    render(){
        return (
                <div id="cyto_canvas" style={this.canvasStyle} ref={el => this.el = el} /> 
        );
    }

    componentDidMount() {
        // on initial mount, create a new cytoscape canvas
        // and hook that canvas up to this element's div
        let cytoInstance = cytoscape({
            container: this.el,
            style: this.initialStyles,
        });

        this.setState({
            cytoInstance: cytoInstance
        })
    }

    componentWillUnmount(){
        // when this React component is unmounted, also unmount the Cytoscape canvas
        this.state.cytoInstance.unmount();
        this.state.cytoInstance.destroy();
    }

    componentDidUpdate() {
        // replace all elements with the current graphNodes
        this.state.cytoInstance.remove("*");
        this.state.cytoInstance.add(this.props.graphNodes); 

        if ('AE' in this.props.focusNode) {
            let nodeToFocus = this.state.cytoInstance.elements(`node#${this.props.focusNode.AE}`)
            let neighbouringNodes = nodeToFocus.closedNeighborhood();

            // show neighbouring elements only
            neighbouringNodes.style("display", "element");
            neighbouringNodes.layout({name:"breadthfirst"}).run();
            this.state.cytoInstance.fit(neighbouringNodes);
        }
        else {
            // no focusNode, so show all nodes
            this.state.cytoInstance.elements(this.props.nodeFilter).style("display", "element");
            // lay all elements out in a circle
            this.state.cytoInstance.layout({ name: "avsdf" }).run();
        }
    }
 
    initialStyles = [ // the stylesheet for the graph
        {
            selector: 'node',
            style: {
                'background-color': '#8b786d',
                "color": "#8b786d",
                'label': 'data(id)',
            }
        },
        {
            selector: '.pathway[:compound]',
            style: {
                "background-color": "#78a1bb",
                "background-opacity": "0.15"
            }
        },
        {
            selector: '.target',
            style: {
                "background-color": "#78a1bb",
                "color": "#78a1bb",
                "opacity": "1"
            }
        },
        {
            selector: '.drug',
            style: {
                "background-color": "lightcoral",
                "color": "lightcoral",
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
                "label": "data(action)"
            }
        },

        // {
        //     selector: '.drug_target',
        //     style: {
        //         "label": "data(action)",
        //     }
        // },
        {
            selector: '*',
            style: {
                "display": "none"
            }
        }
    ]

    canvasStyle = {
        width: '100%',
        minHeight: '300px'
    }
}