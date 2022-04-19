import React, { Component } from "react";
import { PropTypes } from "prop-types";

class Checkbox extends Component {
  state = {
    isChecked: this.props.enabled
  };

  toggleCheckboxChange = () => {
    const { handleCheckboxChange, label } = this.props;

    this.setState(({ isChecked }) => ({
      isChecked: !isChecked
    }));

    handleCheckboxChange(label);
  };

  render() {
    const { label } = this.props;
    const { isChecked } = this.state;

    return (
      <div className="checkbox" style={{ marginBottom: "1rem" }}>
        <label style={{ fontSize: "1.2rem" }}>
          <input
            type="checkbox"
            value={label}
            defaultChecked={isChecked}
            onChange={this.toggleCheckboxChange}
            dataset={this.props.dataset}
            style={{ marginRight: ".5rem" }}
          />
          {label}
        </label>
      </div>
    );
  }
}

Checkbox.propTypes = {
  label: PropTypes.string.isRequired,
  handleCheckboxChange: PropTypes.func.isRequired
};

export default Checkbox;
