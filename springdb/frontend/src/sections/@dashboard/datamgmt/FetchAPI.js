import React, { useState } from "react";
import CheckboxList from "./CheckboxList";
import Child from "./Child";

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
      var data = await res.text();
      //const parsed = JSON.parse(res);
      // const json = await res.json();
      //data = data.replace("[", "");
      //data = data.replace("]", "");
      // data = "[" + data.slice(2, -2) + "]";
      data =
        '{"dataset":"Targets", "description":"Core annotation for targets", "source":"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets", "timestamp":1647831895}';
      //      dataset: "Adverse Events",
      //       description: "Significant adverse events for drug molecules",
      //      source:
      //       "ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/fda/significantAdverseDrugReactions",
      //    timestamp: 1647831895
      //  }

      //data = data.substring(1);
      //data = data.slice(0, -1);
      //const json = JSON.stringify(data);
      var listItems = JSON.parse(data);
      console.log(listItems.description);
      const parentToChild = () => {
        setData(JSON.parse(data).description);
      };
      console.log(listItems.timestamp);
      //console.log(data);
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
      <Child parentToChild={data} />
      <button onClick={apiGet}> Get List of Data</button>
    </div>
  );
}

export default FetchAPI;
