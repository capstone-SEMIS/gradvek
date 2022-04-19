import React, { Component } from "react";
import Checkbox from "./Checkbox";
import Button from "@mui/material/Button";
import axios from "axios";
import toast from "react-hot-toast";

class CheckboxParent extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount = () => {
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
        if (response.status === 200) {
          toast.success("Successfully Updated!");
        } else toast.error("Update FAILED!");
      })
      .catch(function(error) {
        console.log(error);
        toast.error("Error with Request!");
      });
  };

  CreateCheckboxes = () => {
    console.log("createCheckbox:", this.props.dataArray);
    return (
      <>
        {this.props.dataArray.map((descr, index) => (
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
