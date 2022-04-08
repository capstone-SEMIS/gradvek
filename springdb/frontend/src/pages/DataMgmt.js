// material
import {Typography} from "@mui/material";

// components
import Page from "../components/Page";
import CheckboxList from "../sections/@dashboard/datamgmt/CheckboxList";
import CsvImporter from "../sections/@dashboard/datamgmt/CsvImporter";

export default function DataMgmtPage() {
    return (
        <Page title="Dashboard: Data Management ">
            <Typography variant="h4" sx={{mb: 5}}>
                Data Management
            </Typography>

            <CsvImporter/>
            <CheckboxList/>
        </Page>
    );
}
