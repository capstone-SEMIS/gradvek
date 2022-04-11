import React, { Component } from "react";
import CheckboxParent from "./CheckboxParent";

class DataLoader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data_array: []
    };
  }

  async componentDidMount() {
    const url = "/api/datasets";
    const objectFromUrl = await fetch(url);
    const data = await objectFromUrl.json();
    //console.log("data", data);
    this.setState({
      data_array: data
    });
  }

  render() {
    const { data_array } = this.state;

    return (
      <div>
        {data_array.length > 0 && <CheckboxParent data_array={data_array} />}
      </div>
    );
  }
}

export default DataLoader;
