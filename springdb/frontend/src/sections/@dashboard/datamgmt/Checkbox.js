import React, { Component } from "react";
import { PropTypes } from "prop-types";

class Checkbox extends Component {
  state = {
    isChecked: false
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
      <div className="checkbox">
        {this.props.enabled ? (
          <label>
            <input
              type="checkbox"
              value={label}
              defaultChecked={!isChecked}
              onChange={this.toggleCheckboxChange}
              dataset={this.props.dataset}
            />
            {label}
          </label>
        ) : (
          <label>
            <input
              type="checkbox"
              value={label}
              onChange={this.toggleCheckboxChange}
            />
            {label}
          </label>
        )}
      </div>
    );
  }
}

Checkbox.propTypes = {
  label: PropTypes.string.isRequired,
  handleCheckboxChange: PropTypes.func.isRequired
};

export default Checkbox;
