import * as React from "react";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
//  import Checkbox from "@mui/material/Checkbox";
import IconButton from "@mui/material/IconButton";
// import CommentIcon from "@mui/icons-material/Comment";

// import { filter } from "lodash";
// import { sentenceCase } from "change-case";
import { useState } from "react";
import { Link as RouterLink } from "react-router-dom";
// material
import {
  Card,
  Table,
  Stack,
  Avatar,
  Button,
  Checkbox,
  TableRow,
  TableBody,
  TableCell,
  Container,
  Typography,
  TableContainer,
  TablePagination
} from "@mui/material";
// components
import Page from "../components/Page";
import Label from "../components/Label";
import Scrollbar from "../components/Scrollbar";
import Iconify from "../components/Iconify";
// import SearchNotFound from "../components/SearchNotFound";
import {
  //DataMgmtHead,
  //  DataMgmtToolbar,
  DataMgmtMenu
} from "../sections/@dashboard/datamgmt";
//

// ----------------------------------------------------------------------

// ----------------------------------------------------------------------
export default function CheckboxList() {
  const [checked, setChecked] = React.useState([0]);

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
