// material
import {Typography} from "@mui/material";

// components
import Page from "../components/Page";
<<<<<<< HEAD

// ----------------------------------------------------------------------
// React function for CheckboxList. This has hardcoded
// list of data sets. In future version, it will dynamically read from the backend.
// ----------------------------------------------------------------------
export default function CheckboxList() {
  const [checked, setChecked] = React.useState([]);

  const handleToggle = value => () => {
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    setChecked(newChecked);
  };

  return (
    <Page title="Dashboard: Data Management ">
      <Typography variant="h4" sx={{ mb: 5 }}>
        Data Management
      </Typography>

      <List sx={{ width: "100%", maxWidth: 360, bgcolor: "background.paper" }}>
        {[
          "Core Annotation",
          "Moleculare Interactions",
          "Baseline Expression",
          "Mouse Phenotype"
        ].map(value => {
          const labelId = `checkbox-list-label-${value}`;

          return (
            <ListItem
              key={value}
              secondaryAction={
                <IconButton edge="end" aria-label="comments"></IconButton>
              }
              disablePadding
            >
              <ListItemButton
                role={undefined}
                onClick={handleToggle(value)}
                dense
              >
                <ListItemIcon>
                  <Checkbox
                    edge="start"
                    checked={checked.indexOf(value) !== -1}
                    tabIndex={-1}
                    disableRipple
                    inputProps={{ "aria-labelledby": labelId }}
                  />
                </ListItemIcon>
                <ListItemText id={labelId} primary={`Target ${value}`} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
    </Page>
  );
=======
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
>>>>>>> 39fb90d93b406b1962954db9eac5e57eccfc51e6
}
