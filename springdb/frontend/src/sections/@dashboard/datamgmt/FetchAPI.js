import React, { useState } from "react";

function FetchAPI() {
  //state = {
  //  loading: true,
  //  data: []
  //};

  const [data, setData] = useState([]);

  async function apiGet() {
    try {
      const url = "/api/databases";
      const res = await fetch(url);
      const json = await res.json();
      console.log("json", json);
    } catch (err) {
      console.error("err", err);
    }
  }

  //render() {
  /*   if (this.state.loading) {
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
 */
  return (
    <div>
      <button onClick={apiGet}> Get List of Data</button>
    </div>
  );
}

export default FetchAPI;
