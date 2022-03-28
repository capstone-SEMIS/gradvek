import React from "react";

export default class FetchAPI extends React.Component {
  state = {
    loading: true,
    data: []
  };

  async componentDidMount() {
    const response = await fetch("http://localhost:8080/databases");
    const data = await response.json();
    this.setState({ dataset: data.dataset[0], loading: false });
    console.log(data.dataset);
  }

  render() {
    if (this.state.loading) {
      return (
        <div>
          Loading data...
          <br />
        </div>
      );
    }
    if (!this.state.dataset) {
      return <div>didn't get any dataset</div>;
    }

    return (
      <div>
        <div>{this.state.data.dataset}</div>
        <div>{this.state.data.description}</div>
      </div>
    );
  }
}
