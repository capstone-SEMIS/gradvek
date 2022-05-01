import cytoscape from "cytoscape";

import {Component} from "react";

// https://davidmathlogic.com/colorblind
const pathwayColor = "#D81B60";
const targetColor = "#FFC107";
const drugColor = "#1E88E5";
const adverseEventColor = "#004D40";

export default class CytoLegendCanvas extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    return (
      <div
        id={this.props.id}
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
      style: this.initialStyles,
      wheelSensitivity: 0.3,
      zoomingEnabled: false,
      panningEnabled: false,
      boxSelectionEnabled: false
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

    this.state.cytoInstance
        .elements("*")
        .style("display", "element");

    this.state.cytoInstance.layout({
      name: "grid",
      rows: 1,
      nodeDimensionsIncludeLabels: true
    }).run();
  }

  initialStyles = [
    // the stylesheet for the graph
    {
      selector: "node",
      style: {
        "label": "data(name)",
      }
    },
    {
      selector: ".pathway",
      style: {
        "background-color": pathwayColor,
        "shape": "triangle"
      }
    },
    {
      selector: ".target",
      style: {
        "background-color": targetColor,
      }
    },
    {
      selector: ".drug",
      style: {
        "background-color": drugColor,
        "shape": "rectangle"
      }
    },
    {
      selector: ".adverse-event",
      style: {
        "background-color": adverseEventColor,
        "shape": "diamond"
      }
    },
    {
      selector: ".targets",
      style: {
        "label": "data(actionType)",
        "line-color": drugColor,
        "target-arrow-color": drugColor
      }
    },
    {
      selector: "*",
      style: {
        "display": "none",
        "text-outline-color": "white",
        "text-outline-width": "2px",
        "text-valign": "bottom",
        "text-halign": "center",
        "color": "black",
        "min-zoomed-font-size": "10",
      }
    }
  ];

  canvasStyle = {
    width: "100%",
    maxHeight: "75px",
    height: "4em"
  };
}
