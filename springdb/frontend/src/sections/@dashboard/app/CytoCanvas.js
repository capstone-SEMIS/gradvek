import cytoscape from 'cytoscape';

import {Component} from 'react';

// ----------------------------------------------------------------------

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
        let cytoInstance = cytoscape({
            container: this.el,
            style: this.initialStyles,
        });

        this.setState({
            cytoInstance: cytoInstance
        })
    }

    componentWillUnmount(){
        this.state.cytoInstance.unmount();
    }

    componentDidUpdate() {
        // replace all elements
        this.state.cytoInstance.remove("*");
        this.state.cytoInstance.add(this.props.graphNodes);

        // show filtered elements only
        this.state.cytoInstance.elements(this.props.nodeFilter).style("display", "element");

        // lay them out in a circle
        this.state.cytoInstance.layout({name: "circle"}).run();
    }
 
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