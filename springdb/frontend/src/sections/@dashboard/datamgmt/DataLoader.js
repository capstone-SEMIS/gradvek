import React, { Component } from "react";
import CheckboxParent from "./CheckboxParent";

class DataLoader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dataArray: props.dataArray
    };
  }

  // async componentDidMount() {
  //   const url = "/api/datasets";
  //   const objectFromUrl = await fetch(url);
  //   const data = await objectFromUrl.json();
  //   console.log("dataloader", data);
  //   this.setState({
  //     data_array: data
  //   });
  // }

  render() {
    const { dataArray } = this.state;

    return (
      <div>
        {dataArray.length > 0 && <CheckboxParent dataArray={dataArray} />}
      </div>
    );
  }
}

export default DataLoader;
