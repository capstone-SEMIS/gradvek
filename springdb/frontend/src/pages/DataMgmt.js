// import List components
import * as React from "react";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import IconButton from "@mui/material/IconButton";

// material
import { Checkbox, Typography } from "@mui/material";

// components
import Page from "../components/Page";

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
        Data Managment
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
}
