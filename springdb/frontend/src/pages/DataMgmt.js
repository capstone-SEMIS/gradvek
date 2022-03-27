// material
import {Typography} from "@mui/material";

// components
import Page from "../components/Page";
import CheckboxList from "../sections/@dashboard/datamgmt/CheckboxList";
import EntityCsvImporter from "../sections/@dashboard/datamgmt/EntityCsvImporter";

export default function DataMgmtPage() {
    return (
        <Page title="Dashboard: Data Management ">
            <Typography variant="h4" sx={{mb: 5}}>
                Data Management
            </Typography>

            <EntityCsvImporter/>
            <CheckboxList/>
        </Page>
    );
}
