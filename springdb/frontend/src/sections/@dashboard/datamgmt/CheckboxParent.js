import React, { Component } from "react";
import Checkbox from "./Checkbox";
import Button from "@mui/material/Button";
import axios from "axios";

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

    console.log("newArr", newArr);

    const url = "/api/datasets";

    axios
      .post(url, newArr, {
        headers: {
          "Content-Type": "application/json"
        }
      })
      .then(function(response) {
        console.log(response);
      })
      .catch(function(error) {
        console.log(error);
      });
  };

  CreateCheckboxes = () => {
    console.log("createCheckbox:", this.props.data_array);
    return (
      <>
        {this.props.data_array.map((descr, index) => (
          <Checkbox
            label={descr.description}
            handleCheckboxChange={this.toggleCheckbox}
            key={index}
            enabled={descr.enabled}
            dataset={descr.dataset}
          />
        ))}
      </>
    );
  };

  render() {
    return (
      <div className="container">
        <div className="row">
          <div className="col-sm-12">
            <form onSubmit={this.handleFormSubmit}>
              {/* <this.createCheckboxes()/> */}
              <this.CreateCheckboxes />

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
