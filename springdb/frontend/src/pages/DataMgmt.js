// material
import { Typography } from "@mui/material";

// components
import Page from "../components/Page";
import CheckboxList from "../sections/@dashboard/datamgmt/CheckboxList";
import EntityCsvImporter from "../sections/@dashboard/datamgmt/EntityCsvImporter";
import FetchAPI from "../sections/@dashboard/datamgmt/FetchAPI";

export default function DataMgmtPage() {
  return (
    <Page title="Dashboard: Data Management ">
      <Typography variant="h4" sx={{ mb: 5 }}>
        Data Management
      </Typography>

      <EntityCsvImporter />
      <FetchAPI />
      <CheckboxList/>
    </Page>
  );

