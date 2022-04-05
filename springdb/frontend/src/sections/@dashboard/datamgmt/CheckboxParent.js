import React, { Component } from "react";
import Checkbox from "./Checkbox";
import Button from "@mui/material/Button";

class CheckboxParent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      //  data_array: [],
      //selectedCheckboxes: this.props.selected
    };
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
    console.log("selcb", this.selectedCheckboxes);

    let body = {
      include: true,
      selectedCheckboxes: this.selectedCheckboxes
    };

    for (const checkbox of this.selectedCheckboxes) {
      console.log(checkbox, "is selected.");
    }
    const url = "/api/databases/selecteddata";
    const objectFromUrl = fetch(url, {
      method: "POST",
      body: JSON.stringify(body)
    });
  };

  createCheckbox = label => (
    <Checkbox
      label={label}
      handleCheckboxChange={this.toggleCheckbox}
      key={label}
    />
  );
  // createCheckboxes = () => items.map(this.createCheckbox);

  createCheckboxes = () =>
    this.props.data_array.map((descr, index) => {
      return (
        <Checkbox
          label={descr.description}
          handleCheckboxChange={this.toggleCheckbox}
          key={index}
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

              <Button variant="contained" component="span" type="submit">
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
