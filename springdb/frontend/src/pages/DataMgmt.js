// material
import { Typography } from "@mui/material";

// components
import Page from "../components/Page";
import DataLoader from "../sections/@dashboard/datamgmt/DataLoader";
import CsvImporter from "../sections/@dashboard/datamgmt/CsvImporter";

export default function DataMgmtPage() {
  return (
    <Page title="Dashboard: Data Management ">
      <Typography variant="h4" sx={{ mb: 5 }}>
        Data Management
      </Typography>

      <Typography variant="h6" sx={{ mb: 0 }}>
        Import CSV Data into Gradvek:
      </Typography>
      <CsvImporter />
      <br />
      <br />
      <Typography variant="h6" sx={{ mb: 0 }}>
        Datasets from Backend (Include/Exclude):
      </Typography>
      <DataLoader />
    </Page>
  );
}
