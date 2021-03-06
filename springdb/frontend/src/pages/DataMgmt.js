// material
import { Typography, Box } from "@mui/material";

// components
import Page from "../components/Page";
import CsvImporter from "../sections/@dashboard/datamgmt/CsvImporter";
import { useState, useEffect } from "react";
import CheckboxParent from "../sections/@dashboard/datamgmt/CheckboxParent";

export default function DataMgmtPage() {
  const [dataArray, setDataArray] = useState([]);

  const fetchData = async () => {
    const url = "/api/datasets";
    const objectFromUrl = await fetch(url);
    const data = await objectFromUrl.json();

    data.sort((a, b) => (a.dataset > b.dataset) ? 1 : ((b.dataset > a.dataset) ? -1 : 0));
    setDataArray(data);
  };

  useEffect(() => {
    fetchData();
  }, []);

  return (
    <Page title="Dashboard: Data Management ">
      <Box sx={{ ml: 8 }}>
        <Typography variant="h2" sx={{ mb: 5 }}>
          Data Management
        </Typography>

        <Typography variant="h4" sx={{ mb: 2 }}>
          Import CSV Data into Gradvek:
        </Typography>
        <CsvImporter fetchData={fetchData} />
        <br />
        <br />
        <Typography variant="h4" sx={{ mb: 2 }}>
          Datasets from Backend (Include/Exclude):
        </Typography>
        <CheckboxParent dataArray={dataArray} />
      </Box>
    </Page>
  );
}
