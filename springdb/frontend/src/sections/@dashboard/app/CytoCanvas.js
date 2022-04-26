import cytoscape from "cytoscape";

import {Component} from "react";

// ----------------------------------------------------------------------

// https://jfly.uni-koeln.de/color/
const pathwayColor = "rgb(204, 121, 167)"; // orange
const targetColor = "rgb(0, 0, 0)"; // black
const drugColor = "rgb(0, 114, 178)";  // blue
const adverseEventColor = "rgb(213, 84, 0)"; // vermilion

export default class CytoCanvas extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <div
        id="cyto_canvas"
        style={this.canvasStyle}
        ref={el => (this.el = el)}
      />
    );
  }

  componentDidMount() {
    // on initial mount, create a new cytoscape canvas
    // and hook that canvas up to this element's div
    let cytoInstance = cytoscape({
      container: this.el,
      style: this.initialStyles
    });

    this.setState({
      cytoInstance: cytoInstance
    });
  }

  componentWillUnmount() {
    // when this React component is unmounted, also unmount the Cytoscape canvas
    this.state.cytoInstance.unmount();
    this.state.cytoInstance.destroy();
  }

  componentDidUpdate() {
    // replace all elements with the current graphNodes
    this.state.cytoInstance.remove("*");
    this.state.cytoInstance.add(this.props.graphNodes);

    if ("AE" in this.props.focusNode) {
      let nodeToFocus = this.state.cytoInstance.elements(
        `node#${this.props.focusNode.AE}`
      );
      let neighbouringNodes = nodeToFocus.closedNeighborhood();

      // show neighbouring elements only
      neighbouringNodes.style("display", "element");
      neighbouringNodes.layout({ name: "breadthfirst" }).run();
      // neighbouringNodes.layout({name:"circle"}).run();
      this.state.cytoInstance.fit(neighbouringNodes);
    } else {
      // no focusNode, so show all nodes
      this.state.cytoInstance
        .elements(this.props.nodeFilter)
        .style("display", "element");
      // lay all elements out hierarchically from pathway on left to adverse event on right
      this.state.cytoInstance.layout({
        name: "breadthfirst",
        roots: '.pathway',
        transform: (node, position) => { return {x: position.y, y: position.x}}
      }).run();
    }
  }

  initialStyles = [
    // the stylesheet for the graph
    {
      selector: "node",
      style: {
        "background-color": "#8b786d",
        color: "#8b786d",
        label: "data(name)",
        "text-valign": "center",
        "text-outline-color": "white",
        "text-outline-width": "2px"
      }
    },
    {
      selector: ".pathway",
      style: {
        "background-color": pathwayColor,
        "color": pathwayColor
      }
    },
    {
      selector: ".target",
      style: {
        "background-color": targetColor,
        "color": targetColor
      }
    },
    {
      selector: ".drug",
      style: {
        "background-color": drugColor,
        "color": drugColor
      }
    },
    {
      selector: ".adverse-event",
      style: {
        "background-color": adverseEventColor,
        "color": adverseEventColor
      }
    },
    {
      selector: "edge",
      style: {
        width: 3,
        "target-arrow-shape": "triangle",
        "curve-style": "bezier",
      }
    },
    {
      selector: "edge[action = 'ASSOCIATED WITH']",
      style: {
        "label": "data(llr)",
        "color": adverseEventColor,
        "line-color": adverseEventColor,
        "target-arrow-color": adverseEventColor
      }
    },
    {
      selector: "edge[action = 'TARGETS']",
      style: {
        "label": "data(actionType)",
        "color": drugColor,
        "line-color": drugColor,
        "target-arrow-color": drugColor
      }
    },
    {
      selector: "edge[action = 'PARTICIPATES_IN']",
      style: {
        "line-color": pathwayColor,
        "target-arrow-color": pathwayColor
      }
    },
    {
      selector: "*",
      style: {
        display: "none"
      }
    }
  ];

  canvasStyle = {
    width: "100%",
    minHeight: "300px"
  };
}
