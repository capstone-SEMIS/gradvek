import React, { Component } from "react";
import Checkbox from "./Checkbox";
import Button from "@mui/material/Button";

class CheckboxParent extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentWillMount = () => {
    this.selectedCheckboxes = new Set();
  };

  toggleCheckbox = label => {
    if (this.selectedCheckboxes.has(label)) {
      this.selectedCheckboxes.delete(label);
    } else {
      this.selectedCheckboxes.add(label);
    }
  };

  handleFormSubmit = formSubmitEvent => {
    formSubmitEvent.preventDefault();

    const myselected = document.querySelectorAll("input[type=checkbox]");
    const newArr = [];
    myselected.forEach(item => {
      newArr.push({
        dataset: item.getAttribute("dataset"),
        enabled: item.checked
      });
    });

    console.log(newArr);

    const url = "/api/datasets";
    const datapost = fetch(url, {
      method: "POST",
      body: JSON.stringify(newArr)
    });
    console.log("datapost", datapost);
  };

  createCheckboxes = () =>
    this.props.data_array.map((descr, index) => {
      return (
        <Checkbox
          label={descr.description}
          handleCheckboxChange={this.toggleCheckbox}
          key={index}
          enabled={descr.enabled}
          dataset={descr.dataset}
        />
      );
    });

  render() {
    return (
      <div className="container">
        <div className="row">
          <div className="col-sm-12">
            <form onSubmit={this.handleFormSubmit}>
              {this.createCheckboxes()}

              <Button variant="contained" type="submit">
                Update Datasets
              </Button>
            </form>
          </div>
        </div>
      </div>
    );
  }
}

export default CheckboxParent;
